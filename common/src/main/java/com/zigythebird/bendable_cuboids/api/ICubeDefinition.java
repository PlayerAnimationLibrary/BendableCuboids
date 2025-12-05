package com.zigythebird.bendable_cuboids.api;

import com.zigythebird.bendable_cuboids.impl.VanillaBendableCuboid;
import net.minecraft.core.Direction;

public interface ICubeDefinition {
    VanillaBendableCuboid bakeBendableCuboid(int texWidth, int texHeight, Direction direction, int pivot);
}
