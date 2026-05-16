package com.zigythebird.bendable_cuboids;

import com.google.j2objc.annotations.J2ObjCIncompatible;
import com.zigythebird.bendable_cuboids.impl.compatibility.SkinLayersCompat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@J2ObjCIncompatible
public abstract class BendableCuboidsMod {
    public static final Logger LOGGER = LoggerFactory.getLogger("BendableCuboids");

    public void setupSkinLayersTransformer() {
        try {
            SkinLayersCompat.setupTransformer();
        } catch (Throwable ignored) {}
    }
}
