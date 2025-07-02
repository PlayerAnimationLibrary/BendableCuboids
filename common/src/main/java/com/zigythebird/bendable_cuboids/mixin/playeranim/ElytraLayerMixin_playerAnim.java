package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IPlayerAnimationState;
import com.zigythebird.playeranim.animation.PlayerAnimManager;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WingsLayer.class)
@SuppressWarnings("UnstableApiUsage")
public abstract class ElytraLayerMixin_playerAnim<S extends HumanoidRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
    public ElytraLayerMixin_playerAnim(RenderLayerParent<S, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void inject(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, S humanoidRenderState, float f, float g, CallbackInfo ci) {
        if (humanoidRenderState instanceof IPlayerAnimationState animationState) {
            PlayerAnimManager emote = animationState.playerAnimLib$getAnimManager();
            if (emote.isActive()) {
                PlayerAnimBone bone = animationState.playerAnimLib$getAnimProcessor().getBone("torso");
                PlayerAnimBone bone1 = animationState.playerAnimLib$getAnimProcessor().getBone("cape");
                PlayerAnimBone bone2 = animationState.playerAnimLib$getAnimProcessor().getBone("elytra");
                emote.get3DTransform(bone);
                bone1.copyOtherBone(bone);
                emote.get3DTransform(bone1);
                bone2.copyOtherBone(bone1);
                emote.get3DTransform(bone2);
                poseStack.translate(bone2.getPosX() / 16, bone2.getPosX() / 16, bone2.getPosX() / 16);
                poseStack.mulPose((new Quaternionf()).rotateXYZ(-bone2.getRotX(), bone2.getRotY(), -bone2.getRotZ()));
                poseStack.scale(bone2.getScaleX(), bone2.getScaleY(), bone2.getScaleZ());
                PlayerBendHelper.applyTorsoBendToMatrix(poseStack, 0, bone.getBend());
            }
        }
    }
}
