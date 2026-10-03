package com.zigythebird.bendable_cuboids.impl;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * This vertex's position can be changed.
 */
public class RepositionableVertex implements IVertex {
    /** Gradient of a texture coordinate that stays where it is when the vertex slides. */
    static final Vector3fc FIXED = new Vector3f();

    public final float u;
    public final float v;
    protected final RememberingPos pos;
    /** Change of u and of v per unit of the face the vertex lies on. */
    protected final Vector3fc uGradient;
    protected final Vector3fc vGradient;

    public RepositionableVertex(float u, float v, RememberingPos pos) {
        this(u, v, pos, FIXED, FIXED);
    }

    public RepositionableVertex(float u, float v, RememberingPos pos, Vector3fc uGradient, Vector3fc vGradient) {
        this.u = u;
        this.v = v;
        this.pos = pos;
        this.uGradient = uGradient;
        this.vGradient = vGradient;
    }

    public RepositionableVertex remap(float u, float v){
        return new RepositionableVertex(u, v, this.pos, this.uGradient, this.vGradient);
    }

    public Vector3f getPos() {
        return pos.getPos();
    }

    public float getU() {
        return this.u + this.pos.textureOffset(this.uGradient);
    }

    public float getV() {
        return this.v + this.pos.textureOffset(this.vGradient);
    }
}
