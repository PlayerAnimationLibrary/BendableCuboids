package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.api.SodiumHelper;
import com.zigythebird.playeranim.accessors.IMutableModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Collections;

@Mixin(Model.class)
@SuppressWarnings("UnstableApiUsage")
public abstract class ModelMixin_playerAnim {
    @Shadow public abstract ModelPart root();

    @Inject(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V", at = @At("HEAD"))
    public void bc$render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, CallbackInfo ci) {
        if (this instanceof IMutableModel mutable && mutable.playerAnimLib$getAnimation() != null && mutable.playerAnimLib$getAnimation().isActive()) {
            ((SodiumHelper) this.root()).bc$useSodiumRendering(false);
        }
    }

    @Inject(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V", at = @At("TAIL"))
    public void bc$renderTail(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, CallbackInfo ci) {
        ((SodiumHelper) this.root()).bc$useSodiumRendering(true);
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
