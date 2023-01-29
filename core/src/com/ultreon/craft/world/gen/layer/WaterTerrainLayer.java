package com.ultreon.craft.world.gen.layer;

import com.ultreon.craft.block.Blocks;
import com.ultreon.craft.world.Chunk;

public class WaterTerrainLayer extends TerrainLayer {
    private final int waterLevel;

    public WaterTerrainLayer() {
        this(64);
    }

    public WaterTerrainLayer(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    @Override
    public boolean handle(Chunk chunk, int x, int y, int z, int height, long seed) {
        if (y > height && y <= waterLevel) {
            chunk.set(x, y, z, Blocks.WATER.get());
            if (y == height + 1) {
                chunk.set(x, height, z, Blocks.SAND.get());
            }
            return true;
        }
        return false;
    }
}
