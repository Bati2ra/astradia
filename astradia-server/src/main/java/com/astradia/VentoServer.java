package com.astradia;

import com.astradia.command.CommandManager;
import com.astradia.network.NetworkManager;
import com.astradia.player.PlayerManager;
import com.astradia.store.ServerCosmeticStore;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class VentoServer implements ModInitializer {
	public static final String MOD_ID = "astradia-server";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static PlayerManager playerManager;
	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			Database.connect();
			BodyProportionsConfigLoader.onReload();
			playerManager = new PlayerManager(server);
			playerManager.initialize();
		});
		ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
			Database.disconnect();
		});
		ServerCosmeticStore.INSTANCE.initialize();
		CommandManager.initialize();
		NetworkManager.initialize();
	}

	public static PlayerManager getPlayerManager() {
		return playerManager;
	}

	public static File getDataFolder() {
		File dataFolder = new File(FabricLoader.getInstance().getGameDir().toFile(), "vento");
		if (!dataFolder.exists() && dataFolder.mkdirs()) {
			VentoServer.LOGGER.info("[Vento] Created data folder at '{}'", dataFolder.getAbsolutePath());
		}
		return dataFolder;
	}
}