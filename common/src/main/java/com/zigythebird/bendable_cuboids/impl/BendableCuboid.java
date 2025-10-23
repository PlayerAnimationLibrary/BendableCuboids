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
    private final BendableCuboidData data;

    private final Vector3f[] vertices = new Vector3f[8];

    @Nullable
    private Quad[] sides;

    @Nullable
    private RememberingPos[] positions;

    protected float fixX;
    protected float fixY;
    protected float fixZ;
    protected Plane basePlane;
    protected Plane otherPlane;
    protected float fullSize;

    protected Direction direction;
    protected int pivot;
    protected float bend;

    private boolean useSodiumRendering = true;
    
    public BendableCuboid(int texCoordU, int texCoordV, float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, float growX, float growY, float growZ, boolean mirror, float texScaleU, float texScaleV, Set<Direction> visibleFaces) {
        super(texCoordU, texCoordV, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, growX, growY, growZ, mirror, texScaleU, texScaleV, visibleFaces);
        this.data = new BendableCuboidData(texCoordU, texCoordV, dimensionX, dimensionY, dimensionZ, growX, growY, growZ, mirror, texScaleU, texScaleV, visibleFaces);
    }

    @Override
    public void rebuild(@NotNull Direction direction, int point) {
        if (this.sides == null || this.positions == null) build();

        if (this.direction == direction && this.pivot == point) return;
        this.direction = Objects.requireNonNull(direction);
        this.pivot = point;

        direction = Direction.UP;

        Vector3f pivot = new Vector3f(0, 0, 0);
        if (point >= 0) {
            float size = direction.step().mul(data.sizeX(), data.sizeY(), data.sizeZ()).length();
            if (point <= size) {
                pivot = direction.step().mul(size - (point * 2));
                vertices[6] = vertices[6].sub(pivot);
            }
        }
        this.basePlane = new Plane(direction.step(), vertices[6]);
        this.otherPlane = new Plane(direction.step(), vertices[0]);

        this.fullSize = -direction.step().dot(vertices[0]) + direction.step().dot(vertices[6]);
        this.fixX = (data.sizeX() + minX + minX - pivot.x())/2;
        this.fixY = (data.sizeY() + minY + minY - pivot.y())/2;
        this.fixZ = (data.sizeZ() + minZ + minZ - pivot.z())/2;
    }

    private void build() {
        List<Quad> planes = new ArrayList<>();
        Map<Vector3f, RememberingPos> positions = new HashMap<>();
        float pminX = minX - data.extraX(), pminY = minY - data.extraY(), pminZ = minZ - data.extraZ(), pmaxX = maxX + data.extraX(), pmaxY = maxY + data.extraY(), pmaxZ = maxZ + data.extraZ();
        if (data.mirror()) {
            float tmp = pminX;
            pminX = pmaxX;
            pmaxX = tmp;
        }

        //this is copy from MC's cuboid constructor
        this.vertices[0] = new Vector3f(pminX, pminY, pminZ); //west south down
        this.vertices[1] = new Vector3f(pmaxX, pminY, pminZ); //east south down
        this.vertices[2] = new Vector3f(pmaxX, pmaxY, pminZ); //east south up
        this.vertices[3] = new Vector3f(pminX, pmaxY, pminZ); //west south up
        this.vertices[4] = new Vector3f(pminX, pminY, pmaxZ); //west north down
        this.vertices[5] = new Vector3f(pmaxX, pminY, pmaxZ); //east north down
        this.vertices[6] = new Vector3f(pmaxX, pmaxY, pmaxZ); //east north up
        this.vertices[7] = new Vector3f(pminX, pmaxY, pmaxZ); //west north up

        float j = data.u();
        float k = data.u() + data.sizeZ();
        float l = data.u() + data.sizeZ() + data.sizeX();
        float m = data.u() + data.sizeZ() + data.sizeX() + data.sizeX();
        float n = data.u() + data.sizeZ() + data.sizeX() + data.sizeZ();
        float o = data.u() + data.sizeZ() + data.sizeX() + data.sizeZ() + data.sizeX();
        float p = data.v();
        float q = data.v() + data.sizeZ();
        float r = data.v() + data.sizeZ() + data.sizeY();
        float textureWidth = data.textureWidth();
        float textureHeight = data.textureHeight();
        boolean mirror = data.mirror();
        if (data.visibleFaces().contains(Direction.DOWN)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[5], vertices[4], vertices[1]}, k, p, l, q, textureWidth, textureHeight, mirror); //down
        if (data.visibleFaces().contains(Direction.UP)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[2], vertices[3], vertices[6]}, l, q, m, p, textureWidth, textureHeight, mirror); //up
        if (data.visibleFaces().contains(Direction.WEST)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[0], vertices[4], vertices[3]}, j, q, k, r, textureWidth, textureHeight, mirror); //west
        if (data.visibleFaces().contains(Direction.NORTH)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[1], vertices[0], vertices[2]}, k, q, l, r, textureWidth, textureHeight, mirror); //north
        if (data.visibleFaces().contains(Direction.EAST)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[5], vertices[1], vertices[6]}, l, q, n, r, textureWidth, textureHeight, mirror); //east
        if (data.visibleFaces().contains(Direction.SOUTH)) createAndAddQuads(planes, positions, new Vector3f[]{vertices[4], vertices[5], vertices[7]}, n, q, o, r, textureWidth, textureHeight, mirror); //south

        this.sides = planes.toArray(new Quad[0]);
        this.positions = positions.values().toArray(new RememberingPos[0]);
        iteratePositions(Function.identity());
    }

    @Override
    public void bc$useSodiumRendering(boolean use) {
        if (!use && this.sides == null) build();
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
        if (this.bend == bendValue) return;
        // Don't enable bend until rotation is bigger than epsilon.
        // This should avoid unnecessary heavy calculations.
        if (Math.abs(bendValue) < 0.0001f) {
            this.bend = 0;
            this.resetBend();
            return;
        }

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
