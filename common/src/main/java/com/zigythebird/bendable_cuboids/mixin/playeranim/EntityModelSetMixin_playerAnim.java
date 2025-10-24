package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.llamalad7.mixinextras.sugar.Local;
import com.zigythebird.bendable_cuboids.api.ILayerDefinition;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin_playerAnim {
    @Inject(method = "bakeLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/builders/LayerDefinition;bakeRoot()Lnet/minecraft/client/model/geom/ModelPart;"), cancellable = true)
    private void bakeLayerWithBendsForPlayer(ModelLayerLocation modelLayerLocation, CallbackInfoReturnable<ModelPart> cir, @Local LayerDefinition layerDefinition) {
        if (modelLayerLocation.model().getPath().equals("player") || modelLayerLocation.model().getPath().equals("player_slim")) {
            cir.setReturnValue(((ILayerDefinition)layerDefinition).bakeRootWithBends());
        }
    }
}
