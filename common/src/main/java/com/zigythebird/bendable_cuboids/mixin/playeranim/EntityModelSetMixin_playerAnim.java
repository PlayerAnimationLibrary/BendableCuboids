package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin_playerAnim {
    @WrapOperation(method = "bakeLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/builders/LayerDefinition;bakeRoot()Lnet/minecraft/client/model/geom/ModelPart;"))
    private ModelPart bakeLayerWithBendsForPlayer(LayerDefinition instance, Operation<ModelPart> original, @Local LayerDefinition layerDefinition, @Local(argsOnly = true) ModelLayerLocation modelLayerLocation) {
        if ("minecraft".equals(modelLayerLocation.model().getNamespace())
                && ("player".equals(modelLayerLocation.model().getPath()) || "player_slim".equals(modelLayerLocation.model().getPath()))) {
            Map<String, Pair< Direction, Integer>> cuboidDataMap = new HashMap<>();
            cuboidDataMap.put("cape", Pair.of(Direction.UP, 6));
            cuboidDataMap.put("body", Pair.of(Direction.DOWN, -1));
            cuboidDataMap.put("jacket", Pair.of(Direction.DOWN, -1));
            return ((ILayerDefinition)layerDefinition).bakeRootWithBends(cuboidDataMap, Collections.singleton("head"));
        }
        return original.call(instance);
    }
}
