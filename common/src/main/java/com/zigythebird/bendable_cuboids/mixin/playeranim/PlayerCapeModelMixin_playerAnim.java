package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IBoneUpdater;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerCapeModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerCapeModel.class)
public class PlayerCapeModelMixin_playerAnim implements IBoneUpdater {
    @Shadow
    @Final
    private ModelPart cape;

    @Inject(
            method = "pal$updatePart(Lcom/zigythebird/playeranim/animation/AvatarAnimManager;Lnet/minecraft/client/model/geom/ModelPart;Lcom/zigythebird/playeranimcore/bones/PlayerAnimBone;)V",
            at = @At(
                    value = "RETURN"
            )
    )
    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    public void bc$updatePart(AvatarAnimManager manager, ModelPart part, PlayerAnimBone bone, CallbackInfo ci) {
        if (this.cape != part) return;

        float bend = bone.getBend();
        if (Math.abs(bend) > 0.0001f) { // An ugly hack for animations that don't animate the cape
            float torsoBend = manager.get3DTransform(new PlayerAnimBone("torso")).getBend();
            if (torsoBend < 0) bend += torsoBend;
        }

        PlayerBendHelper.bend(this.cape, bend);
    }

    @Inject(
            method = "pal$resetAll(Lcom/zigythebird/playeranim/animation/AvatarAnimManager;)V",
            at = @At(
                    value = "RETURN"
            )
    )
    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    public void bc$resetAll(@Nullable AvatarAnimManager emote, CallbackInfo ci) {
        PlayerBendHelper.resetBend(this.cape);
    }
}
