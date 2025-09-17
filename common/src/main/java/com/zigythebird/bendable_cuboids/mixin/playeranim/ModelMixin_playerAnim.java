package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.api.SodiumHelper;
import com.zigythebird.playeranim.animation.PlayerAnimManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Model.class)
// @SuppressWarnings("UnstableApiUsage")
public abstract class ModelMixin_playerAnim {
    /*@Shadow
    public abstract ModelPart root(); TODO 1.21.9

    @Inject(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At("HEAD"))
    public void bc$render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color, CallbackInfo ci, @Share("palActive") LocalBooleanRef palActive) {
        PlayerAnimManager manager = this instanceof IMutableModel mutable ? mutable.playerAnimLib$getAnimation() : null;
        palActive.set(manager != null && manager.isActive());

        if (palActive.get()) ((SodiumHelper) this.root()).bc$useSodiumRendering(false);
    }

    @Inject(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At("TAIL"))
    public void bc$renderTail(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color, CallbackInfo ci, @Share("palActive") LocalBooleanRef palActive) {
        if (palActive.get()) ((SodiumHelper) this.root()).bc$useSodiumRendering(true);
    }*/
}
