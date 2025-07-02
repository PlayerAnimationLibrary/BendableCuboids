package com.zigythebird.bendable_cuboids.mixin.geckolib;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.impl.BendUtil;
import com.zigythebird.bendable_cuboids.impl.compatibility.geckolib.GeckoLibBendableCuboid;
import com.zigythebird.bendable_cuboids.impl.compatibility.geckolib.GeckoLibBendableCuboidBuilder;
import com.zigythebird.bendable_cuboids.impl.compatibility.geckolib.GeckoRenderUtil;
import com.zigythebird.bendable_cuboids.impl.compatibility.geckolib.GeoCubeAccessor;
import com.zigythebird.playeranim.accessors.IAnimatedPlayer;
import com.zigythebird.playeranim.bones.PlayerAnimBone;
import com.zigythebird.playeranim.math.Pair;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.ArrayList;
import java.util.List;

@Mixin(GeoRenderer.class)
public interface GeoRendererMixin_geckoOnly<T extends GeoAnimatable> {
    @Inject(method = "defaultRender", at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/renderer/GeoRenderer;preRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIII)V"), remap = false)
    private void initBend(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float partialTick, int packedLight, CallbackInfo ci, @Local BakedGeoModel model) {
        if ((GeoRenderer)(Object)this instanceof GeoArmorRenderer<?> armorRenderer) {
            bendableCuboids$initBend(List.of(armorRenderer.getGeoModel().getBone("armorLeftArm").orElse(null)), Direction.UP, "left_arm", model);
            bendableCuboids$initBend(List.of(armorRenderer.getGeoModel().getBone("armorRightArm").orElse(null)), Direction.UP, "right_arm", model);
            bendableCuboids$initBend(List.of(armorRenderer.getGeoModel().getBone("armorBody").orElse(null)), Direction.DOWN, "torso", model);
            bendableCuboids$initBend(List.of(armorRenderer.getGeoModel().getBone("armorRightLeg").orElse(null)), Direction.UP, "right_leg", model);
            bendableCuboids$initBend(List.of(armorRenderer.getGeoModel().getBone("armorLeftLeg").orElse(null)), Direction.UP, "left_leg", model);
        }
    }

    @Inject(method = "renderRecursively", at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/util/RenderUtil;prepMatrixForBone(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/cache/object/GeoBone;)V"), remap = false)
    private void renderBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor, CallbackInfo ci) {
        if ((GeoRenderer)(Object)this instanceof GeoArmorRenderer<?> armorRenderer && armorRenderer.getCurrentEntity() instanceof AbstractClientPlayer player) {
            String name = bone.getName();
            if (name.equals("armorRightArm") || name.equals("armorLeftArm") || name.equals("armorHead")) {
                PlayerAnimBone bone1 = new PlayerAnimBone("torso");
                ((IAnimatedPlayer)player).playerAnimLib$getAnimManager().get3DTransform(bone1);
                if (Math.abs(bone1.getBend()) >= 0.0001f) {
                    BendUtil.applyBendToMatrix(poseStack, 0, 1.125F, 0, bone1.getBendAxis(), -bone1.getBend());
                }
            }
        }
    }

    @WrapWithCondition(method = "renderCubesOfBone", at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/renderer/GeoRenderer;renderCube(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/cache/object/GeoCube;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"), remap = false)
    private boolean renderCube(GeoRenderer instance, PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor, @Local(argsOnly = true) GeoBone geoBone) {
        if (instance instanceof GeoArmorRenderer<?> armorRenderer && armorRenderer.getCurrentEntity() instanceof AbstractClientPlayer player) {
            Pair<String, GeckoLibBendableCuboid> bendableCuboid = ((GeoCubeAccessor)(Object)cube).getBendableCuboid();
            if (bendableCuboid == null) return true;
            PlayerAnimBone bone = new PlayerAnimBone(bendableCuboid.getLeft());
            ((IAnimatedPlayer)player).playerAnimLib$getAnimManager().get3DTransform(bone);
            if (Math.abs(bone.getBend()) >= 0.0001f) {
                PoseStack offset = new PoseStack();
                offset.translate(-5.5, -14, 0);
                for (GeoBone bone1 : playerAnimLib$getParents(new ArrayList<>(),  geoBone)) {
                    GeckoRenderUtil.prepMatrixForBone(offset, bone1);
                }
                bendableCuboid.getRight().applyBend(bone.getBendAxis(), bone.getBend(), offset);
                bendableCuboid.getRight().render(poseStack.last(), buffer, packedLight, packedOverlay, renderColor);
                return false;
            }
        }
        return true;
    }

    @Unique
    private List<GeoBone> playerAnimLib$getParents(List<GeoBone> list, GeoBone bone) {
        if (bone == null || list.contains(bone) || bone.getName().equals("armorRightArm") || bone.getName().equals("armorLeftArm")
            || bone.getName().equals("armorRightLeg") || bone.getName().equals("armorLeftLeg") || bone.getName().equals("armorBody")) return list;
        list.addFirst(bone);
        return playerAnimLib$getParents(list, bone.getParent());
    }

    @Unique
    private void bendableCuboids$initBend(List<GeoBone> bones, Direction direction, String parent, BakedGeoModel model) {
        if (bones == null) return;
        for (GeoBone bone : bones) {
            if (bone == null) continue;
            for (GeoCube cube : bone.getCubes()) {
                if (((GeoCubeAccessor)(Object)cube).getBendableCuboid() != null) continue;
                GeckoLibBendableCuboidBuilder builder = new GeckoLibBendableCuboidBuilder();
                builder.setDirection(direction);
                ((GeoCubeAccessor)(Object)cube).setBendableCuboid(new Pair<>(parent, builder.build(cube, model, parent)));
            }
            bendableCuboids$initBend(bone.getChildBones(), direction, parent, model);
        }
    }
}
