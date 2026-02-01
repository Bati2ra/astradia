package com.astradia.pojo;

import com.astradia.impl.AnimatableProperty;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

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
        var property = cosmetic.getProperty(AnimatableProperty.class);
        if(property.isPresent()) {
            return animTest.setAndContinue(IDLE_ANIMATION);
        }

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        World level = MinecraftClient.getInstance().world;
        if (level == null) {
            return 0;
        }
        double gameTicks = level.getTime();
        double partialTick = MinecraftClient.getInstance().getRenderTime();
        return RenderUtil.getCurrentTick();
    }

    public ClientCosmeticInfo getCosmetic() {
        return cosmetic;
    }
}
