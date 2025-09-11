package com.astradia.render.player;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;

import java.util.Map;

public class PlayerRendererManager {
    public static final PlayerRendererManager INSTANCE = new PlayerRendererManager();
    private static final Map<SkinTextures.Model, PlayerRendererProvider> PLAYER_RENDERER_FACTORIES = Map.of(
        SkinTextures.Model.WIDE, (context, originalRenderers) -> new PlayerRenderer(context, new PlayerModel(), false, (PlayerEntityRenderer) originalRenderers.get(SkinTextures.Model.WIDE)),
        SkinTextures.Model.SLIM, (context, originalRenderers) -> new PlayerRenderer(context, new PlayerModel(), true, (PlayerEntityRenderer) originalRenderers.get(SkinTextures.Model.SLIM))
    );
    private Map<SkinTextures.Model, PlayerRenderer<? extends PlayerEntity, ?>> modelRenderers = Map.of();

    public PlayerRenderer getRenderer(AbstractClientPlayerEntity player) {
        return modelRenderers.get(player.getSkinTextures().model());
    }

    public PlayerRenderer getRenderer(PlayerEntityRenderState player) {
        return modelRenderers.get(player.skinTextures.model());
    }

    public void reload(ResourceManager manager, EntityRendererFactory.Context context, Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> originalRenderers) {
        modelRenderers = reloadPlayerRenderers(context, originalRenderers);
    }

    public static Map<SkinTextures.Model, PlayerRenderer<? extends PlayerEntity, ?>> reloadPlayerRenderers(EntityRendererFactory.Context ctx, Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> originalRenderers) {
        ImmutableMap.Builder<SkinTextures.Model, PlayerRenderer<? extends PlayerEntity, ?>> builder = ImmutableMap.builder();
        PLAYER_RENDERER_FACTORIES.forEach((model, factory) -> {
            try {
                builder.put(model, factory.create(ctx, originalRenderers));
            } catch (Exception var5) {
                throw new IllegalArgumentException("Failed to create player model for " + model, var5);
            }
        });
        return builder.build();
    }

    public interface PlayerRendererProvider {
        PlayerRenderer<?, ?> create(EntityRendererFactory.Context ctx, Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> originalRenderers);
    }
}
