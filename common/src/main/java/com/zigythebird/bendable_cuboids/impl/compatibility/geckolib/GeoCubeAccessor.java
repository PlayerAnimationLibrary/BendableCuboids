package com.zigythebird.bendable_cuboids.impl.compatibility.geckolib;

import it.unimi.dsi.fastutil.Pair;

public interface GeoCubeAccessor {
    void setBendableCuboid(Pair<String, GeckoLibBendableCuboid> bendableCuboid);
    Pair<String, GeckoLibBendableCuboid> getBendableCuboid();
}
