package com.astradia.command;

import com.astradia.VentoServer;
import com.astradia.api.CosmeticCategoryRegistry;
import com.astradia.api.CosmeticDefinition;
import com.astradia.api.CosmeticProperty;
import com.astradia.api.CosmeticPropertyRegistry;
import com.astradia.enums.ResponseType;
import com.astradia.player.PlayerData;
import com.astradia.store.ServerCosmeticStore;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import java.util.Map;
import java.util.Optional;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class CosmeticsCommand {
    private static final SuggestionProvider<CommandSourceStack> COSMETIC_SUGGESTIONS = (context, builder) -> {
        ServerCosmeticStore.INSTANCE.getAll().keySet().forEach(id -> builder.suggest(id.toString()));
        return builder.buildFuture();
    };

    private static final SuggestionProvider<CommandSourceStack> SLOT_SUGGESTIONS = (context, builder) -> {
        CosmeticCategoryRegistry.buildSlotsFromCategories().keySet().forEach(id -> builder.suggest(id.toString()));
        return builder.buildFuture();
    };

    private static final SuggestionProvider<CommandSourceStack> SLOT_SUGGESTIONS_FOR_COSMETIC = (context, builder) -> {
        final Identifier cosmeticId = IdentifierArgument.getId(context, "id");
        if(cosmeticId != null) {
            CosmeticDefinition cosmeticInfo = ServerCosmeticStore.INSTANCE.get(cosmeticId);
            if(cosmeticInfo != null) {
                CosmeticCategoryRegistry.buildSlotsFromCategories().keySet()
                        .stream().filter(id -> cosmeticInfo.getCategoryId().equals(id))
                        .forEach(id -> builder.suggest(id.toString()));
            }
        }
        return builder.buildFuture();
    };

    public static final LiteralArgumentBuilder<CommandSourceStack> COSMETICS_COMMAND = literal("cosmetics")
            // /cosmetics list [category] - Listar cosméticos disponibles
            .then(literal("list")
                    .executes(context -> listCosmetics(context, null))
                    .then(argument("category", IdentifierArgument.id())
                            .executes(context -> listCosmetics(context, null))
                            .suggests((context, builder) -> {
                                CosmeticCategoryRegistry.buildSlotsFromCategories().keySet().forEach(id -> builder.suggest(id.toString()));
                                return builder.buildFuture();
                            })
                            .executes(context -> listCosmetics(
                                    context,
                                    IdentifierArgument.getId(context, "category")
                            ))
                    )
            )
            // /cosmetics info <id> - Ver información detallada de un cosmético
            .then(literal("info")
                    .then(argument("id", IdentifierArgument.id())
                            .suggests(COSMETIC_SUGGESTIONS)
                            .executes(CosmeticsCommand::showCosmeticInfo)
                    )
            )
            // /cosmetics equip <player> <slot> <id> [nbt] - Equipar cosmético
            .then(literal("equip")
                    .then(argument("player", EntityArgument.player())

                            .then(argument("id", IdentifierArgument.id())
                                    .suggests(COSMETIC_SUGGESTIONS)
                                            .then(argument("slot", IdentifierArgument.id())
                                                    .suggests(SLOT_SUGGESTIONS_FOR_COSMETIC)
                                                    .executes(context -> equipCosmetic(context, false))
                                                            .then(argument("nbt", CompoundTagArgument.compoundTag())
                                                                    .executes(context -> equipCosmetic(context, true))
                                                            )
                                    )
                            )
                    )
            )
            // /cosmetics unequip <player> <slot> - Desequipar cosmético
            .then(literal("unequip")
                    .then(argument("player", EntityArgument.player())
                            .then(argument("slot", IdentifierArgument.id())
                                    .suggests(SLOT_SUGGESTIONS)
                                    .executes(CosmeticsCommand::unequipCosmetic)
                            )
                    )
            )
            // /cosmetics clear <player> - Desequipar todos los cosméticos
            .then(literal("clear")
                    .then(argument("player", EntityArgument.player())
                            .executes(CosmeticsCommand::clearAllCosmetics)
                    )
            )
            // /cosmetics unlock <player> [id] - Desbloquear cosmético(s)
            .then(literal("unlock")
                    .then(argument("player", EntityArgument.player())
                            .executes(CosmeticsCommand::unlockAllCosmetics)
                            .then(argument("id", IdentifierArgument.id())
                                    .suggests(COSMETIC_SUGGESTIONS)
                                    .executes(CosmeticsCommand::unlockCosmetic)
                            )
                    )
            )
            // /cosmetics show <player> [type] - Ver cosméticos del jugador
            .then(literal("show")
                    .then(argument("player", EntityArgument.player())
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
                    .then(argument("from", EntityArgument.player())
                            .then(argument("to", EntityArgument.player())
                                    .executes(CosmeticsCommand::copyCosmetics)
                            )
                    )
            )
            // /cosmetics reload - Recargar cosméticos del servidor
            .then(literal("reload")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                    .executes(CosmeticsCommand::reloadCosmetics)
            );

    private static int listCosmetics(CommandContext<CommandSourceStack> context, Identifier category) {
        context.getSource().sendSuccess(() ->
                        Component.literal("§6§l=== Cosméticos Disponibles ===")
                                .withStyle(style -> style.withColor(ChatFormatting.GOLD).withBold(true)),
                false
        );

        int count = 0;
        for (Map.Entry<Identifier, CosmeticDefinition> entry : ServerCosmeticStore.INSTANCE.getAll().entrySet()) {
            if (category != null) {
                if(!entry.getValue().getCategoryId().equals(category))
                 continue;
            }

            count++;
            Identifier id = entry.getKey();
            CosmeticDefinition info = entry.getValue();

            context.getSource().sendSuccess(() ->
                            Component.literal(String.format("§e%s §7- §f%s",
                                    id.toString(),
                                    info.getName() != null ? info.getName() : "Sin nombre"
                            )),
                    false
            );
        }

        final int totalCount = count;
        context.getSource().sendSuccess(() ->
                        Component.literal(String.format("§7Total: §f%d §7cosmético(s)", totalCount)),
                false
        );

        return 1;
    }

    private static int showCosmeticInfo(CommandContext<CommandSourceStack> context) {
        final Identifier id = IdentifierArgument.getId(context, "id");
        var cosmetic = ServerCosmeticStore.INSTANCE.get(id);

        if (cosmetic == null) {
            context.getSource().sendSuccess(() ->
                            Component.literal("§c✗ El cosmético '" + id + "' no existe"),
                    false
            );
            return 0;
        }

        context.getSource().sendSuccess(() ->
                        Component.literal("§6§l=== Información del Cosmético ===")
                                .withStyle(style -> style.withColor(ChatFormatting.GOLD).withBold(true)),
                false
        );

        context.getSource().sendSuccess(() ->
                        Component.literal(String.format("§eID: §f%s", id.toString())),
                false
        );

        context.getSource().sendSuccess(() ->
                        Component.literal(String.format("§eNombre: §f%s",
                                cosmetic.getName() != null ? cosmetic.getName() : "Sin nombre"
                        )),
                false
        );

        context.getSource().sendSuccess(() ->
                        Component.literal(String.format("§eSlot: §f%s",
                                cosmetic.getCategoryId().toString()
                        )),
                false
        );

        context.getSource().sendSuccess(() ->
                        Component.literal("§ePropiedades: §f"),
                false
        );

        for (Map.Entry<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> entry : cosmetic.getProperties().entrySet()) {
            Optional<String> propertyName = CosmeticPropertyRegistry.getTypeByClass(entry.getKey());
            propertyName.ifPresent(name -> {
                var property = entry.getValue();
                CosmeticProperty.PlayerData data = property.createPlayerData();
                if(data == null) return;
                CompoundTag nbt = data.toNbt();
                if(nbt.isEmpty()) return;

                MutableComponent hoverText = Component.literal("");
                boolean first = true;
                for (String key : nbt.keySet()) {
                    if (!first) {
                        hoverText.append("\n");
                    }
                    first = false;

                    Tag value = nbt.get(key);
                    String tipo = value == null ? "?" : value.getType().getName();

                    hoverText.append(Component.literal("§6" + key + "§7: §8[§e" + tipo + "§8]"));
                }

                // Texto principal con hover
                MutableComponent mainText = Component.literal("§7• §e" + name + "§7: §8{§7...§8}")
                        .withStyle(style -> style
                                .withHoverEvent(new HoverEvent.ShowText(hoverText))
                        );

                context.getSource().sendSuccess(() -> mainText, false);
            });
        }

        // Añade más información según tu CosmeticInfo
        // Por ejemplo: parte del cuerpo, tipo de slot, rareza, etc.

        return 1;
    }

    private static String getSimpleValue(Tag element) {
        return switch (element) {
            case StringTag nbtString -> "\"" + element.asString() + "\"";
            case CompoundTag compound -> "{...}";
            case ListTag nbtElements -> "[" + nbtElements.size() + " items]";
            case null, default -> element.toString();
        };
    }

    private static int equipCosmetic(CommandContext<CommandSourceStack> context, boolean hasNbt) throws CommandSyntaxException {
        final ServerPlayer player = EntityArgument.getPlayer(context, "player");
        final Identifier slot = IdentifierArgument.getId(context, "slot");
        final Identifier cosmeticId = IdentifierArgument.getId(context, "id");
        final CompoundTag nbt = hasNbt ? CompoundTagArgument.getCompoundTag(context, "nbt") : null;

        var cosmetic = ServerCosmeticStore.INSTANCE.get(cosmeticId);
        if (cosmetic == null) {
            context.getSource().sendSuccess(() ->
                            Component.literal("§c✗ El cosmético '" + cosmeticId + "' no existe"),
                    false
            );
            return 0;
        }

        PlayerData playerData = VentoServer.getPlayerManager().getFromPlayer(player);

        try {
            var result = playerData.getCosmetics().equip(cosmetic, slot, nbt);

            if (result.getCode().equals(ResponseType.SUCCESS)) {
                VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(player);
            }

            boolean success = result.getMessage().toLowerCase().contains("éxito") ||
                    result.getMessage().toLowerCase().contains("equipado");

            context.getSource().sendSuccess(() ->
                            Component.literal((success ? "§a✓ " : "§c✗ ") + result.getMessage()),
                    false
            );

            return success ? 1 : 0;
        } catch (Exception e) {
            if (e.getCause() instanceof ClassCastException) {
                context.getSource().sendSuccess(() ->
                                Component.literal("§c✗ Error al equipar el cosmético: El formato NBT no es correcto."),
                        false
                );
                return 0;
            }
            context.getSource().sendSuccess(() ->
                            Component.literal("§c✗ Error al equipar el cosmético: Algo salió mal."),
                    false
            );
            return 0;
        }
    }

    private static int unequipCosmetic(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final ServerPlayer player = EntityArgument.getPlayer(context, "player");
        final Identifier slot = IdentifierArgument.getId(context, "slot");

        PlayerData playerData = VentoServer.getPlayerManager().getFromPlayer(player);
        var result = playerData.getCosmetics().unEquip(slot);
        if(result.getCode().equals(ResponseType.SUCCESS)) {
            VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(player);
        }

        context.getSource().sendSuccess(() ->
                        Component.literal(String.format("§a✓ Slot §e%s §ade §e%s §adesequipado",
                                slot.toString(),
                                player.getName().getString()
                        )),
                false
        );

        return 1;
    }

    private static int clearAllCosmetics(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final ServerPlayer player = EntityArgument.getPlayer(context, "player");
        PlayerData playerData = VentoServer.getPlayerManager().getFromPlayer(player);

        playerData.getCosmetics().clearAll();

        VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(player);

        context.getSource().sendSuccess(() ->
                        Component.literal(String.format("§a✓ Todos los cosméticos de §e%s §ahan sido removidos",
                                player.getName().getString()
                        )),
                false
        );

        return 1;
    }

    private static int unlockAllCosmetics(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final ServerPlayer player = EntityArgument.getPlayer(context, "player");
        var result = VentoServer.getPlayerManager().getFromPlayer(player).getCosmetics().unlockAll();

        context.getSource().sendSuccess(() ->
                        Component.literal("§a✓ " + result.getMessage()),
                false
        );

        return 1;
    }

    private static int unlockCosmetic(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final ServerPlayer player = EntityArgument.getPlayer(context, "player");
        final Identifier cosmeticId = IdentifierArgument.getId(context, "id");

        var cosmetic = ServerCosmeticStore.INSTANCE.get(cosmeticId);
        if (cosmetic == null) {
            context.getSource().sendSuccess(() ->
                            Component.literal("§c✗ El cosmético '" + cosmeticId + "' no existe"),
                    false
            );
            return 0;
        }

        try {
            var result = VentoServer.getPlayerManager().getFromPlayer(player).getCosmetics().unlock(cosmeticId);

            boolean success = result.getMessage().toLowerCase().contains("desbloqueado") ||
                    result.getMessage().toLowerCase().contains("éxito");

            context.getSource().sendSuccess(() ->
                            Component.literal((success ? "§a✓ " : "§e⚠ ") + result.getMessage()),
                    false
            );

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            context.getSource().sendSuccess(() ->
                            Component.literal("§c✗ Error al desbloquear: " + e.getMessage()),
                    false
            );
            return 0;
        }
    }

    private static int showPlayerCosmetics(CommandContext<CommandSourceStack> context, String type) throws CommandSyntaxException {
        final ServerPlayer player = EntityArgument.getPlayer(context, "player");
        var cosmetics = VentoServer.getPlayerManager().getFromPlayer(player).getCosmetics();

        context.getSource().sendSuccess(() ->
                        Component.literal(String.format("§6§l=== Cosméticos de %s ===", player.getName().getString()))
                                .withStyle(style -> style.withColor(ChatFormatting.GOLD).withBold(true)),
                false
        );

        switch (type.toLowerCase()) {
            case "equipped":
                context.getSource().sendSuccess(() ->
                                Component.literal(cosmetics.showEquipment()),
                        false
                );
                break;
            case "unlocked":
                context.getSource().sendSuccess(() ->
                                Component.literal(cosmetics.showUnlockedCosmetics()),
                        false
                );
                break;
            case "all":
                context.getSource().sendSuccess(() ->
                                Component.literal("§e§lEquipados:"),
                        false
                );
                context.getSource().sendSuccess(() ->
                                Component.literal(cosmetics.showEquipment()),
                        false
                );
                context.getSource().sendSuccess(() ->
                                Component.literal("§e§lDesbloqueados:"),
                        false
                );
                context.getSource().sendSuccess(() ->
                                Component.literal(cosmetics.showUnlockedCosmetics()),
                        false
                );
                break;
            default:
                context.getSource().sendSuccess(() ->
                                Component.literal("§c✗ Tipo inválido. Usa: equipped, unlocked, o all"),
                        false
                );
                return 0;
        }

        return 1;
    }

    private static int copyCosmetics(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final ServerPlayer fromPlayer = EntityArgument.getPlayer(context, "from");
        final ServerPlayer toPlayer = EntityArgument.getPlayer(context, "to");

        if (fromPlayer.equals(toPlayer)) {
            context.getSource().sendSuccess(() ->
                            Component.literal("§c✗ No puedes copiar cosméticos a ti mismo"),
                    false
            );
            return 0;
        }

        PlayerData fromData = VentoServer.getPlayerManager().getFromPlayer(fromPlayer);
        PlayerData toData = VentoServer.getPlayerManager().getFromPlayer(toPlayer);

        context.getSource().sendSuccess(() -> Component.literal("WIP"), false);
        //toData.getCosmetics().copyEquipmentFrom(fromData.getCosmetics());

        VentoServer.getPlayerManager().sendToTrackingPlayersAndSelf(toPlayer);

        context.getSource().sendSuccess(() ->
                        Component.literal(String.format("§a✓ Cosméticos copiados de §e%s §aa §e%s",
                                fromPlayer.getName().getString(),
                                toPlayer.getName().getString()
                        )),
                false
        );

        return 1;
    }

    private static int reloadCosmetics(CommandContext<CommandSourceStack> context) {
        try {
            //ServerCosmeticStore.INSTANCE.reload();
            context.getSource().sendSuccess(() ->
                            Component.literal("No soportado en esta versión."),
                    false
            );

            context.getSource().sendSuccess(() ->
                            Component.literal("§a✓ Cosméticos recargados exitosamente"),
                    false
            );

            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            context.getSource().sendSuccess(() ->
                            Component.literal("§c✗ Error al recargar cosméticos: " + e.getMessage()),
                    false
            );
            return 0;
        }
    }
}
