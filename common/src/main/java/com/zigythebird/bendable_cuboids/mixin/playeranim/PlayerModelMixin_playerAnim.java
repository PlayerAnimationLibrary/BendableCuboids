package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IPlayerAnimationState;
import com.zigythebird.playeranim.animation.PlayerAnimManager;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerModel.class, priority = 2002)
@SuppressWarnings("UnstableApiUsage")
public abstract class PlayerModelMixin_playerAnim extends HumanoidModel<AvatarRenderState> {
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

    /**
     * Do not annotate with {@link @org.spongepowered.asm.mixin.Unique}: it breaks the bends.
     */
    private final PlayerAnimBone pal$torso = new PlayerAnimBone("torso");
    private final PlayerAnimBone pal$rightArm = new PlayerAnimBone("right_arm");
    private final PlayerAnimBone pal$leftArm = new PlayerAnimBone("left_arm");
    private final PlayerAnimBone pal$rightLeg = new PlayerAnimBone("right_leg");
    private final PlayerAnimBone pal$leftLeg = new PlayerAnimBone("left_leg");

    public PlayerModelMixin_playerAnim(ModelPart root) {
        super(root);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void bc$initBends(ModelPart modelPart, boolean bl, CallbackInfo ci) {
        PlayerBendHelper.initBend(this.jacket, Direction.DOWN);
        PlayerBendHelper.initBend(this.rightSleeve, Direction.UP);
        PlayerBendHelper.initBend(this.leftSleeve, Direction.UP);
        PlayerBendHelper.initBend(this.rightPants, Direction.UP);
        PlayerBendHelper.initBend(this.leftPants, Direction.UP);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At(value = "RETURN"))
    private void setupPlayerAnimation(AvatarRenderState playerRenderState, CallbackInfo ci) {
        PlayerAnimManager manager = playerRenderState instanceof IPlayerAnimationState state ? state.playerAnimLib$getAnimManager() : null;
        if (manager != null && manager.isActive()) {
            PlayerBendHelper.bend(this.body, pal$torso.getBend());
            PlayerBendHelper.bend(this.rightArm, pal$rightArm.getBend());
            PlayerBendHelper.bend(this.leftArm, pal$leftArm.getBend());
            PlayerBendHelper.bend(this.rightLeg, pal$rightLeg.getBend());
            PlayerBendHelper.bend(this.leftLeg, pal$leftLeg.getBend());

            PlayerBendHelper.bend(this.jacket, pal$torso.getBend());
            PlayerBendHelper.bend(this.rightSleeve, pal$rightArm.getBend());
            PlayerBendHelper.bend(this.leftSleeve, pal$leftArm.getBend());
            PlayerBendHelper.bend(this.rightPants, pal$rightLeg.getBend());
            PlayerBendHelper.bend(this.leftPants, pal$leftLeg.getBend());
        } else {
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
        }
    }
}
