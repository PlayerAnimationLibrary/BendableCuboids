package com.zigythebird.bendable_cuboids.impl.compatibility.skinlayers;

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

import java.util.List;

public class SkinLayersCompat implements MeshTransformerProvider, MeshTransformer {
    private static final List<String> OVERLAY_NAMES = List.of("left_sleeve", "right_sleeve", "left_pants", "right_pants", "jacket", "hat");

    protected final MeshTransformerProvider parent;
    protected @Nullable BendableCube original;
    protected @Nullable MeshTransformer transformer;

    protected SkinLayersCompat(MeshTransformerProvider parent) {
        this.parent = parent;
    }

    public static void setupTransformer() {
        SkinLayersAPI.setupMeshTransformerProvider(new SkinLayersCompat(SkinLayersAPI.getMeshTransformerProvider()));
    }

    /**
     * @param modelPart is always null because 3dskinlayers is shit
     */
    @Override
    public MeshTransformer prepareTransformer(@Nullable ModelPart modelPart) {
        if (modelPart == null) modelPart = getOverlayPart(BendableCuboidsMod.currentModelPart);
        if (modelPart == null) return this.parent.prepareTransformer(null);

        this.original = ((BendableModelPart) modelPart).bc$getCuboid(0);
        this.transformer = this.parent.prepareTransformer(modelPart);
        return this;
    }

    @Override
    public void transform(Vector3f vector3f, Vector4f[] vector4fs) {
        if (this.original == null || this.original.getBend() == 0) {
            if (this.transformer != null) this.transformer.transform(vector3f, vector4fs);
            return;
        }

        for (Vector4f vector4f : vector4fs) {
            RememberingPos pos = new RememberingPos(new Vector3f(vector4f.x, vector4f.y, vector4f.z));
            BendUtil.getBend(this.original).applyTo(pos);
            vector4f.set(pos.getPos(), vector4f.w);
        }
        vector3f.set(calculateNormal(vector4fs));
    }

    @Override
    public void transform(ModelPart.Cube cube) {
        if (this.original == null || this.original.getBend() == 0) {
            ((BendableCube) cube).applyBend(0);
            if (this.transformer != null) {
                this.transformer.transform(cube);
            }
            return;
        }
        ((BendableCube) cube).copyState(this.original);
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

    public static ModelPart getOverlayPart(ModelPart modelPart) {
        if (modelPart == null) return null;
        for (String overlay : OVERLAY_NAMES) {
            if (modelPart.hasChild(overlay)) {
                return modelPart.getChild(overlay);
            }
        }
        return modelPart;
    }
}