package com.astradia.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.permissions.Permissions;

import static net.minecraft.commands.Commands.literal;

public class CommandManager {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("vento")
                .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                .then(CosmeticsCommand.COSMETICS_COMMAND)
                .then(PlayerProportionsCommand.PROPORTIONS_COMMAND)
        ));

    }
}
