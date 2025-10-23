package com.zigythebird.bendable_cuboids.api;

import com.zigythebird.playeranim.animation.AvatarAnimManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface IMutableModel {
    @Nullable
    AvatarAnimManager bc$getAnimation();
}
