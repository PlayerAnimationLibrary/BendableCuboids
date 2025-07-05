package com.zigythebird.bendable_cuboids.impl.compatibility.geckolib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zigythebird.bendable_cuboids.impl.*;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GeckoLibBendableCuboid extends BendableCuboid {
    public GeckoLibBendableCuboid(Quad[] sides, RememberingPos[] positions, float fixX, float fixY, float fixZ, Direction direction, Plane basePlane, Plane otherPlane, float fullSize) {
        super(sides, positions, fixX, fixY, fixZ, direction, basePlane, otherPlane, fullSize);
    }

    public Matrix4f applyBend(float bendValue, PoseStack poseStack) {
        this.bend = bendValue;
        BendApplier bendApplier = BendUtil.getBendLegacy(this.getBendDirection(), this.getBendX(), this.getBendY(), this.getBendZ(),
                this.basePlane, this.otherPlane, this.isBendInverted(), direction == Direction.UP, this.bendHeight(), direction == Direction.UP ? bendValue : -bendValue);
        this.iteratePositions(vector3f -> {
            Vector4f vector = new Vector4f(vector3f.x, vector3f.y, vector3f.z, 1).mul(poseStack.last().pose());
            Vector3f vector3f1 = bendApplier.consumer().apply(new Vector3f(vector.x, vector.y, vector.z));
            vector.set(vector3f1.x, vector3f1.y, vector3f1.z, 1);
            vector.mul(poseStack.last().pose().invert(new Matrix4f()));
            return vector3f.set(vector.x, vector.y, vector.z);
        });
        return bendApplier.matrix4f();
    }
}
