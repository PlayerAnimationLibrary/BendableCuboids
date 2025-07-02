package com.zigythebird.bendable_cuboids.impl.compatibility.geckolib;

import com.zigythebird.bendable_cuboids.impl.BendableCuboid;
import com.zigythebird.playeranim.math.Pair;

public interface GeoCubeAccessor {
    void setBendableCuboid(Pair<String, GeckoLibBendableCuboid> bendableCuboid);
    Pair<String, GeckoLibBendableCuboid> getBendableCuboid();
}
