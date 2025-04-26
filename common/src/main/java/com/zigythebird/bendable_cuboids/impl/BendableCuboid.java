package com.zigythebird.bendable_cuboids.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Function;

/**
 * Should be pretty self-explanatory...
 * Bend XYZ are the coordinates of the bend's center
 * If you don't know the math behind it, don't try to edit.
 * <p>
 * Use {@link BendableCuboid#applyBend} to bend the cube
 */
public class BendableCuboid {
    protected final Quad[] sides;
    protected final RememberingPos[] positions;

    //to shift the matrix to the center axis
    protected final float fixX;
    protected final float fixY;
    protected final float fixZ;
    protected final Direction direction;
    protected final Plane basePlane;
    protected final Plane otherPlane;
    protected final float fullSize;

    protected float bend, bendAxis;

    /**
     * To see how you can make existing model parts bendable look at {@link PlayerBendHelper}
     * Or you can use {@link BendableCuboidBuilder#build}.
     */
    public BendableCuboid(Quad[] sides, RememberingPos[] positions, float fixX, float fixY, float fixZ, Direction direction, Plane basePlane, Plane otherPlane, float fullSize) {
        this.sides = sides;
        this.positions = positions;
        this.fixX = fixX;
        this.fixY = fixY;
        this.fixZ = fixZ;
        this.direction = direction;
        this.basePlane = basePlane;
        this.otherPlane = otherPlane;
        this.fullSize = fullSize;

        this.applyBend(0, 0);//Init values to render
    }

    /**
     * Apply bend on this cuboid
     * Values are in radians
     * @param bendAxis bend axis (rotY can be used instead)
     * @param bendValue bend value (Same as rotX)
     * @return Transformation matrix for transforming children
     */
    public Matrix4f applyBend(float bendAxis, float bendValue) {
        this.bend = bendValue; this.bendAxis = bendAxis;
        BendApplier bendApplier = BendUtil.getBend(this, bendAxis, bendValue);
        this.iteratePositions(bendApplier.consumer());
        return bendApplier.matrix4f();
    }

    public Matrix4f applyBendDegrees(float bendAxis, float bendValue) {
        return applyBend(bendAxis * Mth.DEG_TO_RAD, bendValue * Mth.DEG_TO_RAD);
    }

    public Direction getBendDirection() {
        return this.direction;
    }

    public float getBendX() {
        return fixX;
    }

    public float getBendY() {
        return fixY;
    }

    public float getBendZ() {
        return fixZ;
    }

    public Plane getBasePlane() {
        return basePlane;
    }

    public Plane getOtherSidePlane() {
        return otherPlane;
    }

    /**
     * Distance between the two opposite surface of the cuboid.
     * Calculate two plane distance is inefficient.
     * Try to override it (If you have size)
     * @return the size of the cube
     */
    public float bendHeight() {
        return fullSize;
    }

    public boolean isBendInverted() {
        return getBendDirection() == Direction.UP || getBendDirection() == Direction.SOUTH || getBendDirection() == Direction.EAST;
    }

    public void iteratePositions(Function<Vector3f, Vector3f> function) {
        for(RememberingPos pos: positions){
            pos.setPos(function.apply(pos.getOriginalPos()));
        }
    }

    public float getBend() {
        return bend;
    }

    public float getBendAxis() {
        return bendAxis;
    }

    public void render(PoseStack.Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        for(Quad quad:sides) {
            quad.render(matrices, vertexConsumer, light, overlay, color);
        }
    }

    public void copyState(BendableCuboid other) {
        if(other instanceof BendableCuboid b){
            this.applyBend(b.bendAxis, b.bend); //This works only in J16 or higher
        }
    }

    /*
     * A replica of {@link ModelPart.Quad}
     * with IVertex and render()
     */
    public static class Quad {
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
    }
}
