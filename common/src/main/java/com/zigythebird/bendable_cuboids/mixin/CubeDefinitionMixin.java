package com.zigythebird.bendable_cuboids.mixin;

import com.zigythebird.bendable_cuboids.api.ICubeDefinition;
import com.zigythebird.bendable_cuboids.impl.BendableCuboid;
import com.zigythebird.bendable_cuboids.impl.VanillaBendableCuboid;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(CubeDefinition.class)
public class CubeDefinitionMixin implements ICubeDefinition {
    @Shadow @Final private UVPair texCoord;

    @Shadow @Final private Vector3fc origin;

    @Shadow @Final private Vector3fc dimensions;

    @Shadow @Final private CubeDeformation grow;

    @Shadow @Final private boolean mirror;

    @Shadow @Final private UVPair texScale;

    @Shadow @Final private Set<Direction> visibleFaces;

    @Override
    public VanillaBendableCuboid bakeBendableCuboid(int texWidth, int texHeight, Direction direction, int pivot) {
        return new VanillaBendableCuboid((int)this.texCoord.u(), (int)this.texCoord.v(), this.origin.x(), this.origin.y(), this.origin.z(), this.dimensions.x(), this.dimensions.y(), this.dimensions.z(), this.grow.growX, this.grow.growY, this.grow.growZ, this.mirror, (float)texWidth * this.texScale.u(), (float)texHeight * this.texScale.v(), this.visibleFaces, direction, pivot);
    }
}
