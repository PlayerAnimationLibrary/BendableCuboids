package com.zigythebird.bendable_cuboids.impl;

import org.joml.Vector3f;

import java.util.Objects;

public class RememberingPos {
    final Vector3f originPos;
    Vector3f currentPos = null;

    public RememberingPos(Vector3f originPos) {
        this.originPos = originPos;
    }

    public RememberingPos(float x, float y, float z){
        this(new Vector3f(x, y, z));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RememberingPos that)) return false;

        if (!originPos.equals(that.originPos)) return false;
        return Objects.equals(currentPos, that.currentPos);
    }

    @Override
    public int hashCode() {
        int result = originPos.hashCode();
        result = 31 * result + (currentPos != null ? currentPos.hashCode() : 0);
        return result;
    }

    /**
     * @return Copy of the original position.
     */
    public Vector3f getOriginalPos() {
        return new Vector3f(originPos); //I won't let anyone modify the original.
    }

    public Vector3f getPos() {
        return currentPos;
    }

    public void setPos(Vector3f vector3f) {
        this.currentPos = vector3f;
    }
}
