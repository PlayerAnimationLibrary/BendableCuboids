package com.zigythebird.bendable_cuboids.mixin.geckolib;

import com.zigythebird.bendable_cuboids.impl.BendableCuboid;
import com.zigythebird.bendable_cuboids.impl.compatibility.geckolib.GeoCubeAccessor;
import com.zigythebird.playeranim.math.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.cache.object.GeoCube;

@Mixin(GeoCube.class)
public class GeoCubeMixin_geckoOnly implements GeoCubeAccessor {
    @Unique
    Pair<String, BendableCuboid> bendableCuboid;

    @Override
    public void setBendableCuboid(Pair<String, BendableCuboid> bendableCuboid) {
        this.bendableCuboid = bendableCuboid;
    }

    @Override
    public Pair<String, BendableCuboid> getBendableCuboid() {
        return bendableCuboid;
    }
}
