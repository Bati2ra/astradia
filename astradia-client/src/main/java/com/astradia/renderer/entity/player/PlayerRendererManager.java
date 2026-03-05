package com.astradia.renderer.entity.player;

import com.astradia.model.entity.VentoPlayerModel;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;

import java.util.Map;

public class PlayerRendererManager {
    public static final PlayerRendererManager INSTANCE = new PlayerRendererManager();
    private static final Map<PlayerModelType, PlayerRendererProvider> PLAYER_RENDERER_FACTORIES = Map.of(
            PlayerModelType.WIDE, (context, originalRenderers) -> new PlayerRenderer(context, new VentoPlayerModel(), false, (AvatarRenderer) originalRenderers.get(PlayerModelType.WIDE)),
            PlayerModelType.SLIM, (context, originalRenderers) -> new PlayerRenderer(context, new VentoPlayerModel(), true, (AvatarRenderer) originalRenderers.get(PlayerModelType.SLIM))
    );
    private Map<PlayerModelType, PlayerRenderer<? extends Player, ?>> modelRenderers = Map.of();

    public PlayerRenderer getRenderer(AbstractClientPlayer player) {
        return modelRenderers.get(player.getSkin().model());
    }

    public PlayerRenderer getRenderer(AvatarRenderState player) {
        return modelRenderers.get(player.skin.model());
    }

    public void reload(ResourceManager manager, EntityRendererProvider.Context context, Map<PlayerModelType, AvatarRenderer<AbstractClientPlayer>> originalRenderers) {
        modelRenderers = reloadPlayerRenderers(context, originalRenderers);
    }

    public static Map<PlayerModelType, PlayerRenderer<? extends Player, ?>> reloadPlayerRenderers(EntityRendererProvider.Context ctx, Map<PlayerModelType, AvatarRenderer<AbstractClientPlayer>> originalRenderers) {
        ImmutableMap.Builder<PlayerModelType, PlayerRenderer<? extends Player, ?>> builder = ImmutableMap.builder();
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
        PlayerRenderer<?, ?> create(EntityRendererProvider.Context ctx, Map<PlayerModelType, AvatarRenderer<AbstractClientPlayer>> originalRenderers);
    }
}
