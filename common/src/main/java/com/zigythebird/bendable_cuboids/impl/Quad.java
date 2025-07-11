package com.zigythebird.bendable_cuboids.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
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
        this.vertices = new RepositionableVertex[4];
        this.vertices[0] = new RepositionableVertex(u2, v1, vertices[0]);
        this.vertices[1] = new RepositionableVertex(u1, v1, vertices[1]);
        this.vertices[2] = new RepositionableVertex(u1, v2, vertices[2]);
        this.vertices[3] = new RepositionableVertex(u2, v2, vertices[3]);
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
        Vector3f normal = this.getDirection();
        normal.mul(matrices.normal());

        for (int i = 0; i != 4; ++i){
            IVertex vertex = this.vertices[i];
            Vector3f vertexPos = vertex.getPos();
            Vector4f pos = new Vector4f(vertexPos.x/16f, vertexPos.y/16f, vertexPos.z/16f, 1);
            pos.mul(matrices.pose());
            vertexConsumer.addVertex(pos.x, pos.y, pos.z, color, vertex.getU(), vertex.getV(), overlay, light, normal.x, normal.y, normal.z);
        }
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

    // edge[2] can be calculated from edge 0, 1, 3...
    public static void createAndAddQuads(List<Quad> quads, Map<Vector3f, RememberingPos> positions, Vector3f[] edges, float u1, float v1, float u2, float v2, float textureWidth, float textureHeight, boolean mirror) {
        boolean positiveDirection = v2 > v1;
        float totalTexHeight = Mth.abs(v2 - v1);

        Vector3f origin = edges[0];
        Vector3f vecV = new Vector3f(edges[2]).sub(origin);
        float vFracScale = (v1 == v2) ? 0 : 1.0f / (v2 - v1);
        Vector3f vecU = new Vector3f(edges[1]).sub(origin);
        float uFracScale = (u1 == u2) ? 0 : 1.0f / (u2 - u1);

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
                dv = positiveDirection ? 2 : -2;
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

            if (isMiddleSegment && Mth.abs(u2 - u1) > 1.0f) {
                boolean uPositive = u2 > u1;
                float du = uPositive ? 1.0f : -1.0f;

                Vector3f uScanPosBottom = new Vector3f(vPos);
                Vector3f uScanPosTop = new Vector3f(vPos).add(vStep);

                for (float localU = u1; uPositive ? localU < u2 : localU > u2; ) {
                    float localU2 = localU + du;
                    if (uPositive) {
                        if (localU2 > u2) localU2 = u2;
                    } else {
                        if (localU2 < u2) localU2 = u2;
                    }
                    if (localU == localU2) break;

                    float actual_du = localU2 - localU;
                    Vector3f uStep = new Vector3f(vecU).mul(actual_du * uFracScale);

                    RememberingPos bottomLeft = getOrCreate(positions, uScanPosBottom);
                    RememberingPos topLeft = getOrCreate(positions, uScanPosTop);

                    uScanPosBottom.add(uStep);
                    uScanPosTop.add(uStep);

                    RememberingPos bottomRight = getOrCreate(positions, uScanPosBottom);
                    RememberingPos topRight = getOrCreate(positions, uScanPosTop);

                    quads.add(new Quad(new RememberingPos[]{bottomLeft, bottomRight, topRight, topLeft}, localU / textureWidth, localV / textureHeight, localU2 / textureWidth, localV2 / textureHeight, mirror));

                    localU = localU2;
                }

                vPos.add(vStep);
                nextVPos.add(vStep);
            } else {
                RememberingPos rp3 = getOrCreate(positions, vPos);
                RememberingPos rp0 = getOrCreate(positions, nextVPos);
                vPos.add(vStep);
                nextVPos.add(vStep);
                RememberingPos rp2 = getOrCreate(positions, vPos);
                RememberingPos rp1 = getOrCreate(positions, nextVPos);
                quads.add(new Quad(new RememberingPos[]{rp3, rp0, rp1, rp2}, u1 / textureWidth, localV / textureHeight, u2 / textureWidth, localV2 / textureHeight, mirror));
            }

            localV = localV2;
        }
    }

    public static RememberingPos getOrCreate(Map<Vector3f, RememberingPos> positions, Vector3f pos) {
        return positions.computeIfAbsent(pos, p -> new RememberingPos(new Vector3f(p)));
    }
}
