package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.llamalad7.mixinextras.sugar.Local;
import com.zigythebird.bendable_cuboids.api.ILayerDefinition;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin_playerAnim {
    @Inject(method = "bakeLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/builders/LayerDefinition;bakeRoot()Lnet/minecraft/client/model/geom/ModelPart;"), cancellable = true)
    private void bakeLayerWithBendsForPlayer(ModelLayerLocation modelLayerLocation, CallbackInfoReturnable<ModelPart> cir, @Local LayerDefinition layerDefinition) {
        if (modelLayerLocation.model().getPath().equals("player") || modelLayerLocation.model().getPath().equals("player_slim")) {
            Map<String, Pair< Direction, Integer>> cuboidDataMap = new HashMap<>();
            cuboidDataMap.put("cape", Pair.of(Direction.UP, 6));
            cuboidDataMap.put("body", Pair.of(Direction.DOWN, -1));
            cuboidDataMap.put("jacket", Pair.of(Direction.DOWN, -1));
            cir.setReturnValue(((ILayerDefinition)layerDefinition).bakeRootWithBends(cuboidDataMap));
        }
    }
}
