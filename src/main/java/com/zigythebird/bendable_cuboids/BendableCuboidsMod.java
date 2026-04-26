package com.zigythebird.bendable_cuboids;

import com.zigythebird.bendable_cuboids.impl.compatibility.SkinLayersCompat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BendableCuboidsMod {
    public static final Logger LOGGER = LoggerFactory.getLogger("BendableCuboids");

    public void setupSkinLayersTransformer() {
        try {
            SkinLayersCompat.setupTransformer();
        } catch (Throwable ignored) {}
    }
}
