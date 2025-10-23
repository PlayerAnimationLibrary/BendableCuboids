package com.zigythebird.bendable_cuboids.api;

import net.minecraft.client.model.geom.ModelPart;

public interface IPartDefinition {
    ModelPart bakeBendablePart(int texWidth, int texHeight);
}
