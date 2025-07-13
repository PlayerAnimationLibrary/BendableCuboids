package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IMutableModel;
import com.zigythebird.playeranim.accessors.IPlayerAnimationState;
import com.zigythebird.playeranimcore.animation.AnimationProcessor;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerModel.class, priority = 2002)
@SuppressWarnings("UnstableApiUsage")
public abstract class PlayerModelMixin_playerAnim implements IMutableModel {
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

    @Inject(method = "<init>", at = @At("TAIL"))
    private void bc$initBends(ModelPart modelPart, boolean bl, CallbackInfo ci) {
        PlayerBendHelper.initBend(this.jacket, Direction.DOWN);
        PlayerBendHelper.initBend(this.rightSleeve, Direction.UP);
        PlayerBendHelper.initBend(this.leftSleeve, Direction.UP);
        PlayerBendHelper.initBend(this.rightPants, Direction.UP);
        PlayerBendHelper.initBend(this.leftPants, Direction.UP);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)V", at = @At(value = "RETURN"))
    private void setupPlayerAnimation(PlayerRenderState playerRenderState, CallbackInfo ci) {
        PlayerModel model = ((PlayerModel)(Object)this);
        if(playerRenderState instanceof IPlayerAnimationState state && state.playerAnimLib$getAnimManager().isActive()) {
            AnimationProcessor processor = state.playerAnimLib$getAnimProcessor();
            PlayerAnimBone torso = processor.getBone("torso");
            PlayerAnimBone rightArm = processor.getBone("right_arm");
            PlayerAnimBone leftArm = processor.getBone("left_arm");
            PlayerAnimBone rightLeg = processor.getBone("right_leg");
            PlayerAnimBone leftLeg = processor.getBone("left_leg");

            PlayerBendHelper.bend(model.body, torso.getBend());
            PlayerBendHelper.bend(model.rightArm, rightArm.getBend());
            PlayerBendHelper.bend(model.leftArm, leftArm.getBend());
            PlayerBendHelper.bend(model.rightLeg, rightLeg.getBend());
            PlayerBendHelper.bend(model.leftLeg, leftLeg.getBend());

            PlayerBendHelper.bend(model.jacket, torso.getBend());
            PlayerBendHelper.bend(model.rightSleeve, rightArm.getBend());
            PlayerBendHelper.bend(model.leftSleeve, leftArm.getBend());
            PlayerBendHelper.bend(model.rightPants, rightLeg.getBend());
            PlayerBendHelper.bend(model.leftPants, leftLeg.getBend());
        } else {
            PlayerBendHelper.resetBend(model.body);
            PlayerBendHelper.resetBend(model.leftArm);
            PlayerBendHelper.resetBend(model.rightArm);
            PlayerBendHelper.resetBend(model.leftLeg);
            PlayerBendHelper.resetBend(model.rightLeg);

            PlayerBendHelper.resetBend(model.jacket);
            PlayerBendHelper.resetBend(model.leftSleeve);
            PlayerBendHelper.resetBend(model.rightSleeve);
            PlayerBendHelper.resetBend(model.leftPants);
            PlayerBendHelper.resetBend(model.rightPants);
        }
    }
}
