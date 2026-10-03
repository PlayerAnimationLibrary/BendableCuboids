package com.zigythebird.bendable_cuboids.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zigythebird.bendable_cuboids.api.BendableCube;
import net.minecraft.core.Direction;
import org.joml.*;

import java.lang.Math;
import java.util.function.Function;

public class BendUtil {
    private static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

    public static Function<Vector3f, Vector3f> getBend(BendableCube cuboid) {
        return getBend(cuboid, cuboid.getBend());
    }

    public static Function<Vector3f, Vector3f> getBend(BendableCube cuboid, float bendValue) {
        return getBend(cuboid, bendValue, false);
    }

    /**
     * @param ownMesh whether the positions are the cube's own vertices, whose side faces' centre rows turn at the corner
     *                of the joint's outline; anything built on top of the cube, like 3D skin layers, keeps its shape
     */
    public static Function<Vector3f, Vector3f> getBend(BendableCube cuboid, float bendValue, boolean ownMesh) {
        return getBend(cuboid.getBendX(), cuboid.getBendY(), cuboid.getBendZ(), cuboid.getBasePlane(), cuboid.getOtherPlane(),
                cuboid.isBendInverted(), false, cuboid.bendHeight(), cuboid.bendDepth(), cuboid.bendGrow(), ownMesh, bendValue);
    }

    /**
     * Applies the transformation to every position in posSupplier. The function leaves in the position it is given the
     * point of the straight cube whose texture the vertex shows, since folding slides vertices along the surface.
     * @param bendDepth size of the cube across the bend
     * @param grow how far the cube stands out of the cube it is a layer over
     * @param ownMesh whether the positions are the cube's own vertices, whose side faces' centre rows turn at the corner
     *                of the joint's outline
     * @param bendValue bend value
     */
    public static Function<Vector3f, Vector3f> getBend(float bendX, float bendY, float bendZ, Plane basePlane, Plane otherPlane,
                                      boolean isBendInverted, boolean mirrorBend, float bendHeight, float bendDepth, float grow,
                                      boolean ownMesh, float bendValue) {
        if (bendValue == 0) return Function.identity();
        if (mirrorBend) bendValue *= -1;
        Matrix4f transformMatrix = applyBendToMatrix(new Matrix4f(), bendX, bendY, bendZ, bendValue);

        // The middle third of the cube is the joint, one half of it on each side of the bend.
        float joint = bendHeight/6;
        float halfDepth = bendDepth/2;
        // Past 180 degrees the joint takes the shape of the opposite bend.
        float bend = clampToRadian(bendValue);
        double angle = Math.min(Math.abs(bend), Math.PI);
        float tan = (float) Math.tan(angle/2);
        float sin = (float) Math.sin(angle), cos = (float) Math.cos(angle);
        boolean square = angle > Math.PI/2;
        // The halves meet on the line halving the bend. Up to 90 degrees it leaves the joint through the outer corner, a
        // miter. Past that the corner stays square and moves in along the fixed half until the joint ends flat at 180
        // degrees, and the line leaves through that square end. A layer keeps its grow ahead of the corner it covers.
        float corner = square ? (halfDepth - grow)*sin + grow : tan*halfDepth;
        // Depth outside the bend at which the line meets the square end. Up to 90 degrees the outline has no such turn,
        // and the depth moves from the middle of the side to the outer face so that it reaches the turn continuously.
        float cornerOut = square ? corner/tan : halfDepth*(float) (0.5 + angle/Math.PI);
        // Past 90 degrees the turned half's copy of the centre row slides onto the line and the square end, so that the
        // seam between the halves closes the end instead of cutting across the corner.
        float progress = (float) Math.clamp((angle - Math.PI/2)/(Math.PI/6), 0, 1);
        float slide = progress*progress*(3 - 2*progress);
        // Coordinates along which the turned half lies forward and the bend closes on positive depth.
        float alongSign = (mirrorBend || !isBendInverted) ? -1 : 1;
        float depthSign = (bend < 0 ? -1 : 1)*(isBendInverted ? 1 : -1);

        return (pos -> {
            // Plane that goes through the bottom vertices of the cube.
            float distFromBase = Math.abs(basePlane.distanceTo(pos));
            // Same as above, but for the top.
            float distFromOther = Math.abs(otherPlane.distanceTo(pos));
            if (mirrorBend || !isBendInverted) {
                float temp = distFromBase;
                distFromBase = distFromOther;
                distFromOther = temp;
            }
            float along = (distFromOther - distFromBase)/2;
            // Float subdivision can leave a grown cube's middle row a hair off the middle; it belongs to the fixed half
            // either way, or the layers would build the joint from different halves and part ways.
            boolean isTurned = along > 0.001f;
            Vector3f moved = pos;
            // The planes and the joint's end rows sit a hair off where float subdivision put them.
            if (distFromBase + distFromOther <= bendHeight + 0.001f && Math.abs(along) < joint - 0.001f) {
                float depth = depthSign*(pos.z - bendZ);
                float miter = tan*depth;
                if (depth > 0 && (isTurned ? along < miter : along > -miter)) {
                    // Inside the bend each half folds what passes the line halving the bend onto that line along the
                    // cube, so the faces stay flat, and the texture slides along with it instead of shrinking. Where the
                    // line leaves the joint through its end, the rest of the joint folds onto that point.
                    float folded = Math.min(depth, joint/tan);
                    pos.y = bendY + alongSign*tan*folded*(isTurned ? 1 : -1);
                    pos.z = bendZ + depthSign*folded;
                }
                else if (depth <= 0 && (isTurned ? along < 2*Quad.SEAM : along > -0.001f)) {
                    // Outside the bend the fixed half's centre row and the turned half's own copy of it lie on the line
                    // and the square end, and the seam between them closes the end; the rows of both halves stay on their
                    // own half, stretched to reach there.
                    float out = -depth;
                    if (ownMesh && out > 0.001f && out < halfDepth - 0.001f) {
                        // On the side faces the row's vertex half way across slides along the row, its texture with it,
                        // to where the outline turns, and those beside it keep their order. Both halves then meet on the
                        // line up to that turn, and no gap is left between them for the seam to fill.
                        float half = halfDepth/2;
                        out = out < half ? out*cornerOut/half : cornerOut + (out - half)*(halfDepth - cornerOut)/half;
                        pos.z = bendZ - depthSign*out;
                    }
                    moved = new Vector3f(pos);
                    float outMiter = -tan*out;
                    if (!isTurned) {
                        moved.y = bendY + alongSign*Math.min(tan*out, corner);
                    }
                    else {
                        // Up to 90 degrees the copy lies on the miter with the fixed half's row; past it, it slides
                        // onto the square end.
                        float end = square ? Math.max(outMiter, (corner - out*sin)/cos) : outMiter;
                        moved.y = bendY + alongSign*(outMiter + slide*(end - outMiter));
                    }
                }
            }
            if (isTurned) {
                Vector4f reposVector = new Vector4f(moved, 1f);
                reposVector.mul(transformMatrix);
                moved = new Vector3f(reposVector.x, reposVector.y, reposVector.z);
            }
            return moved;
        });
    }

