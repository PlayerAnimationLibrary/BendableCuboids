package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.api.IUpperPartHelper;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mixin(Model.class)
@SuppressWarnings("UnstableApiUsage")
public abstract class ModelMixin_playerAnim {
    @Inject(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At("HEAD"), cancellable = true)
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
        if (this instanceof IMutableModel mutable && mutable.playerAnimLib$getAnimation() != null && mutable.playerAnimLib$getAnimation().isActive()) {
            Collection<ModelPart> modelParts = be$getBodyParts((Model) (Object) this);
            if (modelParts.isEmpty()) return;

            modelParts.forEach((part)->{
                if (!((IUpperPartHelper) part).bc$isUpperPart()) {
                    part.render(matrices, vertices, light, overlay, color);
                }
            });

            matrices.pushPose();
            PlayerAnimManager emoteSupplier = mutable.playerAnimLib$getAnimation();
            PlayerAnimBone bone = new PlayerAnimBone("torso");
            emoteSupplier.get3DTransform(bone);
            PlayerBendHelper.applyTorsoBendToMatrix(matrices, bone.getBend());

            modelParts.forEach((part)->{
                if (((IUpperPartHelper) part).bc$isUpperPart()) {
                    part.render(matrices, vertices, light, overlay, color);
                }
            });
            matrices.popPose();

            ci.cancel();
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
