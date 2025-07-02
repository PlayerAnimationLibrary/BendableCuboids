package com.zigythebird.bendable_cuboids.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.api.MutableCuboid;
import com.zigythebird.bendable_cuboids.impl.accessors.IModelPartAccessor;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public abstract class IModelPartMixin implements IModelPartAccessor {
    @Shadow @Final private Map<String, ModelPart> children;

    @Shadow @Final private List<ModelPart.Cube> cubes;

    @Unique
    private boolean bendableCuboids$hasMutatedCuboid = false;

    @Override
    public List<ModelPart.Cube> bendableCuboids$getCuboids() {
        bendableCuboids$hasMutatedCuboid = true;
        return cubes;
    }

    @Override
    public Map<String, ModelPart> bendableCuboids$getChildren() {
        return children;
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyTransformExtended(ModelPart part, CallbackInfo ci){
        if (((IModelPartAccessor)part).bendableCuboids$getCuboids() == null || cubes == null) return; // Not copying state
        Iterator<ModelPart.Cube> iterator0 = ((IModelPartAccessor)part).bendableCuboids$getCuboids().iterator();
        Iterator<ModelPart.Cube> iterator1 = cubes.iterator();

        while (iterator0.hasNext() && iterator1.hasNext()){
            MutableCuboid cuboid1 = (MutableCuboid) iterator1.next();
            MutableCuboid cuboid0 = (MutableCuboid) iterator0.next();
            cuboid1.bendableCuboids$copyStateFrom(cuboid0);
        }
    }

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;compile(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"), require = 0) //It might not find anything if OF already broke the game
    private void redirectRenderCuboids(ModelPart modelPart, PoseStack.Pose entry, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original){
        bendableCuboids$redirectedFunction(modelPart, entry, vertexConsumer, light, overlay, color, original);
    }

    @Unique
    private void bendableCuboids$redirectedFunction(ModelPart modelPart, PoseStack.Pose entry, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original) {
        if(!bendableCuboids$hasMutatedCuboid || cubes.size() == 1 && ((MutableCuboid)cubes.get(0)).bendableCuboids$getActiveMutator() == null){
            original.call(modelPart, entry, vertexConsumer, light, overlay, color);
        }
        else {
            for(ModelPart.Cube cuboid:cubes){
                cuboid.compile(entry, vertexConsumer, light, overlay, color);
            }
        }
    }
}
