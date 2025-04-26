package com.zigythebird.bendable_cuboids.mixin.playeranim;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PlayerModel.class)
public interface PlayerModelAccessor_playerAnim {
    @Accessor
    List<ModelPart> getBodyParts();
}
