package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.impl.BendUtil;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IMutableModel;
import com.zigythebird.playeranim.accessors.IPlayerAnimationState;
import com.zigythebird.playeranim.accessors.IUpperPartHelper;
import com.zigythebird.playeranimcore.animation.AnimationProcessor;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerModel.class, priority = 2002)
@SuppressWarnings("UnstableApiUsage")
public abstract class PlayerModelMixin_playerAnim implements IMutableModel {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void initBendableStuff(ModelPart modelPart, boolean bl, CallbackInfo ci) {
        PlayerModel model = ((PlayerModel)(Object)this);
        BendUtil.initModel(model);

        ((IUpperPartHelper)model.head).playerAnimLib$setUpperPart(true);
        ((IUpperPartHelper)model.rightArm).playerAnimLib$setUpperPart(true);
        ((IUpperPartHelper)model.leftArm).playerAnimLib$setUpperPart(true);
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
        }
        else {
            resetBend(model.body);
            resetBend(model.leftArm);
            resetBend(model.rightArm);
            resetBend(model.leftLeg);
            resetBend(model.rightLeg);

            resetBend(model.jacket);
            resetBend(model.leftSleeve);
            resetBend(model.rightSleeve);
            resetBend(model.leftPants);
            resetBend(model.rightPants);
        }
    }

    @Unique
    private static void resetBend(ModelPart part) {
        PlayerBendHelper.bend(part, 0);
    }
}
