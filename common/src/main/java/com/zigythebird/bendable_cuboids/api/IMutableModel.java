package com.zigythebird.bendable_cuboids.api;

import com.zigythebird.playeranim.animation.PlayerAnimManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface IMutableModel {
    void bc$setAnimation(@Nullable PlayerAnimManager emoteSupplier);
    @Nullable
    PlayerAnimManager bc$getAnimation();
}
