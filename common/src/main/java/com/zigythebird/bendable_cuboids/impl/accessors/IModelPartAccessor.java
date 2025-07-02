package com.zigythebird.bendable_cuboids.impl.accessors;

import net.minecraft.client.model.geom.ModelPart;

import java.util.List;
import java.util.Map;

/**
 * Basic operation to access cuboid in ModelPart
 */
public interface IModelPartAccessor {
    List<ModelPart.Cube> bendableCuboids$getCuboids();

    Map<String, ModelPart> bendableCuboids$getChildren(); //easy to search in it :D
}
