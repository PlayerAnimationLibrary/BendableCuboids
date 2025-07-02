package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IMutableModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerRenderer.class, priority = 2000)
public abstract class PlayerRendererMixin_playerAnim extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {
    public PlayerRendererMixin_playerAnim(EntityRendererProvider.Context context, PlayerModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void renderHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ResourceLocation resourceLocation, ModelPart modelPart, boolean bl, CallbackInfo ci) {
        if (((IMutableModel)this.getModel()).playerAnimLib$getAnimation() == null || !((IMutableModel)this.getModel()).playerAnimLib$getAnimation().getFirstPersonMode().isEnabled()) {
            PlayerBendHelper.bend(modelPart, 0, 0);
        }
    }
}
