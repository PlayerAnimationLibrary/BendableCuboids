package com.zigythebird.bendable_cuboids.api;

import com.google.j2objc.annotations.J2ObjCIncompatible;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
@J2ObjCIncompatible
public interface IMutableModel {
    @Nullable
    AvatarAnimManager bc$getAnimation();
}
