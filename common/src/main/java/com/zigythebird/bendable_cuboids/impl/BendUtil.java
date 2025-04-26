package com.zigythebird.bendable_cuboids.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import org.joml.*;

import java.lang.Math;

public class BendUtil {
    public static BendApplier getBend(BendableCuboid cuboid, float bendAxis, float bendValue) {
        return getBend(cuboid.getBendDirection(), cuboid.getBendX(), cuboid.getBendY(), cuboid.getBendZ(),
                cuboid.basePlane, cuboid.otherPlane, cuboid.isBendInverted(), false, cuboid.bendHeight(), bendAxis, bendValue);
    }

    /**
     * Applies the transformation to every position in posSupplier
     * @param bendAxis axis for the bend
     * @param bendValue bend value
     */
    public static BendApplier getBend(Direction bendDirection, float bendX, float bendY, float bendZ, Plane basePlane, Plane otherPlane,
                                      boolean isBendInverted, boolean mirrorBend, float bendHeight, float bendAxis, float bendValue) {
        Vector3f axis = new Vector3f((float) Math.cos(bendAxis), 0, (float) Math.sin(bendAxis));
        Matrix3f matrix3f = new Matrix3f().set(bendDirection.getRotation());
        axis.mul(matrix3f);
        if (mirrorBend) bendValue *= -1;
        final float finalBend = bendValue;
        Matrix4f transformMatrix = applyBendToMatrix(new Matrix4f(), bendX, bendY, bendZ, bendAxis, bendValue);

        Vector3f directionUnit;

        directionUnit = bendDirection.step();
        directionUnit.cross(axis);
        //parallel to the bend's axis and to the cube's bend direction
        Plane bendPlane = new Plane(directionUnit, new Vector3f(bendX, bendY, bendZ));
        float halfSize = bendHeight/2;

        return new BendApplier(transformMatrix, pos -> {
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
            if (Math.abs(distFromBase) < Math.abs(distFromOther)) {
                x.mul((float) (-distFromBase/halfSize*s));
                pos.add(x);
                Vector4f reposVector = new Vector4f(pos, 1f);
                reposVector.mul(transformMatrix);
                pos = new Vector3f(reposVector.x, reposVector.y, reposVector.z);
            }
            else {
                x.mul((float) (-distFromOther/halfSize*s));
                pos.add(x);
            }
            return pos;
        });
    }

    public static Matrix4f applyBendToMatrix(Matrix4f transformMatrix, float bendX, float bendY, float bendZ, float bendAxis, float bendValue) {
        Vector3f axis = new Vector3f((float) Math.cos(bendAxis), 0, (float) Math.sin(bendAxis));

        transformMatrix.translate(bendX, bendY, bendZ);
        transformMatrix.rotate(bendValue, axis);
        transformMatrix.translate(-bendX, -bendY, -bendZ);

        return transformMatrix;
    }

    public static PoseStack applyBendToMatrix(PoseStack transformMatrix, float bendX, float bendY, float bendZ, float bendAxis, float bendValue) {
        Vector3f axis = new Vector3f((float) Math.cos(bendAxis), 0, (float) Math.sin(bendAxis));

        transformMatrix.translate(bendX, bendY, bendZ);
        transformMatrix.mulPose(new Quaternionf().rotateAxis(bendValue, axis));
        transformMatrix.translate(-bendX, -bendY, -bendZ);

        return transformMatrix;
    }
}
