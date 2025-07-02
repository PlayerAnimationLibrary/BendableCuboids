package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
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
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
@SuppressWarnings({"UnstableApiUsage","rawtypes"})
public abstract class CapeLayerMixin_playerAnim<T extends HumanoidRenderState> extends RenderLayer<PlayerRenderState, PlayerModel> {
    @Shadow @Final private HumanoidModel<PlayerRenderState> model;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(RenderLayerParent renderLayerParent, EntityModelSet entityModelSet, EquipmentAssetManager equipmentAssetManager, CallbackInfo ci) {
        PlayerBendHelper.initCapeBend(((PlayerCapeModelAccessor_playerAnim)model).getCape());
    }

    public CapeLayerMixin_playerAnim(RenderLayerParent<PlayerRenderState, PlayerModel> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, PlayerRenderState playerRenderState, float f, float g, CallbackInfo ci) {
        PlayerAnimManager emote = ((IPlayerAnimationState) playerRenderState).playerAnimLib$getAnimManager();
        if (model instanceof CapeLayerAccessor capeLayer) {
            if (emote.isActive()) {
                PlayerAnimBone bone = ((IPlayerAnimationState) playerRenderState).playerAnimLib$getAnimProcessor().getBone("torso");
                emote.get3DTransform(bone);
                ModelPart torso = this.getParentModel().body;

                poseStack.translate(torso.x / 16, torso.y / 16, torso.z / 16);
                poseStack.mulPose((new Quaternionf()).rotateXYZ(torso.xRot, torso.yRot, torso.zRot));
                PlayerBendHelper.applyTorsoBendToMatrix(poseStack, 0, bone.getBend());
                poseStack.translate(0.0F, 0.0F, 0.125F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));

                ModelPart part = capeLayer.getCape();
                PlayerAnimBone bone1 = ((IPlayerAnimationState)playerRenderState).playerAnimLib$getAnimProcessor().getBone("cape");
                bone1.setToInitialPose();
                // bone1.setBendAxis(bone.getBendAxis());
                bone1.setBend(bone.getBend());
                emote.get3DTransform(bone1);

                RenderUtil.translatePartToBone(part, bone1, part.getInitialPose());

                PlayerBendHelper.bend(part, 0, bone1.getBend());
            } else {
                PlayerBendHelper.bend(capeLayer.getCape(), 0, 0);
            }
        }
    }


    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V"))
    private boolean setupAnim(HumanoidModel instance, T playerRenderState) {
        PlayerAnimManager emote = ((IPlayerAnimationState)playerRenderState).playerAnimLib$getAnimManager();
        return emote == null || !emote.isActive();
    }
}
