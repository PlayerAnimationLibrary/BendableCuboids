package com.zigythebird.bendable_cuboids;

import com.zigythebird.bendable_cuboids.impl.compatibility.skinlayers.SkinLayersCompat;
import net.minecraft.client.model.geom.ModelPart;

public abstract class BendableCuboidsMod {
    public static ModelPart currentModelPart;

    public void setupSkinLayersTransformer() {
        try {
            SkinLayersCompat.setupTransformer();
        } catch (Throwable ignored) {}
    }
}
