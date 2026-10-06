package com.zigythebird.bendable_cuboids.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/*
 * A replica of {@link ModelPart.Quad}
 * with IVertex and render()
 */
public class Quad {
    public final RepositionableVertex[] vertices;

    public Quad(RememberingPos[] vertices, float u1, float v1, float u2, float v2, boolean flip) {
        this(vertices, u1, v1, u2, v2, flip, RepositionableVertex.FIXED, RepositionableVertex.FIXED);
    }

    /**
     * @param uGradient change of u per unit of the face, so the texture follows a vertex the bend slides along it
     * @param vGradient same for v
     */
    public Quad(RememberingPos[] vertices, float u1, float v1, float u2, float v2, boolean flip, Vector3fc uGradient, Vector3fc vGradient) {
        this.vertices = new RepositionableVertex[4];
        this.vertices[0] = new RepositionableVertex(u2, v1, vertices[0], uGradient, vGradient);
        this.vertices[1] = new RepositionableVertex(u1, v1, vertices[1], uGradient, vGradient);
        this.vertices[2] = new RepositionableVertex(u1, v2, vertices[2], uGradient, vGradient);
        this.vertices[3] = new RepositionableVertex(u2, v2, vertices[3], uGradient, vGradient);
        if (flip){
            int i = vertices.length;

            for(int j = 0; j < i / 2; ++j) {
                RepositionableVertex vertex = this.vertices[j];
                this.vertices[j] = this.vertices[i - 1 - j];
                this.vertices[i - 1 - j] = vertex;
            }
        }
    }

    public void render(PoseStack.Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        render(matrices, vertexConsumer, light, overlay, color, null);
    }

    /**
     * @param fixedHalf where the fixed half of the bent cube lies: a quad of the turned half is drawn only outside it, a
     *                  quad entirely inside not at all; null draws the quad whole
     */
    public void render(PoseStack.Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color, @Nullable FixedHalf fixedHalf) {
        Vector3f normal = this.getDirection();
        normal.mul(matrices.normal());

        if (fixedHalf != null && turns(fixedHalf)) {
            Vector3f a = this.vertices[0].getPos(), b = this.vertices[1].getPos(), c = this.vertices[2].getPos(), d = this.vertices[3].getPos();
            if (fixedHalf.covers(a, b, c, d)) return;
            if (!fixedHalf.misses(a, b, c, d)) {
                // The two triangles the quad is drawn as, each cut to what lies outside the fixed half.
                for (float[] piece : fixedHalf.outside(corners(0, 1, 2))) emit(piece, matrices, vertexConsumer, light, overlay, color, normal);
                for (float[] piece : fixedHalf.outside(corners(0, 2, 3))) emit(piece, matrices, vertexConsumer, light, overlay, color, normal);
                return;
            }
        }

        for (int i = 0; i != 4; ++i){
            IVertex vertex = this.vertices[i];
            Vector3f vertexPos = vertex.getPos();
            Vector4f pos = new Vector4f(vertexPos.x/16f, vertexPos.y/16f, vertexPos.z/16f, 1);
            pos.mul(matrices.pose());
            vertexConsumer.addVertex(pos.x, pos.y, pos.z, color, vertex.getU(), vertex.getV(), overlay, light, normal.x, normal.y, normal.z);
        }
    }

    private boolean turns(FixedHalf fixedHalf) {
        for (RepositionableVertex vertex : this.vertices) {
            if (!fixedHalf.turns(vertex.pos.originPos)) return false;
        }
        return true;
    }

    /** The triangle through three of the quad's vertices, five floats each: position and texture coordinates. */
    private float[] corners(int first, int second, int third) {
        float[] polygon = new float[15];
        int[] picked = {first, second, third};
        for (int k = 0; k < 3; k++) {
            RepositionableVertex vertex = this.vertices[picked[k]];
            Vector3f pos = vertex.getPos();
            polygon[k*5] = pos.x;
            polygon[k*5 + 1] = pos.y;
            polygon[k*5 + 2] = pos.z;
            polygon[k*5 + 3] = vertex.getU();
            polygon[k*5 + 4] = vertex.getV();
        }
        return polygon;
    }

