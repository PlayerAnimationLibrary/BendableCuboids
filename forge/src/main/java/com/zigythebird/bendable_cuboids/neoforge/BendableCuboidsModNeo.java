package com.zigythebird.bendable_cuboids.neoforge;

import com.zigythebird.bendable_cuboids.BendableCuboidsMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

@Mod(value = "bendable_cuboids", dist = Dist.CLIENT)
public class BendableCuboidsModNeo extends BendableCuboidsMod {
    public BendableCuboidsModNeo() {
        if (FMLLoader.getCurrent().getLoadingModList().getModFileById("skinlayers3d") != null) setupSkinLayersTransformer();
    }
}
