package com.zigythebird.bendable_cuboids.impl.compatibility.geckolib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import software.bernie.geckolib.cache.object.GeoBone;

public class GeckoRenderUtil {
    public static void translateMatrixToBone(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(-bone.getPosX(), bone.getPosY(), bone.getPosZ());
    }

    public static void rotateMatrixAroundBone(PoseStack poseStack, GeoBone bone) {
        if (bone.getRotZ() != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotation(bone.getRotZ()));
        }

        if (bone.getRotY() != 0.0F) {
            poseStack.mulPose(Axis.YP.rotation(bone.getRotY()));
        }

        if (bone.getRotX() != 0.0F) {
            poseStack.mulPose(Axis.XP.rotation(bone.getRotX()));
        }

    }

    public static void scaleMatrixForBone(PoseStack poseStack, GeoBone bone) {
        poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static void translateToPivotPoint(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
    }

    public static void translateAwayFromPivotPoint(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(-bone.getPivotX(), -bone.getPivotY(), -bone.getPivotZ());
    }

    public static void prepMatrixForBone(PoseStack poseStack, GeoBone bone) {
        translateMatrixToBone(poseStack, bone);
        translateToPivotPoint(poseStack, bone);
        rotateMatrixAroundBone(poseStack, bone);
        scaleMatrixForBone(poseStack, bone);
        translateAwayFromPivotPoint(poseStack, bone);
    }
}
