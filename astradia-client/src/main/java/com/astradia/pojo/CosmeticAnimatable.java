package com.astradia.pojo;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CosmeticAnimatable implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ClientCosmetic cosmetic;
    protected static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");

    public CosmeticAnimatable(ClientCosmetic cosmetic) {
        this.cosmetic = cosmetic;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "base", 0, this::idleAnimController));
    }

    protected <E extends GeoAnimatable> PlayState idleAnimController(final AnimationState<E> event) {
            if(cosmetic.getAnimationPath() == null) return PlayState.STOP;

            return event.setAndContinue(IDLE_ANIMATION);

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
