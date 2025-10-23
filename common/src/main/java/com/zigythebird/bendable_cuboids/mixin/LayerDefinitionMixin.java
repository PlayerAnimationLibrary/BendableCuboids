package com.zigythebird.bendable_cuboids.mixin;

import com.zigythebird.bendable_cuboids.api.ILayerDefinition;
import com.zigythebird.bendable_cuboids.api.IPartDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LayerDefinition.class)
public class LayerDefinitionMixin implements ILayerDefinition {
    @Shadow @Final private MeshDefinition mesh;

    @Shadow @Final private MaterialDefinition material;

    @Override
    public ModelPart bakeRootWithBends() {
        return ((IPartDefinition)this.mesh.getRoot()).bakeBendablePart(this.material.xTexSize, this.material.yTexSize);
    }
}
