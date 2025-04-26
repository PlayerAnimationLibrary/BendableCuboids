package com.zigythebird.bendable_cuboids.impl.compatibility.geckolib;

import com.zigythebird.bendable_cuboids.impl.*;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

public class GeckoLibBendableCuboid extends BendableCuboidWithOffset {
    public GeckoLibBendableCuboid(Quad[] sides, RememberingPos[] positions, float fixX, float fixY, float fixZ, Direction direction, Plane basePlane, Plane otherPlane, float fullSize, float offsetX, float offsetY, float offsetZ) {
        super(sides, positions, fixX, fixY, fixZ, direction, basePlane, otherPlane, fullSize, offsetX, offsetY, offsetZ);
    }
    
    @Override
    public Matrix4f applyBend(float bendAxis, float bendValue) {
        this.bend = bendValue; this.bendAxis = bendAxis;
        BendApplier bendApplier = BendUtil.getBend(this.getBendDirection(), this.getBendX(), this.getBendY(), this.getBendZ(),
                this.basePlane, this.otherPlane, this.isBendInverted(), true, this.bendHeight(), bendAxis, bendValue);
        this.iteratePositions(bendApplier.consumer());
        return bendApplier.matrix4f();
    }
}
