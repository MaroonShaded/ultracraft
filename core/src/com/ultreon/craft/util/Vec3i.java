package com.ultreon.craft.util;

import com.badlogic.gdx.math.Vector3;

import java.util.Objects;

public class Vec3i {
    protected int x;
    protected int y;
    protected int z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i(Vec3i vec3i) {
        this.x = vec3i.x;
        this.y = vec3i.y;
        this.z = vec3i.z;
    }

    public static Vec3i of(Vector3 gdx) {
        return new Vec3i((int) gdx.x, (int) gdx.y, (int) gdx.z);
    }

    public Vec3i add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vec3i sub(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vec3i mul(int x, int y, int z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Vec3i div(int x, int y, int z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    public Vec3i rem(int x, int y, int z) {
        this.x %= x;
        this.y %= y;
        this.z %= z;
        return this;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "BlockPos[" +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "z=" + z + ']';
    }

    public Vector3 gdx() {
        return new Vector3(x, y, z);
    }

    public void x(int x) {
        this.x = x;
    }

    public void y(int y) {
        this.y = y;
    }

    public void z(int z) {
        this.z = z;
    }
}
