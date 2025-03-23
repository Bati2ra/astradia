package com.astradia;

import com.astradia.command.CommandManager;
import com.astradia.network.NetworkManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstradiaServer implements ModInitializer {
	public static final String MOD_ID = "astradia-server";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ServerCosmeticStore.INSTANCE.initialize();
		ServerPlayerCosmeticManager.INSTANCE.initialize();
		CommandManager.initialize();
		NetworkManager.initialize();

	}
}