package com.zigythebird.bendable_cuboids.impl.compatibility.geckolib;

import com.zigythebird.bendable_cuboids.impl.*;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GeckoLibBendableCuboidBuilder extends BendableCuboidBuilder {
    public BendableCuboid build(GeoCube cube, BakedGeoModel model, String root) {
        ArrayList<BendableCuboid.Quad> planes = new ArrayList<>();
        HashMap<Vector3f, RememberingPos> positions = new HashMap<>();

        GeoVertex geoVertex1 = cube.quads()[2].vertices()[3];
        GeoVertex geoVertex2 = cube.quads()[2].vertices()[2];
        GeoVertex geoVertex3 = cube.quads()[2].vertices()[1];
        GeoVertex geoVertex4 = cube.quads()[2].vertices()[0];
        GeoVertex geoVertex5 = cube.quads()[3].vertices()[2];
        GeoVertex geoVertex6 = cube.quads()[3].vertices()[3];
        GeoVertex geoVertex7 = cube.quads()[3].vertices()[0];
        GeoVertex geoVertex8 = cube.quads()[3].vertices()[1];

        Vector3f vertex1 = geoVertex1.position();
        Vector3f vertex2 = geoVertex2.position();
        Vector3f vertex3 = geoVertex3.position();
        Vector3f vertex4 = geoVertex4.position();
        Vector3f vertex5 = geoVertex5.position();
        Vector3f vertex6 = geoVertex6.position();
        Vector3f vertex7 = geoVertex7.position();
        Vector3f vertex8 = geoVertex8.position();

        boolean mirror = cube.mirror();
        float textureWidth = (float) model.properties().textureWidth();
        float textureHeight = (float) model.properties().textureHeight();

        createAndAddQuads(planes, positions, new Vector3f[]{vertex6, vertex5, vertex2}, cube.quads()[5], textureWidth, textureHeight, mirror);
        createAndAddQuads(planes, positions, new Vector3f[]{vertex3, vertex4, vertex7}, cube.quads()[4], textureWidth, textureHeight, mirror);
        createAndAddQuads(planes, positions, new Vector3f[]{vertex1, vertex5, vertex4}, cube.quads()[0], textureWidth, textureHeight, mirror);
        createAndAddQuads(planes, positions, new Vector3f[]{vertex2, vertex1, vertex3}, cube.quads()[3], textureWidth, textureHeight, mirror);
        createAndAddQuads(planes, positions, new Vector3f[]{vertex6, vertex2, vertex7}, cube.quads()[1], textureWidth, textureHeight, mirror);
        createAndAddQuads(planes, positions, new Vector3f[]{vertex5, vertex6, vertex8}, cube.quads()[2], textureWidth, textureHeight, mirror);

        Vector3f center = new Vector3f();
        center.add(vertex1);
        center.add(vertex2);
        center.add(vertex3);
        center.add(vertex4);
        center.add(vertex5);
        center.add(vertex6);
        center.add(vertex7);
        center.add(vertex8);
        center.mul(2);

        Plane aPlane = new Plane(direction.step(), -10);
        Plane bPlane = new Plane(direction.step(), 2);
        float fullSize = 12;
        float bendX = -0.5F;
        float bendY = 4;
        float bendZ = 0;
        float offsetX = -5.5F;
        float offsetY = -14;
        return new GeckoLibBendableCuboid(planes.toArray(new BendableCuboid.Quad[0]), positions.values().toArray(new RememberingPos[0]), bendX, bendY, bendZ, direction, aPlane, bPlane, fullSize, offsetX, offsetY, 0);
    }
    
    protected void createAndAddQuads(Collection<BendableCuboid.Quad> quads, HashMap<Vector3f, RememberingPos> positions, Vector3f[] edges, GeoQuad quad, float textureWidth, float textureHeight, boolean mirror) {
        float u1 = textureWidth * quad.vertices()[0].texU();
        float u2 = textureWidth * quad.vertices()[2].texU();
        float v1 = textureHeight * quad.vertices()[1].texV();
        float v2 = textureHeight * quad.vertices()[3].texV();
        float du = u2 < u1 ? 1 : -1;
        float dv = v1 < v2 ? 1 : -1;
        for (float localU = u2; localU != u1; localU += du) {
            for (float localV = v1; localV != v2; localV += dv) {
                float localU2 = localU + du;
                float localV2 = localV + dv;
                RememberingPos rp0 = getOrCreate(positions, transformVector(new Vector3f(edges[0]), new Vector3f(edges[1]), new Vector3f(edges[2]), u2, v1, u1, v2, localU2, localV));
                RememberingPos rp1 = getOrCreate(positions, transformVector(new Vector3f(edges[0]), new Vector3f(edges[1]), new Vector3f(edges[2]), u2, v1, u1, v2, localU2, localV2));
                RememberingPos rp2 = getOrCreate(positions, transformVector(new Vector3f(edges[0]), new Vector3f(edges[1]), new Vector3f(edges[2]), u2, v1, u1, v2, localU, localV2));
                RememberingPos rp3 = getOrCreate(positions, transformVector(new Vector3f(edges[0]), new Vector3f(edges[1]), new Vector3f(edges[2]), u2, v1, u1, v2, localU, localV));
                quads.add(new BendableCuboid.Quad(new RememberingPos[]{rp3, rp0, rp1, rp2}, localU2/textureWidth, localV/textureHeight, localU/textureWidth, localV2/textureHeight, mirror));
            }
        }
    }

    protected Vector3f transformVector(Vector3f pos, Vector3f vectorU, Vector3f vectorV, float u1, float v1, float u2, float v2, float u, float v) {
        pos.mul(16);
        vectorU.mul(16);
        vectorV.mul(16);
        vectorU.sub(pos);
        vectorU.mul((u - u1)/(u2-u1));
        vectorV.sub(pos);
        vectorV.mul((v - v1)/(v2-v1));
        pos.add(vectorU);
        pos.add(vectorV);
        return pos;
    }
}
