package com.zigythebird.bendable_cuboids.impl.compatibility;

import com.zigythebird.bendable_cuboids.BendableCuboidsMod;
import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.BendableModelPart;
import com.zigythebird.bendable_cuboids.impl.BendUtil;
import com.zigythebird.bendable_cuboids.impl.RememberingPos;
import dev.tr7zw.skinlayers.api.MeshTransformer;
import dev.tr7zw.skinlayers.api.MeshTransformerProvider;
import dev.tr7zw.skinlayers.api.SkinLayersAPI;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SkinLayersCompat implements MeshTransformerProvider, MeshTransformer {
    protected final MeshTransformerProvider parent;
    protected @Nullable BendableCube original;
    protected @Nullable MeshTransformer transformer;

    protected SkinLayersCompat(MeshTransformerProvider parent) {
        this.parent = parent;
    }

    public static void setupTransformer() {
        SkinLayersAPI.setupMeshTransformerProvider(new SkinLayersCompat(SkinLayersAPI.getMeshTransformerProvider()));
    }

    @Override
    public MeshTransformer prepareTransformer(@Nullable ModelPart modelPart) {
        if (modelPart == null) {
            BendableCuboidsMod.LOGGER.error("3dskinlayers passed a null part of the model, please report this to TR!", new Throwable());
            return this.parent.prepareTransformer(null);
        }

        this.original = ((BendableModelPart) modelPart).bc$getCuboid(0);
        this.transformer = this.parent.prepareTransformer(modelPart);
        return this;
    }

    @Override
    public void transform(Vector3f vector3f, Vector4f[] vector4fs) {
        if (this.original == null || this.original.getBend() == 0) return;
        for (Vector4f vector4f : vector4fs) {
            vector4f.mul(16);
            RememberingPos pos = new RememberingPos(new Vector3f(vector4f.x, vector4f.y, vector4f.z));
            pos.setPos(BendUtil.getBend(this.original).apply(pos.getOriginalPos()));
            vector4f.set(pos.getPos(), vector4f.w);
            vector4f.div(16);
        }
        vector3f.set(calculateNormal(vector4fs));
    }

    @Override
    public void transform(ModelPart.Cube cube) {
        if (cube instanceof BendableCube bendableCube) {
            if (this.original == null || this.original.getBend() == 0) {
                bendableCube.applyBend(0);
                if (this.transformer != null) {
                    this.transformer.transform(cube);
                }
                return;
            }
            bendableCube.bc$copyState(this.original);
        }
    }

    public static Vector3f calculateNormal(Vector4f[] vertices) {
        Vector3f buf = new Vector3f(vertices[3].x, vertices[3].y, vertices[3].z);
        buf.negate();
        Vector3f vecB = new Vector3f(vertices[1].x, vertices[1].y, vertices[1].z);
        vecB.add(buf);
        buf = new Vector3f(vertices[2].x, vertices[2].y, vertices[2].z);
        buf.negate();
        Vector3f vecA = new Vector3f(vertices[0].x, vertices[0].y, vertices[0].z);
        vecA.add(buf);
        vecA.cross(vecB);
        // Return the cross-product, if it's zero then return anything non-zero to not cause crash...
        return vecA.normalize().isFinite() ? vecA : Direction.NORTH.step();
    }
}
