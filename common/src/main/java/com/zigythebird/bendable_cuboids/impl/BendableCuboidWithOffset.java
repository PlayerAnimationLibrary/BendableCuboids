package com.zigythebird.bendable_cuboids.impl;

import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.function.Function;

public class BendableCuboidWithOffset extends BendableCuboid {
    final float offsetX;
    final float offsetY;
    final float offsetZ;

    public BendableCuboidWithOffset(Quad[] sides, RememberingPos[] positions, float fixX, float fixY, float fixZ, Direction direction, Plane basePlane, Plane otherPlane, float fullSize, float offsetX, float offsetY, float offsetZ) {
        super(sides, positions, fixX, fixY, fixZ, direction, basePlane, otherPlane, fullSize);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public void iteratePositions(Function<Vector3f, Vector3f> function) {
        for(RememberingPos pos: positions){
            pos.setPos(function.apply(pos.getOriginalPos().add(offsetX, offsetY, offsetZ)).sub(offsetX, offsetY, offsetZ));
        }
    }
}
