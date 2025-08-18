package com.astradia;

import com.astradia.command.CommandManager;
import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import com.astradia.network.NetworkManager;
import com.astradia.player.EquipmentSlot;
import com.astradia.player.PlayerManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstradiaServer implements ModInitializer {
	public static final String MOD_ID = "astradia-server";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	//private static PlayerCosmeticStore playerCosmeticStore;

	private static PlayerManager playerManager;
	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			Database.connect();
			//playerCosmeticStore = new PlayerCosmeticStore();
			playerManager = new PlayerManager(server);
			playerManager.initialize();
		});
		ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
			//ServerPlayerManager.INSTANCE.onServerClose();
			Database.disconnect();
		});
		ServerCosmeticStore.INSTANCE.initialize();
		CommandManager.initialize();
		NetworkManager.initialize();


	}

	public static PlayerManager getPlayerManager() {
		return playerManager;
	}

	/*@Nullable
	public static PlayerCosmeticStore getPlayerCosmeticStore() {
		return playerCosmeticStore;
	}*/

	public static EquipmentSlot[] getEquipmentSlots() {
		return new EquipmentSlot[] {
			new EquipmentSlot(BodyPart.HEAD, SlotType.BEARD),
			new EquipmentSlot(BodyPart.HEAD, SlotType.HAIR),
			new EquipmentSlot(BodyPart.HEAD, SlotType.ACCESSORY),
			new EquipmentSlot(BodyPart.HEAD, SlotType.ACCESSORY),
			new EquipmentSlot(BodyPart.HEAD, SlotType.HORNS),
			new EquipmentSlot(BodyPart.HEAD, SlotType.HORNS),
			new EquipmentSlot(BodyPart.HEAD, SlotType.EARS),

			new EquipmentSlot(BodyPart.TORSO, SlotType.ACCESSORY),
			new EquipmentSlot(BodyPart.TORSO, SlotType.ACCESSORY),
			new EquipmentSlot(BodyPart.TORSO, SlotType.TAIL),

			new EquipmentSlot(BodyPart.LEFT_ARM, SlotType.REPLACE),
			new EquipmentSlot(BodyPart.LEFT_ARM, SlotType.CLAWS),
			new EquipmentSlot(BodyPart.LEFT_ARM, SlotType.WINGS),

			new EquipmentSlot(BodyPart.RIGHT_ARM, SlotType.REPLACE),
			new EquipmentSlot(BodyPart.RIGHT_ARM, SlotType.CLAWS),
			new EquipmentSlot(BodyPart.RIGHT_ARM, SlotType.WINGS),

			new EquipmentSlot(BodyPart.LEFT_LEG, SlotType.REPLACE),
			new EquipmentSlot(BodyPart.LEFT_LEG, SlotType.CLAWS),

			new EquipmentSlot(BodyPart.RIGHT_LEG, SlotType.REPLACE),
			new EquipmentSlot(BodyPart.RIGHT_LEG, SlotType.CLAWS),
		};
	}
}