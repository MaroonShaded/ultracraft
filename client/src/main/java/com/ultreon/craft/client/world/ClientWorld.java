package com.ultreon.craft.client.world;

import com.badlogic.gdx.utils.Disposable;
import com.ultreon.craft.CommonConstants;
import com.ultreon.craft.block.Blocks;
import com.ultreon.craft.client.UltracraftClient;
import com.ultreon.craft.client.player.LocalPlayer;
import com.ultreon.craft.entity.Player;
import com.ultreon.craft.network.packets.c2s.C2SBlockBreakPacket;
import com.ultreon.craft.network.packets.c2s.C2SBlockBreakingPacket;
import com.ultreon.craft.network.packets.c2s.C2SChunkStatusPacket;
import com.ultreon.craft.util.InvalidThreadException;
import com.ultreon.craft.world.*;
import com.ultreon.libs.commons.v0.vector.Vec2d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientWorld extends World implements Disposable {
    @NotNull
    private final UltracraftClient client;
    private final Map<ChunkPos, ClientChunk> chunks = new ConcurrentHashMap<>();
    private int chunkRefresh;
    private ChunkPos oldChunkPos = new ChunkPos(0, 0);

    public ClientWorld(@NotNull UltracraftClient client) {
        super();
        this.client = client;
    }

    public int getRenderDistance() {
        return this.client.settings.renderDistance.get();
    }

    @Override
    protected boolean unloadChunk(@NotNull Chunk chunk, @NotNull ChunkPos pos) {
        return this.chunks.remove(pos) == chunk;
    }

    @Override
    protected void checkThread() {
        if (!UltracraftClient.isOnMainThread())
            throw new InvalidThreadException("Should be on client main thread.");
    }

    @Override
    public @Nullable ClientChunk getChunk(@NotNull ChunkPos pos) {
        if (!UltracraftClient.isOnMainThread()) {
            return UltracraftClient.invokeAndWait(() -> this.getChunk(pos));
        }
        return this.chunks.get(pos);
    }

    @Override
    public @Nullable ClientChunk getChunkAt(@NotNull BlockPos pos) {
        return (ClientChunk) super.getChunkAt(pos);
    }

    @Override
    public @Nullable ClientChunk getChunkAt(int x, int y, int z) {
        return (ClientChunk) super.getChunkAt(x, y, z);
    }

    @Override
    public Collection<ClientChunk> getLoadedChunks() {
        return this.chunks.values();
    }

    @Override
    public boolean isChunkInvalidated(@NotNull Chunk chunk) {
        if (!UltracraftClient.isOnMainThread()) {
            throw new InvalidThreadException(CommonConstants.EX_NOT_ON_RENDER_THREAD);
        }

        return super.isChunkInvalidated(chunk);
    }

    @Override
    public void startBreaking(@NotNull BlockPos breaking, @NotNull Player breaker) {
        if (breaker == this.client.player) {
            this.client.connection.send(new C2SBlockBreakingPacket(breaking, C2SBlockBreakingPacket.BlockStatus.START));
        }
        super.startBreaking(breaking, breaker);
    }

    @Override
    public BreakResult continueBreaking(@NotNull BlockPos breaking, float amount, @NotNull Player breaker) {
        if (breaker == this.client.player) {
            this.client.connection.send(new C2SBlockBreakingPacket(breaking, C2SBlockBreakingPacket.BlockStatus.CONTINUE));
        }
        BreakResult breakResult = super.continueBreaking(breaking, amount, breaker);
        if (breakResult == BreakResult.BROKEN) {
            this.client.connection.send(new C2SBlockBreakingPacket(breaking, C2SBlockBreakingPacket.BlockStatus.STOP));
            this.client.connection.send(new C2SBlockBreakPacket(breaking));
            this.set(breaking, Blocks.AIR);
        }
        return breakResult;
    }

    @Override
    public void stopBreaking(@NotNull BlockPos breaking, @NotNull Player breaker) {
        if (breaker == this.client.player) {
            this.client.connection.send(new C2SBlockBreakingPacket(breaking, C2SBlockBreakingPacket.BlockStatus.STOP));
        }
        super.stopBreaking(breaking, breaker);
    }

    @Override
    public void onChunkUpdated(@NotNull Chunk chunk) {
        if (!UltracraftClient.isOnMainThread()) {
            throw new InvalidThreadException(CommonConstants.EX_NOT_ON_RENDER_THREAD);
        }

        super.onChunkUpdated(chunk);
    }

    @Override
    public void playSound(@NotNull SoundEvent sound, double x, double y, double z) {
        float range = sound.getRange();
        Player player = this.client.player;
        if (player != null) {
            player.playSound(sound, (float) ((range - player.getPosition().dst(x, y, z)) / range));
        }
    }

    @Override
    public boolean isClientSide() {
        return true;
    }

    public Map<ChunkPos, ClientChunk> getChunks() {
        return this.chunks;
    }

    public void loadChunk(ChunkPos pos, ClientChunk data) {
        var chunk = UltracraftClient.invokeAndWait(() -> this.chunks.get(pos));
        if (chunk == null) chunk = data;
        else {
            World.LOGGER.warn("Duplicate chunk packet detected! Chunk {}", pos);
            return;
        }
        LocalPlayer player = this.client.player;
        if (player == null || new Vec2d(pos.x(), pos.z()).dst(new Vec2d(player.getChunkPos().x(), player.getChunkPos().z())) > this.client.settings.renderDistance.get()) {
            this.client.connection.send(new C2SChunkStatusPacket(pos, Chunk.Status.FAILED));
            return;
        }

        UltracraftClient.invoke(chunk::ready);

        this.chunks.put(pos, data);

        this.client.connection.send(new C2SChunkStatusPacket(pos, Chunk.Status.SUCCESS));
    }

    public void tick() {
        if (this.chunkRefresh-- <= 0) {
            this.chunkRefresh = 40;

            LocalPlayer player = this.client.player;
            if (player != null) {
                if (this.oldChunkPos.equals(player.getChunkPos())) return;
                this.oldChunkPos = player.getChunkPos();
                this.chunks.forEach((chunkPos, clientChunk) -> {
                    if (new Vec2d(chunkPos.x(), chunkPos.z()).dst(player.getChunkPos().x(), player.getChunkPos().z()) > this.client.settings.renderDistance.get()) {
                        this.chunks.remove(chunkPos);
                        clientChunk.dispose();

                        this.client.connection.send(new C2SChunkStatusPacket(chunkPos, Chunk.Status.UNLOADED));
                    }
                });
            }
        }
    }
}
