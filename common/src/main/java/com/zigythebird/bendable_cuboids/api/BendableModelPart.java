package com.zigythebird.bendable_cuboids.api;

import org.jetbrains.annotations.Nullable;

public interface BendableModelPart extends IUpperPartHelper {
    /**
     * Get a cuboid, and cast it to {@link BendableCube}
     */
    @Nullable
    BendableCube bc$getCuboid(int index);
}
