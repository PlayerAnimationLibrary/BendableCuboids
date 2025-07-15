package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IPlayerAnimationState;
import com.zigythebird.playeranim.animation.PlayerAnimManager;
import com.zigythebird.playeranim.mixin.CapeLayerAccessor;
import com.zigythebird.playeranim.util.RenderUtil;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin_playerAnim extends RenderLayer<PlayerRenderState, PlayerModel> {
    @Shadow
    @Final
    private HumanoidModel<PlayerRenderState> model;

    private CapeLayerMixin_playerAnim(RenderLayerParent<PlayerRenderState, PlayerModel> renderLayerParent, Void v) {
        super(renderLayerParent);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, PlayerRenderState playerRenderState, float f, float g, CallbackInfo ci) {
        if (model instanceof CapeLayerAccessor capeLayer) {
            PlayerAnimManager emote = ((IPlayerAnimationState)playerRenderState).playerAnimLib$getAnimManager();
            ModelPart part = capeLayer.getCape();
            if (emote != null && emote.isActive()) {
                ModelPart torso = this.getParentModel().body;
                PlayerAnimBone bone = ((IPlayerAnimationState)playerRenderState).playerAnimLib$getAnimProcessor().getBone("cape");
                PlayerAnimBone torsoBone = ((IPlayerAnimationState)playerRenderState).playerAnimLib$getAnimProcessor().getBone("torso");

                torsoBone.setToInitialPose();
                emote.get3DTransform(torsoBone);
                poseStack.translate(torso.x / 16, torso.y / 16, torso.z / 16);
                RenderUtil.rotateZYX(poseStack.last(), torso.zRot, torso.yRot, torso.xRot);
                PlayerBendHelper.applyTorsoBendToMatrix(poseStack, -torsoBone.getBend());
                bone.setToInitialPose();
                emote.get3DTransform(bone);

                RenderUtil.translatePartToBone(part, bone);

                PlayerBendHelper.bend(part, torsoBone.getBend() + bone.getBend());
            }
            else {
                PlayerBendHelper.resetBend(part);
            }
        }
    }


    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private boolean translate(PoseStack instance, float f, float g, float h, @Local(argsOnly = true) PlayerRenderState playerRenderState) {
        PlayerAnimManager emote = ((IPlayerAnimationState)playerRenderState).playerAnimLib$getAnimManager();
        return emote == null || !emote.isActive();
    }
}
