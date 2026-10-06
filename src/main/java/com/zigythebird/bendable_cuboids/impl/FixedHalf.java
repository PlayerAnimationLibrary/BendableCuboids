package com.zigythebird.bendable_cuboids.impl;

import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Where the fixed half of a bent cube lies across the bend, as two convex pieces: its rigid part and its joint. Past
 * about 90 degrees the turned half passes into the fixed one, and what of it lies inside is not drawn, so the halves
 * never cover the same place.
 */
public final class FixedHalf {
    /** How far outside a piece a point still counts as inside it where a face is cut across. */
    private static final float EDGE = 1e-4f;
    /**
     * How far outside a piece a face lying along one of its sides still counts as inside: near 180 degrees the turned
     * half settles onto the fixed one, and a depth buffer cannot tell such a gap apart at the distances a player is seen.
     */
    private static final float SURFACE = 0.02f;

    private final float bendY, alongSign;
    /** Half-planes ny*y + nz*z <= c in the cube's coordinates, three floats each. */
    private final float[] rigid, joint;

    FixedHalf(float bendY, float alongSign, float[] rigid, float[] joint) {
        this.bendY = bendY;
        this.alongSign = alongSign;
        this.rigid = rigid;
        this.joint = joint;
    }

    /** Whether a vertex, by where it lies on the straight cube, belongs to the turned half. */
    boolean turns(Vector3fc original) {
        return this.alongSign*(original.y() - this.bendY) > 0.001f;
    }

    /** Whether the quad with these corners lies entirely outside, beyond one side of each piece. */
    boolean misses(Vector3fc a, Vector3fc b, Vector3fc c, Vector3fc d) {
        return misses(this.rigid, a, b, c, d) && misses(this.joint, a, b, c, d);
    }

    /** Whether the quad with these corners lies entirely inside one of the pieces. */
    boolean covers(Vector3fc a, Vector3fc b, Vector3fc c, Vector3fc d) {
        return covers(this.rigid, a, b, c, d) || covers(this.joint, a, b, c, d);
    }

    /**
     * @param polygon convex polygon, five floats per vertex: x, y, z, u, v
     * @return the convex pieces of the polygon outside the fixed half
     */
    List<float[]> outside(float[] polygon) {
        List<float[]> pieces = new ArrayList<>();
        for (float[] piece : outside(polygon, this.rigid)) pieces.addAll(outside(piece, this.joint));
        return pieces;
    }

    /** A convex polygon minus a convex piece: what lies beyond each of its half-planes in turn, within those before it. */
    private static List<float[]> outside(float[] polygon, float[] halfPlanes) {
        List<float[]> pieces = new ArrayList<>();
        float[] rest = polygon;
        for (int i = 0; i < halfPlanes.length && rest != null; i += 3) {
            float farthest = Float.NEGATIVE_INFINITY;
            for (int k = 0; k < rest.length; k += 5) farthest = Math.max(farthest, side(halfPlanes, i, rest[k + 1], rest[k + 2]));
            if (farthest <= SURFACE) continue;
            float[] beyond = clip(rest, -halfPlanes[i], -halfPlanes[i + 1], -halfPlanes[i + 2] - EDGE);
            if (beyond != null && area(beyond) > EDGE*EDGE) pieces.add(beyond);
            rest = clip(rest, halfPlanes[i], halfPlanes[i + 1], halfPlanes[i + 2] + EDGE);
        }
        return pieces;
    }

    /** Area of a planar polygon, five floats per vertex. */
    private static float area(float[] polygon) {
        float x = 0, y = 0, z = 0;
        for (int i = 2; i < polygon.length/5; i++) {
            float ax = polygon[(i - 1)*5] - polygon[0], ay = polygon[(i - 1)*5 + 1] - polygon[1], az = polygon[(i - 1)*5 + 2] - polygon[2];
            float bx = polygon[i*5] - polygon[0], by = polygon[i*5 + 1] - polygon[1], bz = polygon[i*5 + 2] - polygon[2];
            x += ay*bz - az*by;
            y += az*bx - ax*bz;
            z += ax*by - ay*bx;
        }
        return (float) Math.sqrt(x*x + y*y + z*z)/2;
    }

    /** The part of a convex polygon where ny*y + nz*z <= c, or null when nothing of it is left. */
    private static float[] clip(float[] polygon, float ny, float nz, float c) {
        int count = polygon.length/5;
        // Cutting a convex polygon along a line adds at most one vertex.
        float[] out = new float[(count + 1)*5];
        int n = 0;
        for (int i = 0; i < count; i++) {
            int j = (i + 1)%count;
            float hi = ny*polygon[i*5 + 1] + nz*polygon[i*5 + 2] - c;
            float hj = ny*polygon[j*5 + 1] + nz*polygon[j*5 + 2] - c;
            if (hi <= 0) {
                System.arraycopy(polygon, i*5, out, n*5, 5);
                n++;
            }
            if ((hi <= 0) != (hj <= 0)) {
                float t = hi/(hi - hj);
                for (int k = 0; k < 5; k++) out[n*5 + k] = polygon[i*5 + k] + (polygon[j*5 + k] - polygon[i*5 + k])*t;
                n++;
            }
        }
        if (n < 3) return null;
        return n == count + 1 ? out : Arrays.copyOf(out, n*5);
    }

    /** Beyond a side of the piece, or only touching it like the turned half's joint along the line halving the bend. */
    private static boolean misses(float[] halfPlanes, Vector3fc a, Vector3fc b, Vector3fc c, Vector3fc d) {
        for (int i = 0; i < halfPlanes.length; i += 3) {
            float nearest = Math.min(Math.min(side(halfPlanes, i, a), side(halfPlanes, i, b)), Math.min(side(halfPlanes, i, c), side(halfPlanes, i, d)));
            float farthest = Math.max(Math.max(side(halfPlanes, i, a), side(halfPlanes, i, b)), Math.max(side(halfPlanes, i, c), side(halfPlanes, i, d)));
            if (nearest >= -EDGE && farthest > SURFACE) return true;
        }
        return false;
    }

    private static boolean covers(float[] halfPlanes, Vector3fc a, Vector3fc b, Vector3fc c, Vector3fc d) {
        for (int i = 0; i < halfPlanes.length; i += 3) {
            if (side(halfPlanes, i, a) > SURFACE || side(halfPlanes, i, b) > SURFACE || side(halfPlanes, i, c) > SURFACE
                    || side(halfPlanes, i, d) > SURFACE) return false;
        }
        return true;
    }

    /** How far beyond the half-plane a point lies, negative inside. */
    private static float side(float[] halfPlanes, int i, Vector3fc point) {
        return side(halfPlanes, i, point.y(), point.z());
    }

    private static float side(float[] halfPlanes, int i, float y, float z) {
        return halfPlanes[i]*y + halfPlanes[i + 1]*z - halfPlanes[i + 2];
    }
}
