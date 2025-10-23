package com.zigythebird.bendable_cuboids.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.SodiumHelper;
import com.zigythebird.bendable_cuboids.impl.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.Function;

import static com.zigythebird.bendable_cuboids.impl.Quad.createAndAddQuads;

@Mixin(ModelPart.Cube.class)
public class CubeMixin implements BendableCube, SodiumHelper {
    @Shadow
    @Final
    public float minX;
    @Shadow
    @Final
    public float minY;
    @Shadow
    @Final
    public float minZ;
    @Shadow
    @Final
    public float maxX;
    @Shadow
    @Final
    public float maxY;
    @Shadow
    @Final
    public float maxZ;

    @Unique
    private BendableCuboidData bc$data;

    @Unique
    private final Vector3f[] bc$vertices = new Vector3f[8];
    @Unique
    @Nullable
    private Quad[] sides;
    @Unique
    @Nullable
    private RememberingPos[] positions;

    @Unique
    protected float bc$fixX;
    @Unique
    protected float bc$fixY;
    @Unique
    protected float bc$fixZ;
    @Unique
    protected Plane bc$basePlane;
    @Unique
    protected Plane bc$otherPlane;
    @Unique
    protected float bc$fullSize;

    @Unique
    protected Direction bc$direction;
    @Unique
    protected int bc$pivot;
    @Unique
    protected float bc$bend;

    @Unique
    private boolean bc$useSodiumRendering = true;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void constructor(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> visibleFaces, CallbackInfo ci){
        this.bc$data = new BendableCuboidData(u, v, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, textureWidth, textureHeight, visibleFaces);
    }

    @Override
    public void rebuild(@NotNull Direction direction, int point) {
        if (this.sides == null || this.positions == null) bc$build();

        if (this.bc$direction == direction && this.bc$pivot == point) return;
        this.bc$direction = Objects.requireNonNull(direction);
        this.bc$pivot = point;

        direction = Direction.UP;

        Vector3f pivot = new Vector3f(0, 0, 0);
        if (point >= 0) {
            float size = direction.step().mul(bc$data.sizeX(), bc$data.sizeY(), bc$data.sizeZ()).length();
            if (point <= size) {
                pivot = direction.step().mul(size - (point * 2));
                bc$vertices[6] = bc$vertices[6].sub(pivot);
            }
        }
        this.bc$basePlane = new Plane(direction.step(), bc$vertices[6]);
        this.bc$otherPlane = new Plane(direction.step(), bc$vertices[0]);

        this.bc$fullSize = -direction.step().dot(bc$vertices[0]) + direction.step().dot(bc$vertices[6]);
        this.bc$fixX = (bc$data.sizeX() + minX + minX - pivot.x())/2;
        this.bc$fixY = (bc$data.sizeY() + minY + minY - pivot.y())/2;
        this.bc$fixZ = (bc$data.sizeZ() + minZ + minZ - pivot.z())/2;
    }

