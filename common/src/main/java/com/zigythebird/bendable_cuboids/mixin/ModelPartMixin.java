package com.zigythebird.bendable_cuboids.mixin;

import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.BendableModelPart;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ModelPart.class)
public class ModelPartMixin implements BendableModelPart {
    @Shadow
    @Final
    private List<ModelPart.Cube> cubes;

    @Override
    public @Nullable BendableCube bc$getCuboid(int index) {
        if (index >= this.cubes.size() || index < 0) return null;
        return this.cubes.get(index);
    }
}
