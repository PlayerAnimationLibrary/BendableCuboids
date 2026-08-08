package com.zigythebird.bendable_cuboids.fabric;

import com.google.j2objc.annotations.J2ObjCIncompatible;
import com.zigythebird.bendable_cuboids.BendableCuboidsMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

@J2ObjCIncompatible
public class BendableCuboidsModFabric extends BendableCuboidsMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("skinlayers3d")) setupSkinLayersTransformer();
    }
}
