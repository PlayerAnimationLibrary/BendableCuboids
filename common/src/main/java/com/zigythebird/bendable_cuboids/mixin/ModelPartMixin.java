package com.zigythebird.bendable_cuboids.mixin;

import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.BendableModelPart;
import com.zigythebird.bendable_cuboids.api.SodiumHelper;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public class ModelPartMixin implements BendableModelPart, SodiumHelper {
    @Shadow
    @Final
    public List<ModelPart.Cube> cubes;
    @Shadow
    @Final
    public Map<String, ModelPart> children;

    @Override
    public @Nullable BendableCube bc$getCuboid(int index) {
        if (index >= this.cubes.size() || index < 0) return null;
        return this.cubes.get(index);
    }

    @Override
    public void bc$useSodiumRendering(boolean use) {
        for (ModelPart.Cube cube : this.cubes) {
            if (cube instanceof SodiumHelper helper) {
                helper.bc$useSodiumRendering(use);
            }
        }
        for (ModelPart child : this.children.values()) {
            ((SodiumHelper) child).bc$useSodiumRendering(use);
        }
    }

    /*@Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyTransformExtended(ModelPart part, CallbackInfo ci) { TODO 1.21.9
        Iterator<ModelPart.Cube> otherIterator = part.cubes.iterator();
        Iterator<ModelPart.Cube> myIterator = this.cubes.iterator();

        while (otherIterator.hasNext() && myIterator.hasNext()){
            BendableCube myCube = (BendableCube) myIterator.next();
            BendableCube otherCube = (BendableCube) otherIterator.next();
            myCube.bc$copyState(otherCube);
        }
    }*/
}
