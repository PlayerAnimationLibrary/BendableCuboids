package com.zigythebird.bendable_cuboids.mixin.playeranim;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor_playerAnim<S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Accessor
    List<RenderLayer<S, M>> getLayers();
}
