package com.zigythebird.bendable_cuboids.impl.compatibility;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.BendableModelPart;
import com.zigythebird.bendable_cuboids.impl.BendUtil;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

public class PlayerBendHelper {
    public static void resetBend(ModelPart modelPart) {
        PlayerBendHelper.bend(modelPart, 0);
    }

    public static void bend(ModelPart modelPart, float rotation) {
        BendableCube cube = ((BendableModelPart) modelPart).bc$getCuboid(0);
        if (cube == null) return;
        cube.applyBend(rotation);
    }

    public static void initBend(ModelPart modelPart, Direction direction) {
        BendableCube cube = ((BendableModelPart) modelPart).bc$getCuboid(0);
        if (cube != null) cube.rebuild(direction);
    }

    public static void initCapeBend(ModelPart modelPart) {
        BendableCube cube = ((BendableModelPart) modelPart).bc$getCuboid(0);
        if (cube != null) cube.rebuild(Direction.UP, 6);
    }

    public static void applyTorsoBendToMatrix(PoseStack poseStack, float bend) {
        BendUtil.applyBendToMatrix(poseStack, 0, 0.375F, 0, bend);
    }
}
