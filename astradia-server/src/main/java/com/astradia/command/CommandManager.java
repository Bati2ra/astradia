package com.astradia.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandManager {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("vento")
                .requires((source) -> source.hasPermissionLevel(2))
                .then(CosmeticsCommand.COSMETICS_COMMAND)
                .then(PlayerProportionsCommand.PROPORTIONS_COMMAND)
        ));

    }
}
