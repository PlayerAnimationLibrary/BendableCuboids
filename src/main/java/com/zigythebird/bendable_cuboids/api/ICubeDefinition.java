package com.zigythebird.bendable_cuboids.api;

import com.zigythebird.bendable_cuboids.impl.BendableCuboid;
import net.minecraft.core.Direction;

public interface ICubeDefinition {
    BendableCuboid bakeBendableCuboid(int texWidth, int texHeight, Direction direction, int pivot);
}
