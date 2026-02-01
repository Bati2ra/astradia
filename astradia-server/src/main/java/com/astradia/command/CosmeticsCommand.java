package com.astradia.command;

import com.astradia.VentoServer;
import com.astradia.api.CosmeticInfo;
import com.astradia.api.CosmeticProperty;
import com.astradia.api.CosmeticPropertyRegistry;
import com.astradia.player.PlayerData;
import com.astradia.store.ServerCosmeticStore;
import com.astradia.utils.SlotUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CosmeticsCommand {
    private static final SuggestionProvider<ServerCommandSource> COSMETIC_SUGGESTIONS = (context, builder) -> {
        ServerCosmeticStore.INSTANCE.getAll().keySet().forEach(id -> builder.suggest(id.toString()));
        return builder.buildFuture();
    };

    private static final SuggestionProvider<ServerCommandSource> SLOT_SUGGESTIONS = (context, builder) -> {
        SlotUtils.getPlayerEquipmentSlots().keySet().forEach(id -> builder.suggest(id.toString()));
        return builder.buildFuture();
    };

    private static final SuggestionProvider<ServerCommandSource> SLOT_SUGGESTIONS_FOR_COSMETIC = (context, builder) -> {
        final Identifier cosmeticId = IdentifierArgumentType.getIdentifier(context, "id");
        if(cosmeticId != null) {
            CosmeticInfo cosmeticInfo = ServerCosmeticStore.INSTANCE.get(cosmeticId);
            if(cosmeticInfo != null) {
                SlotUtils.getPlayerEquipmentSlots().keySet()
                        .stream().filter(id -> cosmeticInfo.getSlotId().equals(id))
                        .forEach(id -> builder.suggest(id.toString()));
            }
        }
        return builder.buildFuture();
    };

    public static final LiteralArgumentBuilder<ServerCommandSource> COSMETICS_COMMAND = literal("cosmetics")
            // /cosmetics list [category] - Listar cosméticos disponibles
            .then(literal("list")
                    .executes(context -> listCosmetics(context, null))
                    .then(argument("category", IdentifierArgumentType.identifier())
                            .executes(context -> listCosmetics(context, null))
                            .suggests((context, builder) -> {
                                SlotUtils.getPlayerEquipmentSlots().keySet().forEach(id -> builder.suggest(id.toString()));
                                return builder.buildFuture();
                            })
                            .executes(context -> listCosmetics(
                                    context,
                                    IdentifierArgumentType.getIdentifier(context, "category")
                            ))
                    )
            )
            // /cosmetics info <id> - Ver información detallada de un cosmético
            .then(literal("info")
                    .then(argument("id", IdentifierArgumentType.identifier())
                            .suggests(COSMETIC_SUGGESTIONS)
                            .executes(CosmeticsCommand::showCosmeticInfo)
                    )
            )
            // /cosmetics equip <player> <slot> <id> [nbt] - Equipar cosmético
            .then(literal("equip")
                    .then(argument("player", EntityArgumentType.player())

                            .then(argument("id", IdentifierArgumentType.identifier())
                                    .suggests(COSMETIC_SUGGESTIONS)
                                            .then(argument("slot", IdentifierArgumentType.identifier())
                                                    .suggests(SLOT_SUGGESTIONS_FOR_COSMETIC)
                                                    .executes(context -> equipCosmetic(context, false))
                                                            .then(argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                                                    .executes(context -> equipCosmetic(context, true))
                                                            )
                                    )
                            )
                    )
            )
            // /cosmetics unequip <player> <slot> - Desequipar cosmético
            .then(literal("unequip")
                    .then(argument("player", EntityArgumentType.player())
                            .then(argument("slot", IdentifierArgumentType.identifier())
                                    .suggests(SLOT_SUGGESTIONS)
                                    .executes(CosmeticsCommand::unequipCosmetic)
                            )
                    )
            )
            // /cosmetics clear <player> - Desequipar todos los cosméticos
            .then(literal("clear")
                    .then(argument("player", EntityArgumentType.player())
                            .executes(CosmeticsCommand::clearAllCosmetics)
                    )
            )
            // /cosmetics unlock <player> [id] - Desbloquear cosmético(s)
            .then(literal("unlock")
                    .then(argument("player", EntityArgumentType.player())
                            .executes(CosmeticsCommand::unlockAllCosmetics)
                            .then(argument("id", IdentifierArgumentType.identifier())
                                    .suggests(COSMETIC_SUGGESTIONS)
                                    .executes(CosmeticsCommand::unlockCosmetic)
                            )
                    )
            )
            // /cosmetics show <player> [type] - Ver cosméticos del jugador
            .then(literal("show")
                    .then(argument("player", EntityArgumentType.player())
                            .executes(context -> showPlayerCosmetics(context, "equipped"))
                            .then(argument("type", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        builder.suggest("equipped");
                                        builder.suggest("unlocked");
                                        builder.suggest("all");
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> showPlayerCosmetics(
                                            context,
                                            StringArgumentType.getString(context, "type")
                                    ))
                            )
                    )
            )
            // /cosmetics copy <from> <to> - Copiar cosméticos equipados
            .then(literal("copy")
                    .then(argument("from", EntityArgumentType.player())
                            .then(argument("to", EntityArgumentType.player())
                                    .executes(CosmeticsCommand::copyCosmetics)
                            )
                    )
            )
            // /cosmetics reload - Recargar cosméticos del servidor
            .then(literal("reload")
                    .requires(source -> source.hasPermissionLevel(4))
                    .executes(CosmeticsCommand::reloadCosmetics)
            );

    private static int listCosmetics(CommandContext<ServerCommandSource> context, Identifier category) {
        context.getSource().sendFeedback(() ->
                        Text.literal("§6§l=== Cosméticos Disponibles ===")
                                .styled(style -> style.withColor(Formatting.GOLD).withBold(true)),
                false
        );

        int count = 0;
        for (Map.Entry<Identifier, CosmeticInfo> entry : ServerCosmeticStore.INSTANCE.getAll().entrySet()) {
            if (category != null) {
                if(!entry.getValue().getSlotId().equals(category))
                 continue;
            }

            count++;
            Identifier id = entry.getKey();
            CosmeticInfo info = entry.getValue();

            context.getSource().sendFeedback(() ->
                            Text.literal(String.format("§e%s §7- §f%s",
                                    id.toString(),
                                    info.getName() != null ? info.getName() : "Sin nombre"
                            )),
                    false
            );
        }

        final int totalCount = count;
        context.getSource().sendFeedback(() ->
                        Text.literal(String.format("§7Total: §f%d §7cosmético(s)", totalCount)),
                false
        );

        return 1;
    }

    private static int showCosmeticInfo(CommandContext<ServerCommandSource> context) {
        final Identifier id = IdentifierArgumentType.getIdentifier(context, "id");
        var cosmetic = ServerCosmeticStore.INSTANCE.get(id);

        if (cosmetic == null) {
            context.getSource().sendFeedback(() ->
                            Text.literal("§c✗ El cosmético '" + id + "' no existe"),
                    false
            );
            return 0;
        }

        context.getSource().sendFeedback(() ->
                        Text.literal("§6§l=== Información del Cosmético ===")
                                .styled(style -> style.withColor(Formatting.GOLD).withBold(true)),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal(String.format("§eID: §f%s", id.toString())),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal(String.format("§eNombre: §f%s",
                                cosmetic.getName() != null ? cosmetic.getName() : "Sin nombre"
                        )),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal(String.format("§eSlot: §f%s",
                                cosmetic.getSlotId().toString()
                        )),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("§ePropiedades: §f"),
                false
        );

        for (Map.Entry<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> entry : cosmetic.getProperties().entrySet()) {
            Optional<String> propertyName = CosmeticPropertyRegistry.getTypeByClass(entry.getKey());
            propertyName.ifPresent(name -> {
                var property = entry.getValue();
                CosmeticProperty.PlayerData data = property.createPlayerData();
                if(data == null) return;
                NbtCompound nbt = data.toNbt();
                if(nbt.isEmpty()) return;

                MutableText hoverText = Text.literal("");
                boolean first = true;
                for (String key : nbt.getKeys()) {
                    if (!first) {
                        hoverText.append("\n");
                    }
                    first = false;

                    NbtElement value = nbt.get(key);
                    String tipo = value == null ? "?" : value.getNbtType().getCrashReportName();

                    hoverText.append(Text.literal("§6" + key + "§7: §8[§e" + tipo + "§8]"));
                }

                // Texto principal con hover
                MutableText mainText = Text.literal("§7• §e" + name + "§7: §8{§7...§8}")
                        .styled(style -> style
                                .withHoverEvent(new HoverEvent.ShowText(hoverText))
                        );

                context.getSource().sendFeedback(() -> mainText, false);
            });
        }

        // Añade más información según tu CosmeticInfo
        // Por ejemplo: parte del cuerpo, tipo de slot, rareza, etc.

        return 1;
    }

    private static String getSimpleValue(NbtElement element) {
        return switch (element) {
            case NbtString nbtString -> "\"" + element.asString() + "\"";
            case NbtCompound compound -> "{...}";
            case NbtList nbtElements -> "[" + nbtElements.size() + " items]";
            case null, default -> element.toString();
        };
    }

    private static int equipCosmetic(CommandContext<ServerCommandSource> context, boolean hasNbt) throws CommandSyntaxException {
        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final Identifier slot = IdentifierArgumentType.getIdentifier(context, "slot");
        final Identifier cosmeticId = IdentifierArgumentType.getIdentifier(context, "id");
        final NbtCompound nbt = hasNbt ? NbtCompoundArgumentType.getNbtCompound(context, "nbt") : null;

        var cosmetic = ServerCosmeticStore.INSTANCE.get(cosmeticId);
        if (cosmetic == null) {
            context.getSource().sendFeedback(() ->
                            Text.literal("§c✗ El cosmético '" + cosmeticId + "' no existe"),
                    false
            );
            return 0;
        }

        PlayerData playerData = VentoServer.getPlayerManager().getFromPlayer(player);

        try {
            var result = playerData.getCosmetics().equipCosmetic(slot, cosmetic, nbt);

            if (playerData.getCosmetics().isDirty()) {
                VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(player);
            }

            boolean success = result.getMessage().toLowerCase().contains("éxito") ||
                    result.getMessage().toLowerCase().contains("equipado");

            context.getSource().sendFeedback(() ->
                            Text.literal((success ? "§a✓ " : "§c✗ ") + result.getMessage()),
                    false
            );

            return success ? 1 : 0;
        } catch (Exception e) {
            if (e.getCause() instanceof ClassCastException) {
                context.getSource().sendFeedback(() ->
                                Text.literal("§c✗ Error al equipar el cosmético: El formato NBT no es correcto."),
                        false
                );
                return 0;
            }
            context.getSource().sendFeedback(() ->
                            Text.literal("§c✗ Error al equipar el cosmético: Algo salió mal."),
                    false
            );
            return 0;
        }
    }

    private static int unequipCosmetic(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final Identifier slot = IdentifierArgumentType.getIdentifier(context, "slot");

        PlayerData playerData = VentoServer.getPlayerManager().getFromPlayer(player);
        var result = playerData.getCosmetics().unequipCosmetic(slot);

        VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(player);

        context.getSource().sendFeedback(() ->
                        Text.literal(String.format("§a✓ Slot §e%s §ade §e%s §adesequipado",
                                slot.toString(),
                                player.getName().getString()
                        )),
                false
        );

        return 1;
    }

    private static int clearAllCosmetics(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        PlayerData playerData = VentoServer.getPlayerManager().getFromPlayer(player);

        playerData.getCosmetics().clearSlots();

        VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(player);

        context.getSource().sendFeedback(() ->
                        Text.literal(String.format("§a✓ Todos los cosméticos de §e%s §ahan sido removidos",
                                player.getName().getString()
                        )),
                false
        );

        return 1;
    }

    private static int unlockAllCosmetics(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        var result = VentoServer.getPlayerManager().getFromPlayer(player).getCosmetics().unlockAll();

        context.getSource().sendFeedback(() ->
                        Text.literal("§a✓ " + result.getMessage()),
                false
        );

        return 1;
    }

    private static int unlockCosmetic(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        final Identifier cosmeticId = IdentifierArgumentType.getIdentifier(context, "id");

        var cosmetic = ServerCosmeticStore.INSTANCE.get(cosmeticId);
        if (cosmetic == null) {
            context.getSource().sendFeedback(() ->
                            Text.literal("§c✗ El cosmético '" + cosmeticId + "' no existe"),
                    false
            );
            return 0;
        }

        try {
            var result = VentoServer.getPlayerManager().getFromPlayer(player).getCosmetics().unlock(cosmeticId);

            boolean success = result.getMessage().toLowerCase().contains("desbloqueado") ||
                    result.getMessage().toLowerCase().contains("éxito");

            context.getSource().sendFeedback(() ->
                            Text.literal((success ? "§a✓ " : "§e⚠ ") + result.getMessage()),
                    false
            );

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            context.getSource().sendFeedback(() ->
                            Text.literal("§c✗ Error al desbloquear: " + e.getMessage()),
                    false
            );
            return 0;
        }
    }

    private static int showPlayerCosmetics(CommandContext<ServerCommandSource> context, String type) throws CommandSyntaxException {
        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        var cosmetics = VentoServer.getPlayerManager().getFromPlayer(player).getCosmetics();

        context.getSource().sendFeedback(() ->
                        Text.literal(String.format("§6§l=== Cosméticos de %s ===", player.getName().getString()))
                                .styled(style -> style.withColor(Formatting.GOLD).withBold(true)),
                false
        );

        switch (type.toLowerCase()) {
            case "equipped":
                context.getSource().sendFeedback(() ->
                                Text.literal(cosmetics.showEquipment()),
                        false
                );
                break;
            case "unlocked":
                context.getSource().sendFeedback(() ->
                                Text.literal(cosmetics.showUnlockedCosmetics()),
                        false
                );
                break;
            case "all":
                context.getSource().sendFeedback(() ->
                                Text.literal("§e§lEquipados:"),
                        false
                );
                context.getSource().sendFeedback(() ->
                                Text.literal(cosmetics.showEquipment()),
                        false
                );
                context.getSource().sendFeedback(() ->
                                Text.literal("§e§lDesbloqueados:"),
                        false
                );
                context.getSource().sendFeedback(() ->
                                Text.literal(cosmetics.showUnlockedCosmetics()),
                        false
                );
                break;
            default:
                context.getSource().sendFeedback(() ->
                                Text.literal("§c✗ Tipo inválido. Usa: equipped, unlocked, o all"),
                        false
                );
                return 0;
        }

        return 1;
    }

    private static int copyCosmetics(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerPlayerEntity fromPlayer = EntityArgumentType.getPlayer(context, "from");
        final ServerPlayerEntity toPlayer = EntityArgumentType.getPlayer(context, "to");

        if (fromPlayer.equals(toPlayer)) {
            context.getSource().sendFeedback(() ->
                            Text.literal("§c✗ No puedes copiar cosméticos a ti mismo"),
                    false
            );
            return 0;
        }

        PlayerData fromData = VentoServer.getPlayerManager().getFromPlayer(fromPlayer);
        PlayerData toData = VentoServer.getPlayerManager().getFromPlayer(toPlayer);

        context.getSource().sendFeedback(() -> Text.literal("WIP"), false);
        //toData.getCosmetics().copyEquipmentFrom(fromData.getCosmetics());

        VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(toPlayer);

        context.getSource().sendFeedback(() ->
                        Text.literal(String.format("§a✓ Cosméticos copiados de §e%s §aa §e%s",
                                fromPlayer.getName().getString(),
                                toPlayer.getName().getString()
                        )),
                false
        );

        return 1;
    }

    private static int reloadCosmetics(CommandContext<ServerCommandSource> context) {
        try {
            //ServerCosmeticStore.INSTANCE.reload();
            context.getSource().sendFeedback(() ->
                            Text.literal("No soportado en esta versión."),
                    false
            );

            context.getSource().sendFeedback(() ->
                            Text.literal("§a✓ Cosméticos recargados exitosamente"),
                    false
            );

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            context.getSource().sendFeedback(() ->
                            Text.literal("§c✗ Error al recargar cosméticos: " + e.getMessage()),
                    false
            );
            return 0;
        }
    }
}
