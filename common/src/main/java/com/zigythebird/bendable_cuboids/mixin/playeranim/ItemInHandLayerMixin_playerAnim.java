package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin_playerAnim<S extends ArmedEntityRenderState> {
    @Unique
    private final PlayerAnimBone bendableCuboids$rightArm = new PlayerAnimBone("right_arm");
    @Unique
    private final PlayerAnimBone bendableCuboids$leftArm = new PlayerAnimBone("left_arm");

    @Inject(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V", ordinal = 0))
    private void renderMixin(S armedEntityRenderState, ItemStackRenderState itemStackRenderState, ItemStack itemStack, HumanoidArm humanoidArm, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, CallbackInfo ci){
        if(armedEntityRenderState instanceof IAvatarAnimationState state){
            if(state.playerAnimLib$getAnimManager().isActive()){
                PlayerAnimBone bone = humanoidArm == HumanoidArm.LEFT ? bendableCuboids$leftArm : bendableCuboids$rightArm;
                bone.bend = 0;
                state.playerAnimLib$getAnimManager().get3DTransform(bone);
                float offset = 0.25f;
                poseStack.translate(0, offset, 0);
                poseStack.mulPose(Axis.XP.rotation(bone.getBend()));
                poseStack.translate(0, -offset, 0);
            }
        }
    }
}
