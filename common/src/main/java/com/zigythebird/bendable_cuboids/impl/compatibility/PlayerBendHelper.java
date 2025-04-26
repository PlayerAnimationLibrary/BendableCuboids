package com.zigythebird.bendable_cuboids.impl.compatibility;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zigythebird.bendable_cuboids.impl.BendUtil;
import com.zigythebird.bendable_cuboids.api.ModelPartAccessor;
import com.zigythebird.bendable_cuboids.api.MutableCuboid;
import com.zigythebird.bendable_cuboids.impl.BendableCuboidBuilder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import java.util.Optional;

public class PlayerBendHelper {
    public static void bend(ModelPart modelPart, float axis, float rotation) {
        Optional<MutableCuboid> optionalMutableCuboid = ModelPartAccessor.optionalGetCuboid(modelPart, 0);
        if (optionalMutableCuboid.isPresent()) {
            MutableCuboid cuboid = optionalMutableCuboid.get();
            // Don't enable bend until rotation is bigger than epsilon.
            // This should avoid unnecessary heavy calculations.
            if (Math.abs(rotation) >= 0.0001f && cuboid.bendableCuboids$hasMutator("bend")) {
                cuboid.bendableCuboids$getAndActivateMutator("bend").applyBend(axis, rotation);
            }
            else cuboid.bendableCuboids$getAndActivateMutator(null);
        }
    }

    public static void initBend(ModelPart modelPart, Direction direction) {
        ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableModelPart -> mutableModelPart.bendableCuboids$registerMutator("bend", data -> new BendableCuboidBuilder().setDirection(direction).build(data)));
    }

    public static void initCapeBend(ModelPart modelPart) {
        ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableModelPart -> mutableModelPart.bendableCuboids$registerMutator("bend", data -> {
            data.pivot = 6;

            return new BendableCuboidBuilder().setDirection(Direction.UP).build(data);
        }));
    }

    public static void applyTorsoBendToMatrix(PoseStack poseStack, float axis, float bend) {
        BendUtil.applyBendToMatrix(poseStack, 0, 0.375F, 0, axis, bend);
    }
}