    @Unique
    private void bc$build() {
        List<Quad> planes = new ArrayList<>();
        Map<Vector3f, RememberingPos> positions = new HashMap<>();
        float pminX = minX - bc$data.extraX(), pminY = minY - bc$data.extraY(), pminZ = minZ - bc$data.extraZ(), pmaxX = maxX + bc$data.extraX(), pmaxY = maxY + bc$data.extraY(), pmaxZ = maxZ + bc$data.extraZ();
        if (bc$data.mirror()) {
            float tmp = pminX;
            pminX = pmaxX;
            pmaxX = tmp;
        }

        //this is copy from MC's cuboid constructor
        this.bc$vertices[0] = new Vector3f(pminX, pminY, pminZ); //west south down
        this.bc$vertices[1] = new Vector3f(pmaxX, pminY, pminZ); //east south down
        this.bc$vertices[2] = new Vector3f(pmaxX, pmaxY, pminZ); //east south up
        this.bc$vertices[3] = new Vector3f(pminX, pmaxY, pminZ); //west south up
        this.bc$vertices[4] = new Vector3f(pminX, pminY, pmaxZ); //west north down
        this.bc$vertices[5] = new Vector3f(pmaxX, pminY, pmaxZ); //east north down
        this.bc$vertices[6] = new Vector3f(pmaxX, pmaxY, pmaxZ); //east north up
        this.bc$vertices[7] = new Vector3f(pminX, pmaxY, pmaxZ); //west north up

        float j = bc$data.u();
        float k = bc$data.u() + bc$data.sizeZ();
        float l = bc$data.u() + bc$data.sizeZ() + bc$data.sizeX();
        float m = bc$data.u() + bc$data.sizeZ() + bc$data.sizeX() + bc$data.sizeX();
        float n = bc$data.u() + bc$data.sizeZ() + bc$data.sizeX() + bc$data.sizeZ();
        float o = bc$data.u() + bc$data.sizeZ() + bc$data.sizeX() + bc$data.sizeZ() + bc$data.sizeX();
        float p = bc$data.v();
        float q = bc$data.v() + bc$data.sizeZ();
        float r = bc$data.v() + bc$data.sizeZ() + bc$data.sizeY();
        float textureWidth = bc$data.textureWidth();
        float textureHeight = bc$data.textureHeight();
        boolean mirror = bc$data.mirror();
        if (bc$data.visibleFaces().contains(Direction.DOWN)) createAndAddQuads(planes, positions, new Vector3f[]{bc$vertices[5], bc$vertices[4], bc$vertices[1]}, k, p, l, q, textureWidth, textureHeight, mirror); //down
        if (bc$data.visibleFaces().contains(Direction.UP)) createAndAddQuads(planes, positions, new Vector3f[]{bc$vertices[2], bc$vertices[3], bc$vertices[6]}, l, q, m, p, textureWidth, textureHeight, mirror); //up
        if (bc$data.visibleFaces().contains(Direction.WEST)) createAndAddQuads(planes, positions, new Vector3f[]{bc$vertices[0], bc$vertices[4], bc$vertices[3]}, j, q, k, r, textureWidth, textureHeight, mirror); //west
        if (bc$data.visibleFaces().contains(Direction.NORTH)) createAndAddQuads(planes, positions, new Vector3f[]{bc$vertices[1], bc$vertices[0], bc$vertices[2]}, k, q, l, r, textureWidth, textureHeight, mirror); //north
        if (bc$data.visibleFaces().contains(Direction.EAST)) createAndAddQuads(planes, positions, new Vector3f[]{bc$vertices[5], bc$vertices[1], bc$vertices[6]}, l, q, n, r, textureWidth, textureHeight, mirror); //east
        if (bc$data.visibleFaces().contains(Direction.SOUTH)) createAndAddQuads(planes, positions, new Vector3f[]{bc$vertices[4], bc$vertices[5], bc$vertices[7]}, n, q, o, r, textureWidth, textureHeight, mirror); //south

        this.sides = planes.toArray(new Quad[0]);
        this.positions = positions.values().toArray(new RememberingPos[0]);
        bc$iteratePositions(Function.identity());
    }

    @Override
    public void bc$useSodiumRendering(boolean use) {
        if (!use && this.sides == null) bc$build();
        this.bc$useSodiumRendering = use;
    }

    @WrapMethod(method = "compile")
    private void bc$render(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, int color, Operation<Void> original) {
        if ((this.bc$useSodiumRendering && this.bc$bend == 0) || this.sides == null) {
            original.call(pose, buffer, packedLight, packedOverlay, color);
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
        if (this.bc$bend == bendValue) return;

        this.bc$bend = bendValue;
        bc$iteratePositions(BendUtil.getBend(this, bendValue));
    }

    @Override
    public Direction getBendDirection() {
        return this.bc$direction;
    }

    @Override
    public int getBendPivot() {
        return this.bc$pivot;
    }

    @Override
    public float getBendX() {
        return this.bc$fixX;
    }

    @Override
    public float getBendY() {
        return this.bc$fixY;
    }

    @Override
    public float getBendZ() {
        return this.bc$fixZ;
    }

    @Override
    public Plane getBasePlane() {
        return this.bc$basePlane;
    }

    @Override
    public Plane getOtherPlane() {
        return this.bc$otherPlane;
    }

    @Override
    public float bendHeight() {
        return this.bc$fullSize;
    }

    @Unique
    public void bc$iteratePositions(Function<Vector3f, Vector3f> function) {
        if (this.positions == null) return;
        for (RememberingPos pos : this.positions) {
            pos.setPos(function.apply(pos.getOriginalPos()));
        }
    }

    @Override
    public float getBend() {
        return this.bc$bend;
    }
}