    /** Draws a convex polygon as a fan of quads from its first vertex, the last one closing on a repeated vertex. */
    private static void emit(float[] polygon, PoseStack.Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color, Vector3f normal) {
        int count = polygon.length/5;
        for (int i = 1; i < count - 1; i += 2) {
            emitVertex(polygon, 0, matrices, vertexConsumer, light, overlay, color, normal);
            emitVertex(polygon, i, matrices, vertexConsumer, light, overlay, color, normal);
            emitVertex(polygon, i + 1, matrices, vertexConsumer, light, overlay, color, normal);
            emitVertex(polygon, Math.min(i + 2, count - 1), matrices, vertexConsumer, light, overlay, color, normal);
        }
    }

    private static void emitVertex(float[] polygon, int index, PoseStack.Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color, Vector3f normal) {
        Vector4f pos = new Vector4f(polygon[index*5]/16f, polygon[index*5 + 1]/16f, polygon[index*5 + 2]/16f, 1);
        pos.mul(matrices.pose());
        vertexConsumer.addVertex(pos.x, pos.y, pos.z, color, polygon[index*5 + 3], polygon[index*5 + 4], overlay, light, normal.x, normal.y, normal.z);
    }

    /**
     * calculate the normal vector from the vertices' coordinates with cross-product
     * @return the normal vector (direction)
     */
    private Vector3f getDirection() {
        Vector3f buf = new Vector3f(vertices[3].getPos());
        buf.mul(-1);
        Vector3f vecB = new Vector3f(vertices[1].getPos());
        vecB.add(buf);
        buf = new Vector3f(vertices[2].getPos());
        buf.mul(-1);
        Vector3f vecA = new Vector3f(vertices[0].getPos());
        vecA.add(buf);
        vecA.cross(vecB);
        // Return the cross-product, if it's zero then return anything non-zero to not cause crash...
        return vecA.normalize().isFinite() ? vecA : Direction.NORTH.step();
    }

    /** How far a row's copy at a seam lies from it; nothing shows while the cube is straight. */
    static final float SEAM = 0.01f;