    public static Function<Vector3f, Vector3f> getBendLegacy(BendableCube cuboid, float bendValue) {
        return getBendLegacy(cuboid.getBendDirection(), cuboid.getBendX(), cuboid.getBendY(), cuboid.getBendZ(),
                cuboid.getBasePlane(), cuboid.getOtherPlane(), cuboid.isBendInverted(), false, cuboid.bendHeight(), bendValue);
    }

    /**
     * Bends in the old pre-1.21.6 way which is more stretchy, but works in more situations, like for GeckoLib armor.
     * @param bendValue bend value
     */
    public static Function<Vector3f, Vector3f> getBendLegacy(Direction bendDirection, float bendX, float bendY, float bendZ, Plane basePlane, Plane otherPlane,
                                      boolean isBendInverted, boolean mirrorBend, float bendHeight, float bendValue) {
        if (mirrorBend) bendValue *= -1;
        final float finalBend = bendValue;
        Matrix4f transformMatrix = applyBendToMatrix(new Matrix4f(), bendX, bendY, bendZ, bendValue);

        Vector3f directionUnit;

        directionUnit = bendDirection.step();
        directionUnit.cross(Z_AXIS);
        //parallel to the bend's axis and to the cube's bend direction
        Plane bendPlane = new Plane(directionUnit, new Vector3f(bendX, bendY, bendZ));
        float halfSize = bendHeight/2;

        return (pos -> {
            float distFromBend = isBendInverted ? -bendPlane.distanceTo(pos) : bendPlane.distanceTo(pos);
            float distFromBase = basePlane.distanceTo(pos);
            float distFromOther = otherPlane.distanceTo(pos);
            Vector3f x = bendDirection.step();
            if (mirrorBend) {
                float temp = distFromBase;
                distFromBase = distFromOther;
                distFromOther = temp;
                distFromBend *= -1;
            }
            double s = Math.tan(finalBend/2)*distFromBend;
            boolean isInBendArea = Math.abs(distFromBase) + Math.abs(distFromOther) <= Math.abs(bendHeight);
            if (Math.abs(distFromBase) < Math.abs(distFromOther)) {
                if (isInBendArea) {
                    x.mul((float) (-distFromBase / halfSize * s));
                    pos.add(x);
                }
                Vector4f reposVector = new Vector4f(pos, 1f);
                reposVector.mul(transformMatrix);
                pos = new Vector3f(reposVector.x, reposVector.y, reposVector.z);
            }
            else if (isInBendArea) {
                x.mul((float) (-distFromOther/halfSize*s));
                pos.add(x);
            }
            return pos;
        });
    }

    public static Matrix4f applyBendToMatrix(Matrix4f transformMatrix, float bendX, float bendY, float bendZ, float bendValue) {
        transformMatrix.translate(bendX, bendY, bendZ);
        transformMatrix.rotateX(bendValue);
        transformMatrix.translate(-bendX, -bendY, -bendZ);

        return transformMatrix;
    }

    public static PoseStack applyBendToMatrix(PoseStack transformMatrix, float bendX, float bendY, float bendZ, float bendValue) {
        transformMatrix.translate(bendX, bendY, bendZ);
        transformMatrix.rotate(Axis.XP, bendValue);
        transformMatrix.translate(-bendX, -bendY, -bendZ);

        return transformMatrix;
    }

    public static float clampToRadian(float f) {
        final double a = Math.PI*2;
        double b = (f + Math.PI)%a;
        if (b < 0) {
            b += a;
        }
        return ((float) (b - Math.PI));
    }

}
