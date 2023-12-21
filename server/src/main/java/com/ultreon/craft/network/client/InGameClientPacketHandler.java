package com.ultreon.craft.network.client;

import com.ultreon.craft.block.Block;
import com.ultreon.craft.collection.PaletteStorage;
import com.ultreon.craft.item.ItemStack;
import com.ultreon.craft.network.NetworkChannel;
import com.ultreon.craft.network.PacketContext;
import com.ultreon.craft.network.api.packet.ModPacket;
import com.ultreon.craft.network.packets.AbilitiesPacket;
import com.ultreon.craft.network.packets.AddPermissionPacket;
import com.ultreon.craft.network.packets.InitialPermissionsPacket;
import com.ultreon.craft.network.packets.RemovePermissionPacket;
import com.ultreon.craft.network.packets.s2c.S2CPlayerHurtPacket;
import com.ultreon.craft.text.TextObject;
import com.ultreon.craft.util.Gamemode;
import com.ultreon.craft.world.*;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.vector.Vec3d;

import java.util.UUID;

public interface InGameClientPacketHandler extends ClientPacketHandler {
    void onModPacket(NetworkChannel channel, ModPacket<?> packet);

    NetworkChannel getChannel(Identifier channelId);

    void onPlayerHealth(float newHealth);

    void onRespawn(Vec3d pos);

    void onPlayerSetPos(Vec3d pos);

    void onChunkCancel(ChunkPos pos);

    void onChunkData(ChunkPos pos, PaletteStorage<Biome> biomes);

    void onSectionData(SectionPos pos, long[] data, Block[] palette);

    void onPlayerPosition(PacketContext ctx, UUID player, Vec3d pos);

    void onKeepAlive();

    void onPlaySound(Identifier sound, float volume);

    void onAddPlayer(UUID uuid, String name, Vec3d position);

    void onRemovePlayer(UUID u);

    void onBlockSet(BlockPos pos, Identifier block);

    void onMenuItemChanged(int index, ItemStack stack);

    void onInventoryItemChanged(int index, ItemStack stack);

    void onMenuCursorChanged(ItemStack cursor);

    void onOpenContainerMenu(Identifier menuType);

    void onAddPermission(AddPermissionPacket packet);

    void onRemovePermission(RemovePermissionPacket packet);

    void onInitialPermissions(InitialPermissionsPacket packet);

    void onChatReceived(TextObject message);

    void onTabCompleteResult(String[] options);

    void onAbilities(AbilitiesPacket packet);

    void onPlayerHurt(S2CPlayerHurtPacket s2CPlayerHurtPacket);

    void onPing(long serverTime, long time);

    void onGamemode(Gamemode gamemode);
}
