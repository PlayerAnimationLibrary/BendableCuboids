package com.zigythebird.bendable_cuboids.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.SodiumHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class VanillaBendableCuboid extends ModelPart.Cube implements BendableCube, SodiumHelper {
    private final BendableCuboid bendableCuboid;

    public VanillaBendableCuboid(int texCoordU, int texCoordV, float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, float growX, float growY, float growZ, boolean mirror, float texScaleU, float texScaleV, Set<Direction> visibleFaces, Direction direction, int pivot) {
        super(texCoordU, texCoordV, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, growX, growY, growZ, mirror, texScaleU, texScaleV, visibleFaces);
        this.bendableCuboid = new BendableCuboid(texCoordU, texCoordV, dimensionX, dimensionY, dimensionZ, growX, growY, growZ, mirror, texScaleU, texScaleV, visibleFaces, direction, pivot, this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    @Override
    public void compile(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        if ((bendableCuboid.useSodiumRendering && bendableCuboid.getBend() == 0) || bendableCuboid.sides == null) {
            super.compile(pose, buffer, packedLight, packedOverlay, color);
            return;
        }

        bendableCuboid.compile(pose, buffer, packedLight, packedOverlay, color);
    }

    @Override
    public void bc$useSodiumRendering(boolean use) {
        bendableCuboid.bc$useSodiumRendering(use);
    }

    @Override
    public void applyBend(float bendValue) {
        bendableCuboid.applyBend(bendValue);
    }

    @Override
    public @Nullable Direction getBendDirection() {
        return bendableCuboid.getBendDirection();
    }

    @Override
    public int getBendPivot() {
        return bendableCuboid.getBendPivot();
    }

    @Override
    public float getBendX() {
        return bendableCuboid.getBendX();
    }

    @Override
    public float getBendY() {
        return bendableCuboid.getBendY();
    }

    @Override
    public float getBendZ() {
        return bendableCuboid.getBendZ();
    }

    @Override
    public Plane getBasePlane() {
        return bendableCuboid.getBasePlane();
    }

    @Override
    public Plane getOtherPlane() {
        return bendableCuboid.getOtherPlane();
    }

    @Override
    public float bendHeight() {
        return bendableCuboid.bendHeight();
    }

    @Override
    public float getBend() {
        return bendableCuboid.getBend();
    }
}
