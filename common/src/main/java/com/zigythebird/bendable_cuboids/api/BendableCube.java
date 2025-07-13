package com.zigythebird.bendable_cuboids.api;

import com.zigythebird.bendable_cuboids.impl.Plane;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

/**
 * Should be pretty self-explanatory...
 * Bend XYZ are the coordinates of the bend's center
 * If you don't know the math behind it, don't try to edit.
 * <p>
 * Use {@link BendableCube#applyBend} to bend the cube
 */
public interface BendableCube extends Bendable {
    /**
     * Apply bend on this cuboid
     * Values are in radians
     * @param bendValue bend value (Same as rotX)
     * @return Transformation matrix for transforming children
     */
    Matrix4f applyBend(float bendValue);

    default Matrix4f applyBendDegrees(float bendValue) {
        return applyBend(bendValue * Mth.DEG_TO_RAD);
    }

    @Nullable
    Direction getBendDirection();
    int getBendPivot();

    float getBendX();
    float getBendY();
    float getBendZ();

    Plane getBasePlane();
    Plane getOtherPlane();

    /**
     * Distance between the two opposite surface of the cuboid.
     * Calculate two plane distance is inefficient.
     * Try to override it (If you have size)
     * @return the size of the cube
     */
    float bendHeight();

    default boolean isBendInverted() {
        return getBendDirection() == Direction.UP || getBendDirection() == Direction.SOUTH || getBendDirection() == Direction.EAST;
    }

    default void rebuild(@NotNull Direction direction) {
        rebuild(direction, -1);
    }

    void rebuild(@NotNull Direction direction, int pivot);
}
