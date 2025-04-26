package com.zigythebird.bendable_cuboids.mixin.geckolib;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.bendable_cuboids.impl.BendableCuboid;
import com.zigythebird.bendable_cuboids.impl.compatibility.geckolib.GeckoLibBendableCuboidBuilder;
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

import java.util.List;

@Mixin(GeoRenderer.class)
public interface GeoRendererMixin_geckoOnly<T extends GeoAnimatable> {
    @Inject(method = "defaultRender", at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/renderer/GeoRenderer;preRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIII)V"))
    private void initBend(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float partialTick, int packedLight, CallbackInfo ci, @Local BakedGeoModel model) {
        if ((GeoRenderer)(Object)this instanceof GeoArmorRenderer<?> armorRenderer) {
            bendableCuboids$initBend(List.of(armorRenderer.getGeoModel().getBone("armorRightArm").orElse(null)), Direction.UP, "right_arm", model);
        }
    }

//    @Inject(method = "renderRecursively", at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/util/RenderUtil;prepMatrixForBone(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/cache/object/GeoBone;)V"))
//    private void renderBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor, CallbackInfo ci) {
//        if ((GeoRenderer)(Object)this instanceof GeoArmorRenderer<?> armorRenderer && armorRenderer.getCurrentEntity() instanceof AbstractClientPlayer player) {
//            String name = bone.getName();
//            if (name.equals("armorRightArm") || name.equals("armorLeftArm") || name.equals("armorHead")) {
//                PlayerAnimBone bone1 = new PlayerAnimBone("torso");
//                ((IAnimatedPlayer)player).playerAnimLib$getAnimManager().get3DTransform(bone1);
//                if (Math.abs(bone1.getBend()) >= 0.0001f) {
//                    PlayerBendHelper.applyTorsoBendToMatrix(poseStack, bone1.getBendAxis(), bone1.getBend());
//                }
//            }
//        }
//    }

    @Inject(method = "renderCube", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;normal()Lorg/joml/Matrix3f;"), cancellable = true)
    private void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor, CallbackInfo ci) {
        if ((GeoRenderer)(Object)this instanceof GeoArmorRenderer<?> armorRenderer && armorRenderer.getCurrentEntity() instanceof AbstractClientPlayer player) {
            Pair<String, BendableCuboid> bendableCuboid = ((GeoCubeAccessor)(Object)cube).getBendableCuboid();
            if (bendableCuboid == null) return;
            PlayerAnimBone bone = new PlayerAnimBone(bendableCuboid.getLeft());
            ((IAnimatedPlayer)player).playerAnimLib$getAnimManager().get3DTransform(bone);
            if (Math.abs(bone.getBend()) >= 0.0001f) {
                bendableCuboid.getRight().applyBend(bone.getBendAxis(), bone.getBend());
                bendableCuboid.getRight().render(poseStack.last(), buffer, packedLight, packedOverlay, renderColor);
                ci.cancel();
            }
        }
    }

    @Unique
    private void bendableCuboids$initBend(List<GeoBone> bones, Direction direction, String parent, BakedGeoModel model) {
        for (GeoBone bone : bones) {
            if (bone == null) return;
            for (GeoCube cube : bone.getCubes()) {
                if (((GeoCubeAccessor)(Object)cube).getBendableCuboid() != null) return;
                GeckoLibBendableCuboidBuilder builder = new GeckoLibBendableCuboidBuilder();
                builder.setDirection(direction);
                ((GeoCubeAccessor)(Object)cube).setBendableCuboid(new Pair<>(parent, builder.build(cube, model, parent)));
            }
            bendableCuboids$initBend(bone.getChildBones(), direction, parent, model);
        }
    }
}
