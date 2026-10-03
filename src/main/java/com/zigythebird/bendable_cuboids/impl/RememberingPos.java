package com.zigythebird.bendable_cuboids.impl;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Objects;

public class RememberingPos {
    final Vector3f originPos;
    Vector3f currentPos = null;
    /** Point of the straight cube whose texture the vertex shows, or null while it shows its own. */
    Vector3f materialPos = null;

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
        return Objects.equals(currentPos, that.currentPos) && Objects.equals(materialPos, that.materialPos);
    }

    @Override
    public int hashCode() {
        int result = originPos.hashCode();
        result = 31 * result + (currentPos != null ? currentPos.hashCode() : 0);
        result = 31 * result + (materialPos != null ? materialPos.hashCode() : 0);
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

    /**
     * Where a bend slides the vertex along the cube's surface, its texture slides with it instead of stretching.
     * @param materialPos point of the straight cube whose texture the vertex shows
     */
    public void setMaterialPos(Vector3f materialPos) {
        this.materialPos = materialPos;
    }

    /**
     * @param gradient change of a texture coordinate per unit of the face the vertex lies on
     * @return how far that texture coordinate follows the vertex along the surface
     */
    public float textureOffset(Vector3fc gradient) {
        if (this.materialPos == null) return 0;
        return gradient.x()*(this.materialPos.x - this.originPos.x) + gradient.y()*(this.materialPos.y - this.originPos.y)
                + gradient.z()*(this.materialPos.z - this.originPos.z);
    }
}
