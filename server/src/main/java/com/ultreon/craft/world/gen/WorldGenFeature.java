package com.ultreon.craft.world.gen;

import com.badlogic.gdx.utils.Disposable;
import com.ultreon.craft.world.ChunkAccess;
import com.ultreon.craft.world.ServerWorld;
import com.ultreon.craft.world.World;
import org.jetbrains.annotations.ApiStatus;

/**
 * A terrain layer that handles world generation.
 *
 * @author <a href="https://github.com/XyperCode">XyperCode</a>
 * @since 0.1.0
 */
public abstract class WorldGenFeature implements Disposable {
    /**
     * Set blocks for the building of a chunk.
     * It handles world generation of a single terrain layer.
     *
     * @param world the world to build the terrain in.
     * @param chunk the chunk that is being built.
     * @param x the current X coordinate of the column being built.
     * @param z the current Z coordinate of the column being built.
     * @param height the terrain height generated by the noise generator.
     * @return true to finish the column building, false to continue.
     */
    @ApiStatus.OverrideOnly
    public abstract boolean handle(World world, ChunkAccess chunk, int x, int z, int height);

    /**
     * Create the world generator feature in the given world.
     * <p>NOTE: Always override {@link #dispose()} to avoid memory leaks.</p>
     *
     * @param world the world to create the feature in.
     */
    @ApiStatus.OverrideOnly
    public void create(ServerWorld world) {

    }

    /**
     * Dispose the feature when the world is being unloaded.
     */
    @Override
    @ApiStatus.OverrideOnly
    public void dispose() {

    }
}