    /**
     * edge[2] can be calculated from edge 0, 1, 3...
     * @param seamY height of the row the bend turns the cube at
     * @param turnedSide sign of the direction from that row into the half the bend turns
     * @param joint distance from that row to either end of the joint
     */
    public static void createAndAddQuads(List<Quad> quads, Map<Vector3f, RememberingPos> positions, Vector3f[] edges, float u1, float v1, float u2, float v2, float textureWidth, float textureHeight, boolean mirror, float seamY, float turnedSide, float joint) {
        boolean positiveDirection = v2 > v1;
        float totalTexHeight = Mth.abs(v2 - v1);

        Vector3f origin = edges[0];
        Vector3f vecV = new Vector3f(edges[2]).sub(origin);
        float vFracScale = (v1 == v2) ? 0 : 1.0f / (v2 - v1);
        Vector3f vecU = new Vector3f(edges[1]).sub(origin);
        float uFracScale = (u1 == u2) ? 0 : 1.0f / (u2 - u1);
        float rise = Math.signum(vecV.y());
        float seamDv = positiveDirection ? 0.01f : -0.01f;
        // u runs from u2 at the origin to u1 along vecU, v from v1 to v2 along vecV.
        Vector3f uGradient = new Vector3f(vecU).mul(vecU.lengthSquared() == 0 ? 0 : (u1 - u2)/vecU.lengthSquared()/textureWidth);
        Vector3f vGradient = new Vector3f(vecV).mul(vecV.lengthSquared() == 0 ? 0 : (v2 - v1)/vecV.lengthSquared()/textureHeight);

        Vector3f vPos = new Vector3f(origin);
        Vector3f nextVPos = new Vector3f(edges[1]);
        Vector3f vStep = new Vector3f();

        float[] quadHeights = null;
        int segmentHeight = 0;
        if (totalTexHeight > 0 && totalTexHeight % 3.0f == 0) {
            segmentHeight = (int) (totalTexHeight / 3.0f);
            if (segmentHeight > 0) {
                quadHeights = new float[2 + segmentHeight];
                quadHeights[0] = segmentHeight;
                Arrays.fill(quadHeights, 1, 1 + segmentHeight, 1.0f);
                quadHeights[1 + segmentHeight] = segmentHeight;
            }
        }

        int layerIndex = 0;

        for (float localV = v1; positiveDirection ? localV < v2 : localV > v2; ) {
            float dv;
            boolean isMiddleSegment = false;
            if (quadHeights != null) {
                if (layerIndex >= quadHeights.length) {
                    break;
                }
                dv = positiveDirection ? quadHeights[layerIndex] : -quadHeights[layerIndex];
                if (layerIndex > 0 && layerIndex <= segmentHeight) {
                    isMiddleSegment = true;
                }
                layerIndex++;
            } else {
                dv = positiveDirection ? 1 : -1;
            }

            float localV2 = localV + dv;
            if (quadHeights == null) {
                if (positiveDirection) {
                    if (localV2 > v2) {
                        localV2 = v2;
                    }
                } else {
                    if (localV2 < v2) {
                        localV2 = v2;
                    }
                }
            }

            float actual_dv = localV2 - localV;
            if (actual_dv == 0) break;
            vStep.set(vecV).mul(actual_dv * vFracScale);

            // A row on the copy's side of a seam starts or ends at the copy, and the seam's quads join the copy to the row.
            float startSide = seamSide(vPos.y(), seamY, turnedSide, joint), endSide = seamSide(vPos.y() + vStep.y(), seamY, turnedSide, joint);
            float startCopy = rise != 0 && startSide == rise ? startSide : 0, endCopy = rise != 0 && endSide == -rise ? endSide : 0;

            if (isMiddleSegment && Mth.abs(u2 - u1) > 1.0f) {
                boolean uPositive = u2 > u1;
                float du = uPositive ? 1.0f : -1.0f;

                Vector3f uScanPosBottom = new Vector3f(vPos);
                Vector3f uScanPosTop = new Vector3f(vPos).add(vStep);

                for (float localU = u2; localU != u1; localU -= du) {
                    float localU2 = localU - du;
                    if (uPositive) {
                        if (localU2 > u2) localU2 = u2;
                    } else {
                        if (localU2 < u2) localU2 = u2;
                    }
                    if (localU == localU2) break;

                    float actual_du = localU2 - localU;
                    Vector3f uStep = new Vector3f(vecU).mul(actual_du * uFracScale);
                    Vector3f startLeft = new Vector3f(uScanPosBottom), endLeft = new Vector3f(uScanPosTop);

                    RememberingPos bottomLeft = getOrCreate(positions, uScanPosBottom, startCopy);
                    RememberingPos topLeft = getOrCreate(positions, uScanPosTop, endCopy);

                    uScanPosBottom.sub(uStep);
                    uScanPosTop.sub(uStep);

                    RememberingPos bottomRight = getOrCreate(positions, uScanPosBottom, startCopy);
                    RememberingPos topRight = getOrCreate(positions, uScanPosTop, endCopy);

                    quads.add(new Quad(new RememberingPos[]{bottomLeft, bottomRight, topRight, topLeft}, localU2 / textureWidth, localV / textureHeight, localU / textureWidth, localV2 / textureHeight, mirror, uGradient, vGradient));
                    if (startCopy != 0) addSeam(quads, positions, startLeft, new Vector3f(uScanPosBottom), 0, startCopy, localU / textureWidth, localU2 / textureWidth, (localV - seamDv) / textureHeight, (localV + seamDv) / textureHeight, mirror, uGradient, vGradient);
                    if (endCopy != 0) addSeam(quads, positions, endLeft, new Vector3f(uScanPosTop), endCopy, 0, localU / textureWidth, localU2 / textureWidth, (localV2 - seamDv) / textureHeight, (localV2 + seamDv) / textureHeight, mirror, uGradient, vGradient);
                }

                vPos.add(vStep);
                nextVPos.add(vStep);
            } else {
                Vector3f startLeft = new Vector3f(vPos), startRight = new Vector3f(nextVPos);
                RememberingPos rp3 = getOrCreate(positions, vPos, startCopy);
                RememberingPos rp0 = getOrCreate(positions, nextVPos, startCopy);
                vPos.add(vStep);
                nextVPos.add(vStep);
                RememberingPos rp2 = getOrCreate(positions, vPos, endCopy);
                RememberingPos rp1 = getOrCreate(positions, nextVPos, endCopy);
                quads.add(new Quad(new RememberingPos[]{rp3, rp0, rp1, rp2}, u1 / textureWidth, localV / textureHeight, u2 / textureWidth, localV2 / textureHeight, mirror, uGradient, vGradient));
                if (startCopy != 0) addSeam(quads, positions, startLeft, startRight, 0, startCopy, u2 / textureWidth, u1 / textureWidth, (localV - seamDv) / textureHeight, (localV + seamDv) / textureHeight, mirror, uGradient, vGradient);
                if (endCopy != 0) addSeam(quads, positions, new Vector3f(vPos), new Vector3f(nextVPos), endCopy, 0, u2 / textureWidth, u1 / textureWidth, (localV2 - seamDv) / textureHeight, (localV2 + seamDv) / textureHeight, mirror, uGradient, vGradient);
            }

            localV = localV2;
        }
    }

