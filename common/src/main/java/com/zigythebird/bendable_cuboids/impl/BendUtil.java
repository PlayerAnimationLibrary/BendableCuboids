package com.zigythebird.bendable_cuboids.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zigythebird.bendable_cuboids.api.BendableCube;
import net.minecraft.core.Direction;
import org.joml.*;

import java.lang.Math;
import java.util.function.Function;

public class BendUtil {
    private static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

    public static Function<Vector3f, Vector3f> getBend(BendableCube cuboid) {
        return getBend(cuboid, cuboid.getBend());
    }

    public static Function<Vector3f, Vector3f> getBend(BendableCube cuboid, float bendValue) {
        return getBend(cuboid.getBendX(), cuboid.getBendY(), cuboid.getBendZ(),
                cuboid.getBasePlane(), cuboid.getOtherPlane(), cuboid.isBendInverted(), false, cuboid.bendHeight(), bendValue);
    }

    /**
     * Applies the transformation to every position in posSupplier
     * @param bendValue bend value
     */
    public static Function<Vector3f, Vector3f> getBend(float bendX, float bendY, float bendZ, Plane basePlane, Plane otherPlane,
                                      boolean isBendInverted, boolean mirrorBend, float bendHeight, float bendValue) {
        if (bendValue == 0) return Function.identity();
        if (mirrorBend) bendValue *= -1;
        final float finalBend = bendValue;
        Matrix4f transformMatrix = applyBendToMatrix(new Matrix4f(), bendX, bendY, bendZ, bendValue);

        float halfSize = bendHeight/2;

        return (pos -> {
            float distFromBase = Math.abs(basePlane.distanceTo(pos));
            float distFromOther = Math.abs(otherPlane.distanceTo(pos));
            float s = (float) Math.tan(finalBend/2)*pos.z;
            if (mirrorBend || !isBendInverted) {
                float temp = distFromBase;
                distFromBase = distFromOther;
                distFromOther = temp;
            }
            float v = halfSize - ((isBendInverted ? s < 0 : s >= 0) ? Math.min(Math.abs(s)/2, 1) : Math.abs(s));
            if (distFromBase < distFromOther) {
                if (distFromBase + distFromOther <= bendHeight && distFromBase > v)
                    pos.y = bendY + s;
                Vector4f reposVector = new Vector4f(pos, 1f);
                reposVector.mul(transformMatrix);
                pos = new Vector3f(reposVector.x, reposVector.y, reposVector.z);
            }
            else if (distFromBase + distFromOther <= bendHeight && distFromOther > v) {
                    pos.y = bendY - s;
            }
            return pos;
        });
    }

    public static Function<Vector3f, Vector3f> getBendLegacy(BendableCube cuboid, float bendValue) {
        return getBendLegacy(cuboid.getBendDirection(), cuboid.getBendX(), cuboid.getBendY(), cuboid.getBendZ(),
                cuboid.getBasePlane(), cuboid.getOtherPlane(), cuboid.isBendInverted(), false, cuboid.bendHeight(), bendValue);
    }

    /**
     * Bends in the old pre-1.21.6 way which is more stretchy, but works in more situations, like for GeckoLib armor.
     * @param bendValue bend value
     */
    public static Function<Vector3f, Vector3f> getBendLegacy(Direction bendDirection, float bendX, float bendY, float bendZ, Plane basePlane, Plane otherPlane,
                                      boolean isBendInverted, boolean mirrorBend, float bendHeight, float bendValue) {
        if (mirrorBend) bendValue *= -1;
        final float finalBend = bendValue;
        Matrix4f transformMatrix = applyBendToMatrix(new Matrix4f(), bendX, bendY, bendZ, bendValue);

        Vector3f directionUnit;

        directionUnit = bendDirection.step();
        directionUnit.cross(Z_AXIS);
        //parallel to the bend's axis and to the cube's bend direction
        Plane bendPlane = new Plane(directionUnit, new Vector3f(bendX, bendY, bendZ));
        float halfSize = bendHeight/2;

        return (pos -> {
            float distFromBend = isBendInverted ? -bendPlane.distanceTo(pos) : bendPlane.distanceTo(pos);
            float distFromBase = basePlane.distanceTo(pos);
            float distFromOther = otherPlane.distanceTo(pos);
            Vector3f x = bendDirection.step();
            if (mirrorBend) {
                float temp = distFromBase;
                distFromBase = distFromOther;
                distFromOther = temp;
                distFromBend *= -1;
            }
            double s = Math.tan(finalBend/2)*distFromBend;
            boolean isInBendArea = Math.abs(distFromBase) + Math.abs(distFromOther) <= Math.abs(bendHeight);
            if (Math.abs(distFromBase) < Math.abs(distFromOther)) {
                if (isInBendArea) {
                    x.mul((float) (-distFromBase / halfSize * s));
                    pos.add(x);
                }
                Vector4f reposVector = new Vector4f(pos, 1f);
                reposVector.mul(transformMatrix);
                pos = new Vector3f(reposVector.x, reposVector.y, reposVector.z);
            }
            else if (isInBendArea) {
                x.mul((float) (-distFromOther/halfSize*s));
                pos.add(x);
            }
            return pos;
        });
    }

    public static Matrix4f applyBendToMatrix(Matrix4f transformMatrix, float bendX, float bendY, float bendZ, float bendValue) {
        transformMatrix.translate(bendX, bendY, bendZ);
        transformMatrix.rotateX(bendValue);
        transformMatrix.translate(-bendX, -bendY, -bendZ);

        return transformMatrix;
    }

    public static PoseStack applyBendToMatrix(PoseStack transformMatrix, float bendX, float bendY, float bendZ, float bendValue) {
        transformMatrix.translate(bendX, bendY, bendZ);
        transformMatrix.mulPose(Axis.XP.rotation(bendValue));
        transformMatrix.translate(-bendX, -bendY, -bendZ);

        return transformMatrix;
    }
}
