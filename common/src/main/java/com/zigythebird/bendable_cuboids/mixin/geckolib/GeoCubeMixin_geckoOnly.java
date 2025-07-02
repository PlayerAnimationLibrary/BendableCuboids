package com.zigythebird.bendable_cuboids.mixin.geckolib;

import com.zigythebird.bendable_cuboids.impl.compatibility.geckolib.GeckoLibBendableCuboid;
import com.zigythebird.bendable_cuboids.impl.compatibility.geckolib.GeoCubeAccessor;
import it.unimi.dsi.fastutil.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.cache.object.GeoCube;

@Mixin(GeoCube.class)
public class GeoCubeMixin_geckoOnly implements GeoCubeAccessor {
    @Unique
    Pair<String, GeckoLibBendableCuboid> bendableCuboid;

    @Override
    public void setBendableCuboid(Pair<String, GeckoLibBendableCuboid> bendableCuboid) {
        this.bendableCuboid = bendableCuboid;
    }

    @Override
    public Pair<String, GeckoLibBendableCuboid> getBendableCuboid() {
        return bendableCuboid;
    }
}
