package com.zigythebird.bendable_cuboids.impl;

import org.joml.Vector3f;

/**
 * A plane in the 3d space.
 * Forms a vector and a position for distance calculation.
 */
public class Plane{
    public final Vector3f normal;
    private final float normDistance;

    public Plane(Vector3f normal, Vector3f position){
        this.normal = normal;
        this.normal.normalize();
        this.normDistance = -this.normal.dot(position);
    }

    public Plane(Vector3f normal, float normDistance) {
        this.normal = normal;
        this.normDistance = normDistance;
    }

    public Plane scaled(float scalar) {
        return new Plane(new Vector3f(normal), normDistance * scalar);
    }

    /**
     * This will return with the SIGNED distance
     * @param pos some pos
     * @return the distance between the pos and this plane
     */
    public float distanceTo(Vector3f pos){
        return normal.dot(pos) + normDistance;
    }

    /**
     * This will return with the SIGNED distance
     * @param otherPlane some plane
     * @return the distance between the two planes. 0 if not parallel
     */
    public float distanceTo(Plane otherPlane){
        Vector3f tmp = new Vector3f(this.normal);
        tmp.cross(otherPlane.normal);
        //if the lines are parallel
        if(tmp.dot(tmp) < 0.01){
            return this.normDistance + this.normal.dot(otherPlane.normal) * otherPlane.normDistance; //if the normals point to the opposite direction
        }
        else {
            return 0;
        }
    }
}
