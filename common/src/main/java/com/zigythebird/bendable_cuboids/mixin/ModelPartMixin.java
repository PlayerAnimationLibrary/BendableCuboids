package com.zigythebird.bendable_cuboids.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.BendableCuboidsMod;
import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.BendableModelPart;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ModelPart.class)
public class ModelPartMixin implements BendableModelPart {
    @Shadow
    @Final
    private List<ModelPart.Cube> cubes;

    @Override
    public @Nullable BendableCube bc$getCuboid(int index) {
        if (index > this.cubes.size() || index < 0) return null;
        return this.cubes.get(index);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At("HEAD"))
    private void bc$captureModelPart(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color, CallbackInfo ci) {
        BendableCuboidsMod.currentModelPart = (ModelPart) (Object) this;
    }
}
