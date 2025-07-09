package com.zigythebird.bendable_cuboids.impl;

import net.minecraft.core.Direction;

import java.util.Set;

public class BendableCuboidData {
    /**
     * Size parameters
     */
    public float x, y, z, sizeX, sizeY, sizeZ;
    public float extraX, extraY, extraZ;
    public int u, v;
    public boolean mirror = false;
    public float textureWidth, textureHeight; //That will be int
    public int pivot;
    //public float bendX, bendY, bendZ;
    public Set<Direction> visibleFaces;

    public BendableCuboidData() {}

    public BendableCuboidData(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> visibleFaces, int pivot) {
        this.u = u;
        this.v = v;
        this.x = x;
        this.y = y;
        this.z = z;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.extraX = extraX;
        this.extraY = extraY;
        this.extraZ = extraZ;
        this.mirror = mirror;
        this.pivot = pivot;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.visibleFaces = visibleFaces;
    }

    public BendableCuboidData(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> visibleFaces) {
        this(u, v, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, textureWidth, textureHeight, visibleFaces, -1);
    }
}
