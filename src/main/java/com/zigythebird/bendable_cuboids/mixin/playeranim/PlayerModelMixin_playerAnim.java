package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.api.IMutableModel;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IBoneUpdater;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerModel.class, priority = 2002)
public abstract class PlayerModelMixin_playerAnim extends HumanoidModel<AvatarRenderState> implements IBoneUpdater, IMutableModel {
    @Shadow
    @Final
    public ModelPart jacket;
    @Shadow
    @Final
    public ModelPart rightSleeve;
    @Shadow
    @Final
    public ModelPart leftSleeve;
    @Shadow
    @Final
    public ModelPart rightPants;
    @Shadow
    @Final
    public ModelPart leftPants;

    @Unique
    private AvatarAnimManager bc$animation = null;

    public PlayerModelMixin_playerAnim(ModelPart root) {
        super(root);
    }

    @Inject(
            method = {
                    "pal$updatePart(Lcom/zigythebird/playeranim/animation/AvatarAnimManager;Lnet/minecraft/client/model/geom/ModelPart;Lcom/zigythebird/playeranimcore/bones/PlayerAnimBone;)V",
                    "pal$updatePart(Lcom/zigythebird/playeranim/animation/AvatarAnimManager;Lnet/minecraft/class_630;Lcom/zigythebird/playeranimcore/bones/PlayerAnimBone;)V"
            },
            at = @At(
                    value = "RETURN"
            )
    )
    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    public void bc$updatePart(AvatarAnimManager emote, ModelPart part, PlayerAnimBone bone, CallbackInfo ci) {
        this.bc$animation = emote;

        if (this.head == part) return;
        PlayerBendHelper.bend(part, bone.getBend());

        // Overlay shit
        if (this.body == part) {
            PlayerBendHelper.bend(this.jacket, bone.getBend());
        } else if (this.rightArm == part) {
            PlayerBendHelper.bend(this.rightSleeve, bone.getBend());
        } else if (this.leftArm == part) {
            PlayerBendHelper.bend(this.leftSleeve, bone.getBend());
        } else if (this.rightLeg == part) {
            PlayerBendHelper.bend(this.rightPants, bone.getBend());
        } else if (this.leftLeg == part) {
            PlayerBendHelper.bend(this.leftPants, bone.getBend());
        }
    }

    @Inject(
            method = "pal$resetAll(Lcom/zigythebird/playeranim/animation/AvatarAnimManager;)V",
            at = @At(
                    value = "RETURN"
            )
    )
    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    public void bc$resetAll(@Nullable AvatarAnimManager emote, CallbackInfo ci) {
        PlayerBendHelper.resetBend(this.body);
        PlayerBendHelper.resetBend(this.leftArm);
        PlayerBendHelper.resetBend(this.rightArm);
        PlayerBendHelper.resetBend(this.leftLeg);
        PlayerBendHelper.resetBend(this.rightLeg);

        PlayerBendHelper.resetBend(this.jacket);
        PlayerBendHelper.resetBend(this.leftSleeve);
        PlayerBendHelper.resetBend(this.rightSleeve);
        PlayerBendHelper.resetBend(this.leftPants);
        PlayerBendHelper.resetBend(this.rightPants);

        this.bc$animation = null;
    }

    @Override
    public @Nullable AvatarAnimManager bc$getAnimation() {
        return this.bc$animation;
    }
}
