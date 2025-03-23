package com.astradia;

import com.astradia.network.ClientNetworkManager;
import com.astradia.render.layer.CosmeticLayer;
import com.astradia.screen.DebugOverlay;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstradiaClient implements ModInitializer {
	public static final String MOD_ID = "astradia-client";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world from client!");
		DebugOverlay.initialize();

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if(entityRenderer instanceof PlayerEntityRenderer renderer) {
				registrationHelper.register(new CosmeticLayer(renderer));
			}
		});
		ClientPlayerCosmeticManager.INSTANCE.initialize();
		ClientNetworkManager.initialize();
	}
}