    /**
     * Where the bend has to fold the cube apart, a row is split in two and a seam joins the copies. The turned half
     * starts at its own copy of the row it turns at, past 90 degrees the bend opens that seam into the square end of the
     * joint, and the rows on either side stay on their own half. At each end of the joint the joint has its own copy of
     * the row it starts at, so that it can fold without dragging the rest of the cube along.
     * @return the side, along y, of the copy at the row at {@code y}, or 0 when that row is not split
     */
    private static float seamSide(float y, float seamY, float turnedSide, float joint) {
        float fromSeam = y - seamY;
        if (Math.abs(fromSeam) < 0.001f) return turnedSide;
        if (Math.abs(Math.abs(fromSeam) - joint) < 0.001f) return -Math.signum(fromSeam);
        return 0;
    }

    /**
     * Quad across a seam from the row before it, in the order the face is built, to the row after it; its texture is the
     * line between the two rows, each half of the seam taking its own side of it.
     */
    private static void addSeam(List<Quad> quads, Map<Vector3f, RememberingPos> positions, Vector3f left, Vector3f right, float copyBefore, float copyAfter, float uLeft, float uRight, float vBefore, float vAfter, boolean mirror, Vector3fc uGradient, Vector3fc vGradient) {
        RememberingPos bottomLeft = getOrCreate(positions, left, copyBefore);
        RememberingPos bottomRight = getOrCreate(positions, right, copyBefore);
        RememberingPos topRight = getOrCreate(positions, right, copyAfter);
        RememberingPos topLeft = getOrCreate(positions, left, copyAfter);
        quads.add(new Quad(new RememberingPos[]{bottomLeft, bottomRight, topRight, topLeft}, uRight, vBefore, uLeft, vAfter, mirror, uGradient, vGradient));
    }

    /** The vertex at {@code pos}, or its copy at a seam lying on the given side of it when {@code copySide} is not 0. */
    private static RememberingPos getOrCreate(Map<Vector3f, RememberingPos> positions, Vector3f pos, float copySide) {
        return getOrCreate(positions, copySide != 0 ? new Vector3f(pos).add(0, SEAM*copySide, 0) : pos);
    }

    public static RememberingPos getOrCreate(Map<Vector3f, RememberingPos> positions, Vector3f pos) {
        return positions.computeIfAbsent(pos, p -> new RememberingPos(new Vector3f(p)));
    }
}
