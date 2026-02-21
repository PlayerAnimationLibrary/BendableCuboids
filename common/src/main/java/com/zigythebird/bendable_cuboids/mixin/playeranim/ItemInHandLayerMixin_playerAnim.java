package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zigythebird.playeranim.accessors.IAnimatedPlayer;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin_playerAnim {
    @Unique
    private final PlayerAnimBone bendableCuboids$rightArm = new PlayerAnimBone("right_arm");
    @Unique
    private final PlayerAnimBone bendableCuboids$leftArm = new PlayerAnimBone("left_arm");

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 0))
    private void renderMixin(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci){
        if (livingEntity instanceof IAnimatedPlayer iPlayer){
            if(iPlayer.playerAnimLib$getAnimManager().isActive()){
                PlayerAnimBone bone = arm == HumanoidArm.LEFT ? bendableCuboids$leftArm : bendableCuboids$rightArm;
                bone.bend = 0;
                iPlayer.playerAnimLib$getAnimManager().get3DTransform(bone);
                float offset = 0.25f;
                poseStack.translate(0, offset, 0);
                poseStack.mulPose(Axis.XP.rotation(bone.getBend()));
                poseStack.translate(0, -offset, 0);
            }
        }
    }
}
