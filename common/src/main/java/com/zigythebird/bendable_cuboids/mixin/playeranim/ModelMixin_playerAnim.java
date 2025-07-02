package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IMutableModel;
import com.zigythebird.playeranim.accessors.IUpperPartHelper;
import com.zigythebird.playeranim.animation.PlayerAnimManager;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Model.class)
@SuppressWarnings("UnstableApiUsage")
public abstract class ModelMixin_playerAnim {
    @Inject(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At("HEAD"), cancellable = true)
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
        if(((Model)(Object)this) instanceof PlayerModel playerModel && ((IMutableModel)this).playerAnimLib$getAnimation() != null && ((IMutableModel)this).playerAnimLib$getAnimation().isActive()){
            ((PlayerModelAccessor_playerAnim)playerModel).getBodyParts().forEach((part)->{
                if(!((IUpperPartHelper) part).playerAnimLib$isUpperPart()){
                    part.render(matrices, vertices, light, overlay, color);
                }
            });
            matrices.pushPose();
            PlayerAnimManager emoteSupplier = ((IMutableModel)playerModel).playerAnimLib$getAnimation();
            PlayerAnimBone bone = new PlayerAnimBone("torso");
            emoteSupplier.get3DTransform(bone);
            PlayerBendHelper.applyTorsoBendToMatrix(matrices, 0, bone.getBend());
            ((PlayerModelAccessor_playerAnim)playerModel).getBodyParts().forEach((part)->{
                if(((IUpperPartHelper) part).playerAnimLib$isUpperPart()){
                    part.render(matrices, vertices, light, overlay, color);
                }
            });
            matrices.popPose();
            ci.cancel();
        }
    }
}
