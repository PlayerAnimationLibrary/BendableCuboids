package com.zigythebird.bendable_cuboids.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.api.MutableCuboid;
import com.zigythebird.bendable_cuboids.impl.BendableCuboid;
import com.zigythebird.bendable_cuboids.impl.BendableCuboidData;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

@Mixin(ModelPart.Cube.class)
public class CuboidMutator implements MutableCuboid {
    @Shadow
    @Final
    public float minX;
    @Shadow
    @Final
    public float minY;
    @Shadow
    @Final
    public float minZ;
    //Store the mutators and the mutator builders.

    @Unique
    private final HashMap<String, BendableCuboid> mutators = new HashMap<>();

    @Unique
    private final HashMap<String, Function<BendableCuboidData, BendableCuboid>> mutatorBuilders = new HashMap<>();

    @Unique
    private BendableCuboidData partData;

    @Nullable
    @Unique
    private BendableCuboid activeMutator;

    @Nullable
    @Unique
    private String activeMutatorID;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void constructor(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> visibleFaces, CallbackInfo ci){
        partData = new BendableCuboidData(u, v, minX, minY, minZ, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, textureWidth, textureHeight, visibleFaces);
    }

    @Override
    public boolean bendableCuboids$registerMutator(String name, Function<BendableCuboidData, BendableCuboid> builder) {
        if(mutatorBuilders.containsKey(name)) return false;
        if(builder == null) throw new NullPointerException("builder can not be null");
        mutatorBuilders.put(name, builder);
        return true;
    }

    @Override
    public boolean bendableCuboids$unregisterMutator(String name) {
        if(mutatorBuilders.remove(name) != null){
            if(name.equals(activeMutatorID)){
                activeMutator = null;
                activeMutatorID = null;
            }
            mutators.remove(name);

            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Tuple<String, BendableCuboid> bendableCuboids$getActiveMutator() {
        return activeMutator == null ? null : new Tuple<>(activeMutatorID, activeMutator);
    }

    @Override
    public boolean bendableCuboids$hasMutator(String key) {
        return mutators.containsKey(key) || mutatorBuilders.containsKey(key);
    }

    @Nullable
    @Override
    public Function<BendableCuboidData, BendableCuboid> bendableCuboids$getCuboidBuilder(String key) {
        return mutatorBuilders.get(key);
    }

    @Nullable
    @Override
    public BendableCuboid bendableCuboids$getMutator(String name) {
        return mutators.get(name);
    }

    @Nullable
    @Override
    public BendableCuboid bendableCuboids$getAndActivateMutator(@Nullable String name) {
        if(name == null){
            activeMutatorID = null;
            activeMutator = null;
            return null;
        }
        if(mutatorBuilders.containsKey(name)){
            if(!mutators.containsKey(name)){
                mutators.put(name, mutatorBuilders.get(name).apply(partData));
            }
            activeMutatorID = name;
            return activeMutator = mutators.get(name);
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void bendableCuboids$copyStateFrom(MutableCuboid other) {
        if(other.bendableCuboids$getActiveMutator() == null){
            activeMutator = null;
            activeMutatorID = null;
        }
        else {
            if(this.bendableCuboids$getAndActivateMutator(other.bendableCuboids$getActiveMutator().getA()) != null){
                activeMutator.copyState(other.bendableCuboids$getActiveMutator().getB());
            }
        }
    }

    @Inject(method = "compile", at = @At(value = "HEAD"), cancellable = true)
    private void renderRedirect(PoseStack.Pose entry, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci){
        if(bendableCuboids$getActiveMutator() != null){
            bendableCuboids$getActiveMutator().getB().render(entry, vertexConsumer, light, overlay, color);
            ci.cancel();
        }
    }
}
