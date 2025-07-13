package com.zigythebird.bendable_cuboids.mixin.playeranim;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LivingEntityRenderer.class, priority = 2000)
@SuppressWarnings("UnstableApiUsage")
public class LivingEntityMixin_playerAnim<S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    /*@Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V"))
    private void inject(S livingEntityRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci,  @Local RenderLayer<S, M> layer) {
        if (livingEntityRenderState instanceof IPlayerAnimationState state && state.playerAnimLib$getAnimManager().isActive() && ((IUpperPartHelper) layer).playerAnimLib$isUpperPart()) {
            PlayerAnimBone bone = state.playerAnimLib$getAnimProcessor().getBone("torso");
            state.playerAnimLib$getAnimManager().get3DTransform(bone);
            PlayerBendHelper.applyTorsoBendToMatrix(poseStack, bone.getBend());
        }
    }*/
}
