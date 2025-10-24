package com.zigythebird.bendable_cuboids.api;

import com.zigythebird.bendable_cuboids.impl.BendableCuboid;

public interface ICubeDefinition {
    BendableCuboid bakeBendableCuboid(int texWidth, int texHeight);
}
