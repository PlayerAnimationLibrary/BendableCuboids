package com.zigythebird.bendable_cuboids.impl;

import org.joml.Vector3f;

/**
 * This vertex's position can be changed.
 */
public class RepositionableVertex implements IVertex {
    public final float u;
    public final float v;
    protected final RememberingPos pos;

    public RepositionableVertex(float u, float v, RememberingPos pos) {
        this.u = u;
        this.v = v;
        this.pos = pos;
    }

    public RepositionableVertex remap(float u, float v){
        return new RepositionableVertex(u, v, this.pos);
    }

    public Vector3f getPos() {
        return pos.getPos();
    }

    public float getU() {
        return this.u;
    }

    public float getV() {
        return this.v;
    }
}
