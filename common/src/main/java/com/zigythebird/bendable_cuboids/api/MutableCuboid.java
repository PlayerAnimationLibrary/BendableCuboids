package com.zigythebird.bendable_cuboids.api;

import com.zigythebird.bendable_cuboids.impl.BendableCuboid;
import com.zigythebird.bendable_cuboids.impl.BendableCuboidData;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface MutableCuboid {

    /**
     * Register a mutator to a cuboid
     * @param name registration name
     * @param builder BendableCuboid builder
     * @return is the registration success
     */
    boolean bendableCuboids$registerMutator(String name, Function<BendableCuboidData, BendableCuboid> builder);

    /**
     * Unregister a mutator
     * @param name registration name
     * @return could unregister something
     */
    boolean bendableCuboids$unregisterMutator(String name);

    /**
     * Get the active mutator and its identifier
     * @return null, if no active
     */
    @Nullable
    Tuple<String, BendableCuboid> bendableCuboids$getActiveMutator();

    /**
     * Check if mutator with key exists
     * @param key key
     * @return is the mutator registered (even if not active)
     */
    boolean bendableCuboids$hasMutator(String key);

    @Nullable
    Function<BendableCuboidData, BendableCuboid> bendableCuboids$getCuboidBuilder(String key);

    /**
     * Get a mutator
     * @param name mutator identifier
     * @return null, if no mutator exists or created
     */
    @Nullable
    @Deprecated
    //it can be removed in any future version
    BendableCuboid bendableCuboids$getMutator(String name);

    /**
     * Get a mutator and make it the active
     * @param name mutator identifier
     * @return null, if no registered
     */
    @Nullable
    BendableCuboid bendableCuboids$getAndActivateMutator(@Nullable String name);

    void bendableCuboids$copyStateFrom(MutableCuboid other);

}
