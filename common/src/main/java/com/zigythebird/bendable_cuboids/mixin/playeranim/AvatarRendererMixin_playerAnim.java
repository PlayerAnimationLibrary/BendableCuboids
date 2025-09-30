package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zigythebird.bendable_cuboids.api.IMutableModel;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.animation.PlayerAnimManager;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AvatarRenderer.class, priority = 2000)
public abstract class AvatarRendererMixin_playerAnim extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> {
    public AvatarRendererMixin_playerAnim(EntityRendererProvider.Context context, PlayerModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    private void renderHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, ResourceLocation resourceLocation, ModelPart modelPart, boolean bl, CallbackInfo ci) {
        PlayerModel model = this.getModel();
        PlayerAnimManager manager = ((IMutableModel) model).bc$getAnimation();

        if (manager == null || !manager.getFirstPersonMode().isEnabled()) {
            PlayerBendHelper.resetBend(modelPart);
            PlayerBendHelper.resetBend(model.rightSleeve);
            PlayerBendHelper.resetBend(model.leftSleeve);
        }
    }
}
