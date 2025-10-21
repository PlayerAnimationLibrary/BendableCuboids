package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.ICapeLayer;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerCapeModel.class)
public class PlayerCapeModelMixin_playerAnim implements ICapeLayer {
    @Shadow
    @Final
    private ModelPart cape;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ModelPart modelPart, CallbackInfo ci) {
        PlayerBendHelper.initBend(this.cape, Direction.UP, 6);
    }

    @Override
    public void applyBend(AvatarAnimManager manager, float bend) {
        float torsoBend = manager.get3DTransform(new PlayerAnimBone("torso")).getBend();
        if (torsoBend < 0) bend += torsoBend;

        PlayerBendHelper.bend(this.cape, bend);
    }

    @Override
    public void resetBend() {
        PlayerBendHelper.resetBend(this.cape);
    }
}
