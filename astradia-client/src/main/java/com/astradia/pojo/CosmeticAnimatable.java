package com.astradia.pojo;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CosmeticAnimatable implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ClientCosmeticInfo cosmetic;
    protected static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");

    public CosmeticAnimatable(ClientCosmeticInfo cosmetic) {
        this.cosmetic = cosmetic;
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("base", 0, this::flyAnimController));
    }

    protected <E extends GeoAnimatable> PlayState flyAnimController(final AnimationTest<E> animTest) {
        if(true) return PlayState.STOP;

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }
}
