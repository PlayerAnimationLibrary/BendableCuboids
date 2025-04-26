package com.zigythebird.bendable_cuboids.mixin.playeranim;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CapeLayer.class)
public interface CapeLayerAccessor_playerAnim {
    @Accessor
    HumanoidModel<PlayerRenderState> getModel();
}
