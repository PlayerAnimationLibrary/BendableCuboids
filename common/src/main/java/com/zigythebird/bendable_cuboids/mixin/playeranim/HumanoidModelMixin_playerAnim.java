package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.api.IMutableModel;
import com.zigythebird.bendable_cuboids.impl.compatibility.PlayerBendHelper;
import com.zigythebird.playeranim.animation.PlayerAnimManager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin_playerAnim implements IMutableModel {
    @Unique
    private PlayerAnimManager bc$animation = null;

    @Shadow
    @Final
    public ModelPart rightArm;
    @Shadow
    @Final
    public ModelPart leftArm;
    @Shadow
    @Final
    public ModelPart body;
    @Shadow
    @Final
    public ModelPart rightLeg;
    @Shadow
    @Final
    public ModelPart leftLeg;

    @Inject(method = "<init>(Lnet/minecraft/client/model/geom/ModelPart;Ljava/util/function/Function;)V", at = @At("TAIL"))
    private void bc$initBends(ModelPart root, Function<ResourceLocation, RenderType> renderType, CallbackInfo ci) {
        PlayerBendHelper.initBend(this.body, Direction.DOWN);
        PlayerBendHelper.initBend(this.rightArm, Direction.UP);
        PlayerBendHelper.initBend(this.leftArm, Direction.UP);
        PlayerBendHelper.initBend(this.rightLeg, Direction.UP);
        PlayerBendHelper.initBend(this.leftLeg, Direction.UP);
    }

    @Override
    public void bc$setAnimation(@Nullable PlayerAnimManager emoteSupplier) {
        this.bc$animation = emoteSupplier;
    }

    @Override
    public @Nullable PlayerAnimManager bc$getAnimation() {
        return this.bc$animation;
    }
}
