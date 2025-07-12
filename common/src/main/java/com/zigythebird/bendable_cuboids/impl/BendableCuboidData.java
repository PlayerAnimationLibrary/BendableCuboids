package com.zigythebird.bendable_cuboids.impl;

import net.minecraft.core.Direction;

import java.util.Set;

public record BendableCuboidData(
        int u, int v,
        float sizeX, float sizeY, float sizeZ,
        float extraX, float extraY, float extraZ,
        boolean mirror,
        float textureWidth, float textureHeight,
        Set<Direction> visibleFaces
) {
}
