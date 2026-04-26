package com.zigythebird.bendable_cuboids.api;

import com.zigythebird.bendable_cuboids.impl.Plane;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     */
    void applyBend(float bendValue);

    default void applyBendDegrees(float bendValue) {
        applyBend(bendValue * Mth.DEG_TO_RAD);
    }

    @Nullable
    Direction getBendDirection();
    int getBendPivot();

    float getBendX();
    float getBendY();
    float getBendZ();

    float getExtentZ();

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
        Direction direction = getBendDirection();
        if (direction == null) return false;
        return direction.getAxisDirection() == Direction.AxisDirection.POSITIVE;
    }

    default void bc$copyState(BendableCube other) {
        if (getBendDirection() != null) applyBend(other.getBend());
    }
}
