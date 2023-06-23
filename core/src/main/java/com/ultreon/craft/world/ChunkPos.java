package com.ultreon.craft.world;

import com.badlogic.gdx.math.Vector3;

import java.util.Objects;

public final class ChunkPos {
    private final int x;
    private final int z;

    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int x() {
        return x;
    }

    public int z() {
        return z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ChunkPos chunkPos = (ChunkPos) o;
        return this.x == chunkPos.x && this.z == chunkPos.z;
    }

    @Override
    public String toString() {
        return this.x + "," + this.z;
    }

    public static RegionPos parse(String s) {
        String[] split = s.split(",", 2);
        Integer x = parseInt(split[0]);
        Integer z = parseInt(split[1]);
        if (x == null) return null;
        if (z == null) return null;
        return new RegionPos(x, z);
    }

    private static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Vector3 getChunkOrigin() {
        return new Vector3(this.x * World.CHUNK_SIZE, World.WORLD_DEPTH, this.z * World.CHUNK_SIZE);
    }
}
