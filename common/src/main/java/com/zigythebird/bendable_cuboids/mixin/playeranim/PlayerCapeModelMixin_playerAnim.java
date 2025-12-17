package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.ICapeLayer;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerCapeModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerCapeModel.class)
public class PlayerCapeModelMixin_playerAnim implements ICapeLayer {
    @Shadow
    @Final
    private ModelPart cape;

    @Override
    public void applyBend(AvatarAnimManager manager, float bend) {
        if (Math.abs(bend) > 0.0001f) { // An ugly hack for animations that don't animate the cape
            float torsoBend = manager.get3DTransform(new PlayerAnimBone("torso")).getBend();
            if (torsoBend < 0) bend += torsoBend;
        }

        PlayerBendHelper.bend(this.cape, bend);
    }

    @Override
    public void resetBend() {
        PlayerBendHelper.resetBend(this.cape);
    }
}
