package com.astradia;

import com.astradia.network.ClientNetworkManager;
import com.astradia.player.PlayerManager;

import com.astradia.pojo.CosmeticAnimatable;
import com.astradia.screen.DebugOverlay;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.loading.math.MolangQueries;

public class VentoClient implements ModInitializer {
	public static final String MOD_ID = "astradia-client";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static PlayerManager playerManager;
	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world from client!");
		DebugOverlay.initialize();
		//ClientPlayerManager.INSTANCE.initialize();
		ClientCosmeticStore.INSTANCE.initialize();
		playerManager = new PlayerManager();
		playerManager.initialize();
		ClientNetworkManager.initialize();

		MolangQueries.<CosmeticAnimatable>setActorVariable("query.physics_pitch", actor -> 0);
		MolangQueries.<CosmeticAnimatable>setActorVariable("query.physics_yaw", actor -> 0);

	}

	public static PlayerManager getPlayerManager() {
		return playerManager;
	}

	public static boolean isEverythingReady() {
		return ClientCosmeticStore.INSTANCE.isReady();
	}
}