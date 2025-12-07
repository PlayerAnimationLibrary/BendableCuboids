package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zigythebird.bendable_cuboids.api.ICubeDefinition;
import dev.tr7zw.skinlayers.render.CustomizableCubeListBuilder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CustomizableCubeListBuilder.class)
public class CustomizableCubeListBuilderMixin_skinLayers {
    @WrapOperation(method = "addVanillaBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/builders/CubeDefinition;bake(II)Lnet/minecraft/client/model/geom/ModelPart$Cube;"))
    private ModelPart.Cube bakeBendableCuboid(CubeDefinition instance, int texWidth, int texHeight, Operation<ModelPart.Cube> original, @Local(argsOnly = true, ordinal = 3) float width) {
        if (instance.dimensions.y() != 12) return original.call(instance, texWidth, texHeight);
        return ((ICubeDefinition)instance).bakeBendableCuboid(texWidth, texHeight, instance.dimensions.x() == 8 ? Direction.DOWN : Direction.UP, -1);
    }
}
