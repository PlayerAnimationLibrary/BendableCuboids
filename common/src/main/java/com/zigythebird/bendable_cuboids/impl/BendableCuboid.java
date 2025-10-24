package com.zigythebird.bendable_cuboids.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.SodiumHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import java.util.*;
import java.util.function.Function;

import static com.zigythebird.bendable_cuboids.impl.Quad.createAndAddQuads;

public class BendableCuboid extends ModelPart.Cube implements BendableCube, SodiumHelper {
    @Nullable
    private final Quad[] sides;

    @Nullable
    private final RememberingPos[] positions;

    protected final float fixX;
    protected final float fixY;
    protected final float fixZ;
    protected final Plane basePlane;
    protected final Plane otherPlane;
    protected final float fullSize;

    protected final Direction direction;
    protected final int pivot;
    protected float bend;

    private boolean useSodiumRendering = true;
    
    public BendableCuboid(int texCoordU, int texCoordV, float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, float growX, float growY, float growZ, boolean mirror, float texScaleU, float texScaleV, Set<Direction> visibleFaces, Direction direction, int pivot) {
        super(texCoordU, texCoordV, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, growX, growY, growZ, mirror, texScaleU, texScaleV, visibleFaces);

        List<Quad> planes = new ArrayList<>();
        Map<Vector3f, RememberingPos> positions = new HashMap<>();
        float pminX = minX - growX, pminY = minY - growY, pminZ = minZ - growZ, pmaxX = maxX + growX, pmaxY = maxY + growY, pmaxZ = maxZ + growZ;
        if (mirror) {
            float tmp = pminX;
            pminX = pmaxX;
            pmaxX = tmp;
        }

        Vector3f[] vertices = new Vector3f[8];
        //this is copy from MC's cuboid constructor
        vertices[0] = new Vector3f(pminX, pminY, pminZ); //west south down
        vertices[1] = new Vector3f(pmaxX, pminY, pminZ); //east south down
        vertices[2] = new Vector3f(pmaxX, pmaxY, pminZ); //east south up
        vertices[3] = new Vector3f(pminX, pmaxY, pminZ); //west south up
        vertices[4] = new Vector3f(pminX, pminY, pmaxZ); //west north down
        vertices[5] = new Vector3f(pmaxX, pminY, pmaxZ); //east north down
        vertices[6] = new Vector3f(pmaxX, pmaxY, pmaxZ); //east north up
        vertices[7] = new Vector3f(pminX, pmaxY, pmaxZ); //west north up

        float j = texCoordU;
        float k = texCoordU + dimensionZ;
        float l = texCoordU + dimensionZ + dimensionX;
        float m = texCoordU + dimensionZ + dimensionX + dimensionX;
        float n = texCoordU + dimensionZ + dimensionX + dimensionZ;
        float o = texCoordU + dimensionZ + dimensionX + dimensionZ + dimensionX;
        float p = texCoordV;
        float q = texCoordV + dimensionZ;
        float r = texCoordV + dimensionZ + dimensionY;
        if (visibleFaces.contains(Direction.DOWN)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[5], vertices[4], vertices[1]}, k, p, l, q, texScaleU, texScaleV, mirror); //down
        if (visibleFaces.contains(Direction.UP)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[2], vertices[3], vertices[6]}, l, q, m, p, texScaleU, texScaleV, mirror); //up
        if (visibleFaces.contains(Direction.WEST)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[0], vertices[4], vertices[3]}, j, q, k, r, texScaleU, texScaleV, mirror); //west
        if (visibleFaces.contains(Direction.NORTH)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[1], vertices[0], vertices[2]}, k, q, l, r, texScaleU, texScaleV, mirror); //north
        if (visibleFaces.contains(Direction.EAST)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[5], vertices[1], vertices[6]}, l, q, n, r, texScaleU, texScaleV, mirror); //east
        if (visibleFaces.contains(Direction.SOUTH)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[4], vertices[5], vertices[7]}, n, q, o, r, texScaleU, texScaleV, mirror); //south

        this.sides = planes.toArray(new Quad[0]);
        this.positions = positions.values().toArray(new RememberingPos[0]);
        iteratePositions(Function.identity());

        this.direction = Objects.requireNonNull(direction);
        this.pivot = pivot;
        direction = Direction.UP;

        Vector3f pivotVec = new Vector3f();
        if (pivot >= 0) {
            float size = direction.step().mul(dimensionX, dimensionY, dimensionZ).length();
            if (pivot <= size) {
                pivotVec = direction.step().mul(size - (pivot * 2));
                vertices[6] = vertices[6].sub(pivotVec);
            }
        }
        this.basePlane = new Plane(direction.step(), vertices[6]);
        this.otherPlane = new Plane(direction.step(), vertices[0]);

        this.fullSize = -direction.step().dot(vertices[0]) + direction.step().dot(vertices[6]);
        this.fixX = (dimensionX + minX + minX - pivotVec.x())/2;
        this.fixY = (dimensionY + minY + minY - pivotVec.y())/2;
        this.fixZ = (dimensionZ + minZ + minZ - pivotVec.z())/2;
    }

    @Override
    public void bc$useSodiumRendering(boolean use) {
        this.useSodiumRendering = use;
    }

    @Override
    public void compile(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        if ((this.useSodiumRendering && this.bend == 0) || this.sides == null) {
            super.compile(pose, buffer, packedLight, packedOverlay, color);
            return;
        }

        for (Quad quad : this.sides) {
            quad.render(pose, buffer, packedLight, packedOverlay, color);
        }
    }

    /**
     * Apply bend on this cuboid
     * Values are in radians
     * @param bendValue bend value (Same as rotX)
     */
    @Override
    public void applyBend(float bendValue) {
        // Don't enable bend until rotation is bigger than epsilon.
        // This should avoid unnecessary heavy calculations.
        if (Math.abs(bendValue) < 0.0001f) bendValue = 0;
        if (this.bend == bendValue) return;

        this.bend = bendValue;
        iteratePositions(BendUtil.getBend(this, bendValue));
    }

    @Override
    public Direction getBendDirection() {
        return this.direction;
    }

    @Override
    public int getBendPivot() {
        return this.pivot;
    }

    @Override
    public float getBendX() {
        return this.fixX;
    }

    @Override
    public float getBendY() {
        return this.fixY;
    }

    @Override
    public float getBendZ() {
        return this.fixZ;
    }

    @Override
    public Plane getBasePlane() {
        return this.basePlane;
    }

    @Override
    public Plane getOtherPlane() {
        return this.otherPlane;
    }

    @Override
    public float bendHeight() {
        return this.fullSize;
    }

    @Override
    public float getBend() {
        return this.bend;
    }

    public void iteratePositions(Function<Vector3f, Vector3f> function) {
        if (this.positions == null) return;
        for (RememberingPos pos : this.positions) {
            pos.setPos(function.apply(pos.getOriginalPos()));
        }
    }

    private void resetBend() {
        if (this.positions == null) return;
        for (RememberingPos pos : this.positions) {
            pos.setPos(pos.getOriginalPos());
        }
    }
}
