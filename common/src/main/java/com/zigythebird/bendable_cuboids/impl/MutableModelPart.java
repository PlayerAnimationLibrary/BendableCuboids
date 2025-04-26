package com.zigythebird.bendable_cuboids.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.api.ModelPartAccessor;
import com.zigythebird.bendable_cuboids.api.MutableCuboid;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * ModelPart to support BendableCuboids
 * <p>
 * If you want to mutate existing Cuboids, see {@link ModelPartAccessor} and {@link MutableCuboid}
 *
 * This can be used with {@link BendableCuboid}.
 */
public abstract class MutableModelPart extends ModelPart {

    @Nullable
    @Deprecated
    private MutableModelPart last = null;

    protected final ObjectList<BendableCuboid> BendableCuboids = new ObjectArrayList<>();

    public MutableModelPart(List<Cube> cuboids, Map<String, ModelPart> children) {
        super(cuboids, children);
    }

    @Override
    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        super.render(matrices, vertices, light, overlay);
        if(!BendableCuboids.isEmpty()){
            matrices.pushPose();
            this.translateAndRotate(matrices);
            this.renderBendableCuboids(matrices.last(), vertices, light, overlay, color);
            matrices.popPose();
        }
    }

    protected void renderBendableCuboids(PoseStack.Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        this.BendableCuboids.forEach((cuboid)-> cuboid.render(matrices, vertexConsumer, light, overlay, color));
    }

    public void addBendableCuboid(BendableCuboid cuboid){
        this.BendableCuboids.add(cuboid);
    }

}
