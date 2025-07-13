package com.zigythebird.bendable_cuboids.mixin;

import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.BendableModelPart;
import com.zigythebird.bendable_cuboids.api.IUpperPartHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public class ModelPartMixin implements BendableModelPart {
    @Shadow
    @Final
    public List<ModelPart.Cube> cubes;
    @Shadow
    @Final
    public Map<String, ModelPart> children;

    @Unique
    private boolean bc$isUpper = false;

    @Override
    public @Nullable BendableCube bc$getCuboid(int index) {
        if (index >= this.cubes.size() || index < 0) return null;
        return this.cubes.get(index);
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyTransformExtended(ModelPart part, CallbackInfo ci) {
        Iterator<ModelPart.Cube> otherIterator = part.cubes.iterator();
        Iterator<ModelPart.Cube> myIterator = this.cubes.iterator();

        while (otherIterator.hasNext() && myIterator.hasNext()){
            BendableCube myCube = (BendableCube) myIterator.next();
            BendableCube otherCube = (BendableCube) otherIterator.next();

            Direction direction = otherCube.getBendDirection();
            if (direction != null) myCube.rebuild(direction, otherCube.getBendPivot());
            myCube.applyBend(otherCube.getBend());
        }
        this.bc$isUpper = ((IUpperPartHelper) part).bc$isUpperPart();
    }

    @Override
    public boolean bc$isUpperPart() {
        return this.bc$isUpper;
    }

    @Override
    public void bc$setUpperPart(boolean bl) {
        this.bc$isUpper = bl;

        for (ModelPart child : this.children.values()) {
            ((IUpperPartHelper) child).bc$setUpperPart(bl);
        }
    }
}
