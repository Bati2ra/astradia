package com.astradia;

import com.astradia.network.ClientNetworkManager;
import com.astradia.player.PlayerManager;
import com.astradia.render.layer.CosmeticLayer;

import com.astradia.screen.WardrobeScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstradiaClient implements ModInitializer {
	public static final String MOD_ID = "astradia-client";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static PlayerManager playerManager;
	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world from client!");
		//DebugOverlay.initialize();

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if(entityRenderer instanceof PlayerEntityRenderer renderer) {
				registrationHelper.register(new CosmeticLayer(renderer));
			}
		});
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if(client.player == null) return;

			if(client.player.isSprinting()) {
				MinecraftClient.getInstance().setScreen(new WardrobeScreen(null));
			}
		});
		//ClientPlayerManager.INSTANCE.initialize();
		ClientCosmeticStore.INSTANCE.initialize();
		playerManager = new PlayerManager();
		playerManager.initialize();
		ClientNetworkManager.initialize();

	}

	public static PlayerManager getPlayerManager() {
		return playerManager;
	}

	public static boolean isEverythingReady() {
		return ClientCosmeticStore.INSTANCE.isReady();
	}
}