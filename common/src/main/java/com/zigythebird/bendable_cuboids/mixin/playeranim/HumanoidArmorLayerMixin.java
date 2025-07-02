package com.zigythebird.bendable_cuboids.mixin.playeranim;

import com.zigythebird.bendable_cuboids.impl.BendUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderLayerParent;Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;)V", at = @At("TAIL"))
    private void init(RenderLayerParent renderLayerParent, HumanoidModel humanoidModel, HumanoidModel humanoidModel2, HumanoidModel humanoidModel3, HumanoidModel humanoidModel4, EquipmentLayerRenderer equipmentLayerRenderer, CallbackInfo ci) {
        BendUtil.initModel(humanoidModel);
        BendUtil.initModel(humanoidModel2);
        BendUtil.initModel(humanoidModel3);
        BendUtil.initModel(humanoidModel4);
    }
}
