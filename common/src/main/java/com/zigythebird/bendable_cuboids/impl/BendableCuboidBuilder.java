package com.zigythebird.bendable_cuboids.impl;

import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class BendableCuboidBuilder {
    public Direction direction;

    public BendableCuboidBuilder setDirection(Direction d) {
        this.direction = d;
        return this;
    }

    public BendableCuboid build(BendableCuboidData data) {
        ArrayList<BendableCuboid.Quad> planes = new ArrayList<>();
        HashMap<Vector3f, RememberingPos> positions = new HashMap<>();
        float minX = data.x, minY = data.y, minZ = data.z, maxX = data.x + data.sizeX, maxY = data.y + data.sizeY, maxZ = data.z + data.sizeZ;
        float pminX = data.x - data.extraX, pminY = data.y - data.extraY, pminZ = data.z - data.extraZ, pmaxX = maxX + data.extraX, pmaxY = maxY + data.extraY, pmaxZ = maxZ + data.extraZ;
        if(data.mirror){
            float tmp = pminX;
            pminX = pmaxX;
            pmaxX = tmp;
        }

        //this is copy from MC's cuboid constructor
        Vector3f vertex1 = new Vector3f(pminX, pminY, pminZ); //west south down
        Vector3f vertex2 = new Vector3f(pmaxX, pminY, pminZ); //east south down
        Vector3f vertex3 = new Vector3f(pmaxX, pmaxY, pminZ); //east south up
        Vector3f vertex4 = new Vector3f(pminX, pmaxY, pminZ); //west south up
        Vector3f vertex5 = new Vector3f(pminX, pminY, pmaxZ); //west north down
        Vector3f vertex6 = new Vector3f(pmaxX, pminY, pmaxZ); //east north down
        Vector3f vertex7 = new Vector3f(pmaxX, pmaxY, pmaxZ); //east north up
        Vector3f vertex8 = new Vector3f(pminX, pmaxY, pmaxZ); //west north up

        int j = data.u;
        int k = (int) (data.u + data.sizeZ);
        int l = (int) (data.u + data.sizeZ + data.sizeX);
        int m = (int) (data.u + data.sizeZ + data.sizeX + data.sizeX);
        int n = (int) (data.u + data.sizeZ + data.sizeX + data.sizeZ);
        int o = (int) (data.u + data.sizeZ + data.sizeX + data.sizeZ + data.sizeX);
        int p = data.v;
        int q = (int) (data.v + data.sizeZ);
        int r = (int) (data.v + data.sizeZ + data.sizeY);
        float textureWidth = data.textureWidth;
        float textureHeight = data.textureHeight;
        boolean mirror = data.mirror;
        if (data.visibleFaces.contains(Direction.DOWN)) createAndAddQuads(planes, positions, new Vector3f[]{vertex6, vertex5, vertex2}, k, p, l, q, textureWidth, textureHeight, mirror); //down
        if (data.visibleFaces.contains(Direction.UP)) createAndAddQuads(planes, positions, new Vector3f[]{vertex3, vertex4, vertex7}, l, q, m, p, textureWidth, textureHeight, mirror); //up
        if (data.visibleFaces.contains(Direction.WEST)) createAndAddQuads(planes, positions, new Vector3f[]{vertex1, vertex5, vertex4}, j, q, k, r, textureWidth, textureHeight, mirror); //west
        if (data.visibleFaces.contains(Direction.NORTH)) createAndAddQuads(planes, positions, new Vector3f[]{vertex2, vertex1, vertex3}, k, q, l, r, textureWidth, textureHeight, mirror); //north
        if (data.visibleFaces.contains(Direction.EAST)) createAndAddQuads(planes, positions, new Vector3f[]{vertex6, vertex2, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror); //east
        if (data.visibleFaces.contains(Direction.SOUTH)) createAndAddQuads(planes, positions, new Vector3f[]{vertex5, vertex6, vertex8}, n, q, o, r, textureWidth, textureHeight, mirror); //south

        Vector3f pivot = new Vector3f(0, 0, 0);
        if (data.pivot >= 0) {
            float size = direction.step().mul(maxX - minX, maxY - minY, maxZ - minZ).length();
            if (data.pivot <= size) {
                pivot = direction.step().mul(size - (data.pivot * 2));
                vertex7 = vertex7.sub(pivot);
            }
        }
        boolean bl = direction == Direction.UP || direction == Direction.SOUTH || direction == Direction.EAST;
        Plane aPlane = new Plane(direction.step(), vertex7);
        Plane bPlane = new Plane(direction.step(), vertex1);
        float fullSize = - direction.step().dot(vertex1) + direction.step().dot(vertex7);
        float bendX = (data.sizeX + data.x + data.x - pivot.x())/2;
        float bendY = (data.sizeY + data.y + data.y - pivot.y())/2;
        float bendZ = (data.sizeZ + data.z + data.z - pivot.z())/2;
        return new BendableCuboid(planes.toArray(new BendableCuboid.Quad[0]), positions.values().toArray(new RememberingPos[0]), bendX, bendY, bendZ, direction, bl ? aPlane : bPlane, bl ? bPlane : aPlane, fullSize);
    }

    //edge[2] can be calculated from edge 0, 1, 3...
    protected void createAndAddQuads(Collection<BendableCuboid.Quad> quads, HashMap<Vector3f, RememberingPos> positions, Vector3f[] edges, int u1, int v1, int u2, int v2, float textureWidth, float textureHeight, boolean mirror) {
        int du = u2 < u1 ? 1 : -1;
        int dv = v1 < v2 ? 1 : -1;

        Vector3f origin = edges[0];
        Vector3f vecU = new Vector3f(edges[1]).sub(origin);
        Vector3f vecV = new Vector3f(edges[2]).sub(origin);

        float uFracScale = 1.0f / (u1 - u2);
        Vector3f uStep = vecU.mul(du * uFracScale);

        float vFracScale = 1.0f / (v2 - v1);
        Vector3f vStep = vecV.mul(dv * vFracScale);

        Vector3f uPos = new Vector3f(origin);

        for (int localU = u2; localU != u1; localU += du) {
            Vector3f nextUPos = new Vector3f(uPos).add(uStep);

            Vector3f vPos = new Vector3f(uPos);
            Vector3f nextVPos = new Vector3f(nextUPos);

            for (int localV = v1; localV != v2; localV += dv) {
                int localU2 = localU + du;
                int localV2 = localV + dv;

                RememberingPos rp3 = getOrCreate(positions, vPos);
                RememberingPos rp0 = getOrCreate(positions, nextVPos);
                vPos.add(vStep);
                nextVPos.add(vStep);
                RememberingPos rp2 = getOrCreate(positions, vPos);
                RememberingPos rp1 = getOrCreate(positions, nextVPos);

                quads.add(new BendableCuboid.Quad(new RememberingPos[]{rp3, rp0, rp1, rp2}, localU2 / textureWidth, localV / textureHeight, localU / textureWidth, localV2 / textureHeight, mirror));
            }
            uPos = nextUPos;
        }
    }

    protected RememberingPos getOrCreate(HashMap<Vector3f, RememberingPos> positions, Vector3f pos) {
        return positions.computeIfAbsent(pos, p -> new RememberingPos(new Vector3f(p)));
    }
}
