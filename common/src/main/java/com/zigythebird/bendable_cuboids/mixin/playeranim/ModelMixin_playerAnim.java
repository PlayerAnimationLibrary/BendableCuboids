package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.api.IUpperPartHelper;
import com.zigythebird.bendable_cuboids.api.SodiumHelper;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IMutableModel;
import com.zigythebird.playeranim.animation.PlayerAnimManager;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.Collections;

@Mixin(Model.class)
@SuppressWarnings("UnstableApiUsage")
public abstract class ModelMixin_playerAnim {

    @WrapOperation(
            method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
            )
    )
    public void bc$render(ModelPart instance, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color, Operation<Void> original) {
        if (this instanceof IMutableModel mutable && mutable.playerAnimLib$getAnimation() != null && mutable.playerAnimLib$getAnimation().isActive()) {
            Collection<ModelPart> modelParts = be$getBodyParts((Model) (Object) this);
            if (modelParts.isEmpty()) {
                original.call(instance, poseStack, buffer, packedLight, packedOverlay, color);
                return;
            }

            ((SodiumHelper) instance).bc$useSodiumRendering(false);
            for (ModelPart part : modelParts) {
                if (!((IUpperPartHelper) part).bc$isUpperPart()) {
                    part.render(poseStack, buffer, packedLight, packedOverlay, color);
                }
            }

            poseStack.pushPose();
            PlayerAnimManager emoteSupplier = mutable.playerAnimLib$getAnimation();
            PlayerAnimBone bone = new PlayerAnimBone("torso");
            emoteSupplier.get3DTransform(bone);
            PlayerBendHelper.applyTorsoBendToMatrix(poseStack, bone.getBend());

            for (ModelPart part : modelParts) {
                if (((IUpperPartHelper) part).bc$isUpperPart()) {
                    part.render(poseStack, buffer, packedLight, packedOverlay, color);
                }
            }

            poseStack.popPose();
            ((SodiumHelper) instance).bc$useSodiumRendering(true);
        } else {
            original.call(instance, poseStack, buffer, packedLight, packedOverlay, color);
        }
    }

    @Unique
    private static Collection<ModelPart> be$getBodyParts(Model model) {
        if (model instanceof PlayerModelAccessor_playerAnim accessor) {
            return accessor.getBodyParts();
        } else if (model instanceof HumanoidModel<?> humanoid) { // For armor
            return humanoid.root().children.values();
        } else {
            return Collections.emptySet();
        }
    }
}
