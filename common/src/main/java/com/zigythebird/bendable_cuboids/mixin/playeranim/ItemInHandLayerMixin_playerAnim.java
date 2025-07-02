package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zigythebird.playeranim.accessors.IPlayerAnimationState;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
@SuppressWarnings("UnstableApiUsage")
public class ItemInHandLayerMixin_playerAnim<S extends ArmedEntityRenderState> {
    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V", ordinal = 0))
    private void renderMixin(S armedEntityRenderState, ItemStackRenderState itemStackRenderState, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci){
        if(armedEntityRenderState instanceof IPlayerAnimationState state){
            if(state.playerAnimLib$getAnimManager().isActive()){
                PlayerAnimBone bone = state.playerAnimLib$getAnimProcessor().getBone(humanoidArm == HumanoidArm.LEFT ? "left_arm" : "right_arm");
                state.playerAnimLib$getAnimManager().get3DTransform(bone);
                float offset = 0.25f;
                poseStack.translate(0, offset, 0);
                Vector3f axis = new Vector3f((float) Math.cos(-0), 0, (float) -Math.sin(-0));
                poseStack.mulPose(new Quaternionf().rotateAxis(bone.getBend(), axis));
                poseStack.translate(0, - offset, 0);
            }
        }
    }
}
