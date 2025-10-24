package com.zigythebird.bendable_cuboids.mixin;

import com.google.common.collect.ImmutableList;
import com.zigythebird.bendable_cuboids.api.ICubeDefinition;
import com.zigythebird.bendable_cuboids.api.IPartDefinition;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(PartDefinition.class)
public class PartDefinitionMixin implements IPartDefinition {
    @Shadow @Final private Map<String, PartDefinition> children;

    @Shadow @Final private List<CubeDefinition> cubes;

    @Shadow @Final private PartPose partPose;

    @Override
    public ModelPart bakeBendablePart(int texWidth, int texHeight) {
        Object2ObjectArrayMap<String, ModelPart> object2objectarraymap = this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (partDefinitionMap) -> ((IPartDefinition)partDefinitionMap.getValue()).bakeBendablePart(texWidth, texHeight), (modelPart, modelPart1) -> modelPart, Object2ObjectArrayMap::new));
        List<ModelPart.Cube> list = this.cubes.stream().map((definition) -> ((ICubeDefinition)definition).bakeBendableCuboid(texWidth, texHeight)).collect(ImmutableList.toImmutableList());
        ModelPart modelpart = new ModelPart(list, object2objectarraymap);
        modelpart.setInitialPose(this.partPose);
        modelpart.loadPose(this.partPose);
        return modelpart;
    }
}
