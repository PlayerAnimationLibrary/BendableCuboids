package com.zigythebird.bendable_cuboids.mixin;

import com.zigythebird.bendable_cuboids.api.ILayerDefinition;
import com.zigythebird.bendable_cuboids.api.IPartDefinition;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(LayerDefinition.class)
public class LayerDefinitionMixin implements ILayerDefinition {
    @Shadow @Final private MeshDefinition mesh;

    @Shadow @Final private MaterialDefinition material;

    @Override
    public ModelPart bakeRootWithBends(Map<String, Pair<Direction, Integer>> cuboidDataMap, Set<String> noBends) {
        return ((IPartDefinition)this.mesh.getRoot()).bakeBendablePart(this.material.xTexSize, this.material.yTexSize, cuboidDataMap, noBends, "root");
    }
}
