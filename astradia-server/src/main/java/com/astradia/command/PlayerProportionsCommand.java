package com.astradia.command;

import com.astradia.BodyProportionsConfigLoader;
import com.astradia.VentoServer;
import com.astradia.api.player.ScaleParameter;
import com.astradia.player.PlayerBodyProportions;
import com.astradia.player.PlayerData;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PlayerProportionsCommand {
    public static final LiteralArgumentBuilder<ServerCommandSource> PROPORTIONS_COMMAND = literal("proportions")
            // /proportions list - Muestra todos los parámetros disponibles
            .then(literal("list")
                    .executes(PlayerProportionsCommand::listParameters)
            )
            // /proportions get <player> [parameter] - Ver proporciones de un jugador
            .then(literal("get")
                    .then(argument("player", EntityArgumentType.player())
                            .executes(context -> getPlayerProportions(context, null))
                            .then(argument("parameter", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        BodyProportionsConfigLoader.CONFIG.getAll().forEach(param ->
                                                builder.suggest(param.getId())
                                        );
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> getPlayerProportions(
                                            context,
                                            StringArgumentType.getString(context, "parameter")
                                    ))
                            )
                    )
            )
            // /proportions set <player> <parameter> <value> - Modificar una proporción
            .then(literal("set")
                    .then(argument("player", EntityArgumentType.player())
                            .then(argument("parameter", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        BodyProportionsConfigLoader.CONFIG.getAll().forEach(param ->
                                                builder.suggest(param.getId())
                                        );
                                        return builder.buildFuture();
                                    })
                                    .then(argument("value", FloatArgumentType.floatArg())
                                            .executes(context -> setPlayerProportion(context, false))
                                            // Rama con linked (solo se sugiere si es arm/leg)
                                            .then(argument("linked", BoolArgumentType.bool())
                                                    .suggests((context, builder) -> {
                                                        // Solo sugerir si el parámetro es arm o leg
                                                        try {
                                                            String param = StringArgumentType.getString(context, "parameter");
                                                            if (isArmOrLeg(param)) {
                                                                builder.suggest("true", Text.literal("Vincular ambos lados"));
                                                                builder.suggest("false", Text.literal("Solo este lado"));
                                                            }
                                                        } catch (IllegalArgumentException ignored) {}
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(context -> {
                                                        boolean linked = BoolArgumentType.getBool(context, "linked");
                                                        return setPlayerProportion(context, linked);
                                                    })
                                            )
                                    )
                            )
                    )
            )
            // /proportions reset <player> [parameter] - Resetear proporciones
            .then(literal("reset")
                    .then(argument("player", EntityArgumentType.player())
                            .executes(context -> resetPlayerProportions(context, null))
                            .then(argument("parameter", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        BodyProportionsConfigLoader.CONFIG.getAll().forEach(param ->
                                                builder.suggest(param.getId())
                                        );
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> resetPlayerProportions(
                                            context,
                                            StringArgumentType.getString(context, "parameter")
                                    ))
                            )
                    )
            )
            // /proportions copy <from> <to> - Copiar proporciones de un jugador a otro
            .then(literal("copy")
                    .then(argument("from", EntityArgumentType.player())
                            .then(argument("to", EntityArgumentType.player())
                                    .executes(context -> copyPlayerProportions(context))
                            )
                    )
            );

    private static int listParameters(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() ->
                Text.literal("§6§l=== Parámetros de Proporciones Disponibles ===").styled(style ->
                        style.withColor(Formatting.GOLD).withBold(true)
                ), false
        );

        BodyProportionsConfigLoader.CONFIG.getAll().forEach(param -> {
            context.getSource().sendFeedback(() ->
                    Text.literal(String.format("§e%s §7(Min: §f%.2f§7, Max: §f%.2f§7, Default: §f%.2f§7)",
                            param.getId(),
                            param.getMin(),
                            param.getMax(),
                            param.getValue()
                    )), false
            );
        });

        return 1;
    }

    private static int getPlayerProportions(CommandContext<ServerCommandSource> context, String parameterId) throws CommandSyntaxException {
        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        PlayerData playerData = VentoServer.getPlayerManager().getFromPlayer(player);
        PlayerBodyProportions proportions = playerData.getProportions(); // Asumiendo que existe este método

        if (parameterId == null) {
            // Mostrar todas las proporciones
            context.getSource().sendFeedback(() ->
                            Text.literal(String.format("§6§l=== Proporciones de %s ===", player.getName().getString()))
                                    .styled(style -> style.withColor(Formatting.GOLD).withBold(true)),
                    false
            );

            for (var entry : proportions.getValues().getAll().entrySet()) {
                String id = entry.getKey();
                float value = entry.getValue();
                ScaleParameter config = BodyProportionsConfigLoader.CONFIG.getParameterById(id);

                context.getSource().sendFeedback(() ->
                        Text.literal(String.format("§e%s: §f%.2f §7(%.2f - %.2f)",
                                id, value, config.getMin(), config.getMax()
                        )), false
                );
            }
        } else {
            // Mostrar una proporción específica
            float value = proportions.getValues().getParameterById(parameterId);
            ScaleParameter config = BodyProportionsConfigLoader.CONFIG.getParameterById(parameterId);

            if (config.getId().equals("default")) {
                context.getSource().sendFeedback(() ->
                                Text.literal("§c✗ El parámetro '" + parameterId + "' no existe en la configuración"),
                        false
                );
                return 0;
            }

            context.getSource().sendFeedback(() ->
                    Text.literal(String.format("§6%s §7de §e%s§7: §f%.2f §7(Min: §f%.2f§7, Max: §f%.2f§7)",
                            parameterId,
                            player.getName().getString(),
                            value,
                            config.getMin(),
                            config.getMax()
                    )), false
            );
        }

        return 1;
    }

    private static boolean isArmOrLeg(String parameter) {
        return parameter.toLowerCase().contains("arm") || parameter.toLowerCase().contains("leg");
    }

    private static int setPlayerProportion(CommandContext<ServerCommandSource> context, boolean linked) throws CommandSyntaxException {
        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final String parameterId = StringArgumentType.getString(context, "parameter");
        final float value = FloatArgumentType.getFloat(context, "value");

        ScaleParameter config = BodyProportionsConfigLoader.CONFIG.getParameterById(parameterId);

        if (config.getId().equals("default")) {
            context.getSource().sendFeedback(() ->
                            Text.literal("§c✗ El parámetro '" + parameterId + "' no existe en la configuración"),
                    false
            );
            return 0;
        }

        // Clampear el valor
        float clampedValue = config.clamp(value);

        PlayerData playerData = VentoServer.getPlayerManager().getFromPlayer(player);
        PlayerBodyProportions proportions = playerData.getProportions();

        proportions.getValues().getAll().put(parameterId, clampedValue);

        if (linked && isArmOrLeg(parameterId)) {
            String position = parameterId.contains("right") ? parameterId.replace("right", "left") : parameterId.replace("left", "right");
            proportions.getValues().getAll().put(position, clampedValue);
        }

        // Enviar actualización a los clientes
        VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(player);

        if (clampedValue != value) {
            context.getSource().sendFeedback(() ->
                            Text.literal(String.format("§e⚠ Valor ajustado de §f%.2f §ea §f%.2f §e(fuera de rango)", value, clampedValue)),
                    false
            );
        }

        context.getSource().sendFeedback(() ->
                Text.literal(String.format("§a✓ Proporción §e%s §ade §e%s §aestablecida a §f%.2f",
                        parameterId,
                        player.getName().getString(),
                        clampedValue
                )), false
        );

        return 1;
    }

    private static int resetPlayerProportions(CommandContext<ServerCommandSource> context, String parameterId) throws CommandSyntaxException {
        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        PlayerData playerData = VentoServer.getPlayerManager().getFromPlayer(player);
        PlayerBodyProportions proportions = playerData.getProportions();

        if (parameterId == null) {
            // Resetear todas las proporciones
            proportions.getValues().getAll().clear();

            // Establecer valores por defecto
            BodyProportionsConfigLoader.CONFIG.getAll().forEach(param -> {
                proportions.getValues().getAll().put(param.getId(), param.getValue());
            });

            VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(player);

            context.getSource().sendFeedback(() ->
                    Text.literal(String.format("§a✓ Todas las proporciones de §e%s §ahan sido reseteadas a sus valores por defecto",
                            player.getName().getString()
                    )), false
            );
        } else {
            // Resetear una proporción específica
            ScaleParameter config = BodyProportionsConfigLoader.CONFIG.getParameterById(parameterId);

            if (config.getId().equals("default")) {
                context.getSource().sendFeedback(() ->
                                Text.literal("§c✗ El parámetro '" + parameterId + "' no existe en la configuración"),
                        false
                );
                return 0;
            }

            proportions.getValues().getAll().put(parameterId, config.getValue());
            VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(player);

            context.getSource().sendFeedback(() ->
                    Text.literal(String.format("§a✓ Proporción §e%s §ade §e%s §areseteada a §f%.2f",
                            parameterId,
                            player.getName().getString(),
                            config.getValue()
                    )), false
            );
        }

        return 1;
    }

    private static int copyPlayerProportions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerPlayerEntity fromPlayer = EntityArgumentType.getPlayer(context, "from");
        final ServerPlayerEntity toPlayer = EntityArgumentType.getPlayer(context, "to");

        if (fromPlayer.equals(toPlayer)) {
            context.getSource().sendFeedback(() ->
                            Text.literal("§c✗ No puedes copiar las proporciones de un jugador a sí mismo"),
                    false
            );
            return 0;
        }

        PlayerData fromData = VentoServer.getPlayerManager().getFromPlayer(fromPlayer);
        PlayerData toData = VentoServer.getPlayerManager().getFromPlayer(toPlayer);

        PlayerBodyProportions fromProportions = fromData.getProportions();
        PlayerBodyProportions toProportions = toData.getProportions();

        // Copiar todos los valores
        for (var entry : fromProportions.getValues().getAll().entrySet()) {
            toProportions.getValues().getAll().put(entry.getKey(), entry.getValue());
        }

        VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(toPlayer);

        context.getSource().sendFeedback(() ->
                Text.literal(String.format("§a✓ Proporciones copiadas de §e%s §aa §e%s",
                        fromPlayer.getName().getString(),
                        toPlayer.getName().getString()
                )), false
        );

        return 1;
    }
}
