package com.zigythebird.bendable_cuboids.api;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import java.util.Map;

public interface ILayerDefinition {
    ModelPart bakeRootWithBends(Map<String, Pair<Direction, Integer>> cuboidDataMap);
}
