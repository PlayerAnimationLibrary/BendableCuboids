package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.BendableModelPart;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.accessors.ICapeLayer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin_playerAnim implements ICapeLayer {
    @Override
    public void applyBend(ModelPart cape, ModelPart torso, float bend) {
        BendableCube cube = ((BendableModelPart)torso).bc$getCuboid(0);
        if (cube != null) {
            bend += cube.getBend();
        }
        PlayerBendHelper.bend(cape, bend);
    }

    @Override
    public void resetBend(ModelPart cape) {
        PlayerBendHelper.resetBend(cape);
    }
}
