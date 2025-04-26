package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.IMutableModel;
import com.zigythebird.playeranim.accessors.IPlayerAnimationState;
import com.zigythebird.playeranim.accessors.IPlayerModel;
import com.zigythebird.playeranim.accessors.IUpperPartHelper;
import com.zigythebird.playeranim.animation.AnimationProcessor;
import com.zigythebird.playeranim.bones.PlayerAnimBone;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerModel.class, priority = 2001)
public abstract class PlayerModelMixin_playerAnim implements IMutableModel, IPlayerModel {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void initBendableStuff(ModelPart modelPart, boolean bl, CallbackInfo ci) {
        PlayerModel model = ((PlayerModel)(Object)this);
        bendy_lib$addBendMutator(model.body, Direction.DOWN);
        bendy_lib$addBendMutator(model.rightArm, Direction.UP);
        bendy_lib$addBendMutator(model.leftArm, Direction.UP);
        bendy_lib$addBendMutator(model.rightLeg, Direction.UP);
        bendy_lib$addBendMutator(model.leftLeg, Direction.UP);
        ((IUpperPartHelper)model.head).playerAnimLib$setUpperPart(true);
        ((IUpperPartHelper)model.rightArm).playerAnimLib$setUpperPart(true);
        ((IUpperPartHelper)model.leftArm).playerAnimLib$setUpperPart(true);
    }

    @Unique
    private void bendy_lib$addBendMutator(ModelPart part, Direction d){
        PlayerBendHelper.initBend(part, d);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)V", at = @At(value = "RETURN"))
    private void setupPlayerAnimation(PlayerRenderState playerRenderState, CallbackInfo ci) {
        PlayerModel model = ((PlayerModel)(Object)this);
        if(!this.playerAnimLib$isFirstPersonRender() && playerRenderState instanceof IPlayerAnimationState state && state.playerAnimLib$getAnimManager().isActive()) {
            AnimationProcessor processor = state.playerAnimLib$getAnimProcessor();
            PlayerAnimBone torso = processor.getBone("torso");
            PlayerAnimBone rightArm = processor.getBone("right_arm");
            PlayerAnimBone leftArm = processor.getBone("left_arm");
            PlayerAnimBone rightLeg = processor.getBone("right_leg");
            PlayerAnimBone leftLeg = processor.getBone("left_leg");

            PlayerBendHelper.bend(model.body, torso.getBendAxis(), torso.getBend());
            PlayerBendHelper.bend(model.rightArm, rightArm.getBendAxis(), rightArm.getBend());
            PlayerBendHelper.bend(model.leftArm, leftArm.getBendAxis(), leftArm.getBend());
            PlayerBendHelper.bend(model.rightLeg, rightLeg.getBendAxis(), rightLeg.getBend());
            PlayerBendHelper.bend(model.leftLeg, leftLeg.getBendAxis(), leftLeg.getBend());
        }
        else {
            resetBend(model.body);
            resetBend(model.leftArm);
            resetBend(model.rightArm);
            resetBend(model.leftLeg);
            resetBend(model.rightLeg);
        }
    }

    @Unique
    private static void resetBend(ModelPart part) {
        PlayerBendHelper.bend(part, 0, 0);
    }
}
