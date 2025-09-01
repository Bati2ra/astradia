package com.astradia.command;

import com.astradia.AstradiaServer;
import com.astradia.ServerCosmeticStore;
import com.astradia.api.CosmeticDefinition;
import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import com.astradia.player.PlayerData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandManager {
   private static final SuggestionProvider<ServerCommandSource> BODY_PART_SUGGESTIONS = (context, builder) -> suggest(builder, Arrays.stream(BodyPart.values()).map(Enum::name).collect(Collectors.toSet()));
    private static final SuggestionProvider<ServerCommandSource> COSMETIC_SUGGESTIONS = (context, builder) -> suggestWithTooltip(builder, null, -1);
    private static final SuggestionProvider<ServerCommandSource> SLOT_TYPE_SUGGESTIONS = (context, builder) -> suggestSlots(builder, StringArgumentType.getString(context, "bodyPart"));

    public static final LiteralArgumentBuilder<ServerCommandSource> UNLOCK_COMMAND = literal("unlock")
            .then(argument("player", EntityArgumentType.player())
                    .executes(context -> {
                        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                        var result = AstradiaServer.getPlayerManager().getFromPlayer(player).getCosmetics().unlockAll();
                        context.getSource().sendFeedback(() -> Text.literal(result.getMessage()), false);
                        return 1;
                    })
                    .then(argument("id", IdentifierArgumentType.identifier())
                            .suggests(COSMETIC_SUGGESTIONS)
                            .executes(context -> {
                                final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                final Integer value = IntegerArgumentType.getInteger(context, "id");
                                try {
                                    var result = AstradiaServer.getPlayerManager().getFromPlayer(player).getCosmetics().unlock(value);
                                    context.getSource().sendFeedback(() -> Text.literal(result.getMessage()), false);
                                } catch(Exception exception) {
                                    exception.printStackTrace();
                                }
                                return 1;
                            })
                    )
            );
    public static final LiteralArgumentBuilder<ServerCommandSource> EQUIP_COMMAND = literal("equip")
            .then(argument("player", EntityArgumentType.player())
                        .then(argument("slot", StringArgumentType.string())
                                .then(argument("id", IntegerArgumentType.integer())
                                       .executes(context -> equipCosmetic(context, false))
                                        .then(argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                                .executes(context -> equipCosmetic(context, true))
                                        )
                                )
                        )

            );
    public static final LiteralArgumentBuilder<ServerCommandSource> UNEQUIP_COMMAND = literal("unequip")
            .then(argument("player", EntityArgumentType.player())
                            .then(argument("slot", StringArgumentType.string())
                                    .executes(context -> {
                                        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                        final Identifier slot = Identifier.of(StringArgumentType.getString(context, "slot"));
                                        AstradiaServer.getPlayerManager().getFromPlayer(player).getCosmetics().unequipCosmetic(slot);

                                        return 1;
                                    })
                            )
            );
    public static final LiteralArgumentBuilder<ServerCommandSource> SHOW_COMMAND = literal("show")
            .then(argument("player", EntityArgumentType.player())
                    .then(argument("option", StringArgumentType.string())
                            .executes(context -> {
                                final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                final String option = StringArgumentType.getString(context, "option");
                                var cosmetics = AstradiaServer.getPlayerManager().getFromPlayer(player).getCosmetics();
                                if(option.contentEquals("equipment")) {
                                    context.getSource().sendFeedback(() -> Text.literal(cosmetics.showEquipment()), false);
                                }
                                if(option.contentEquals("unlocked")) {
                                    context.getSource().sendFeedback(() -> Text.literal(cosmetics.showUnlockedCosmetics()), false);
                                }
                                if(option.contentEquals("network")) {
                                    context.getSource().sendFeedback(() -> Text.literal(AstradiaServer.getPlayerManager().getFromPlayer(player).toJson().toString()), false);
                                }
                                return 1;
                            })
                    )
            );

    public static final LiteralArgumentBuilder<ServerCommandSource> SHOW_COSMETICS = literal("showc")


                            .executes(context -> {
                                var store = ServerCosmeticStore.INSTANCE.cachedSerializedCosmetics;
                                context.getSource().sendFeedback(() -> Text.literal(store.toString()), false);
                                return 1;
                            }

            );

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("astradia")
                    .requires((source) -> source.hasPermissionLevel(3))
                    .then(UNLOCK_COMMAND)
                    .then(EQUIP_COMMAND)
                    .then(UNEQUIP_COMMAND)
                    .then(SHOW_COMMAND)
                    .then(SHOW_COSMETICS)
            );
        });

    }

    private static int equipCosmetic(CommandContext<ServerCommandSource> context, boolean hasNbt) throws CommandSyntaxException {
        final ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");

        Identifier slot = Identifier.of(StringArgumentType.getString(context, "slot"));
        final Identifier value = IdentifierArgumentType.getIdentifier(context, "id");
        final NbtCompound nbt = hasNbt ?  NbtCompoundArgumentType.getNbtCompound(context, "nbt") : null;

        var cosmetic = ServerCosmeticStore.INSTANCE.get(value);
        if(cosmetic == null) {
            context.getSource().sendFeedback(() -> Text.literal("El id especificado no pertenece a ningún cosmético"), false);
            return 0;
        }
        PlayerData playerData = AstradiaServer.getPlayerManager().getFromPlayer(player);
        try {
            var result = playerData.getCosmetics().equipCosmetic(slot, cosmetic, nbt);
            if(playerData.getCosmetics().isDirty()) {
                AstradiaServer.getPlayerManager().sendToTrackingPlayersAndSelf(player);
            }
            context.getSource().sendFeedback(() -> Text.literal(result.getMessage()), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*var result = ServerPlayerManager.INSTANCE.getFrom(player).equipItem(
                cosmetic,
                part,
                slot,
                nbt
        );
        if(result.getCode().equals(ResponseType.SUCCESS)) {
            ServerPlayerManager.INSTANCE.sendToTrackingPlayersAndSelf(player);
        }
        context.getSource().sendFeedback(() -> Text.literal(result.getMessage()), false);*/
        return 1;
    }

    private static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, Collection<String> suggestions) {
        for (String s : suggestions) {
            builder.suggest(s);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestSlots(SuggestionsBuilder builder, String bodyPartString) {
        try {
            var bodyPart = BodyPart.valueOf(bodyPartString);
            int i = 0;
            for (SlotType slot : bodyPart.getSlots()) {
                builder.suggest(i++, Text.literal("Slot type: " + slot.name()));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestWithTooltip(SuggestionsBuilder builder, @Nullable String bodyPartString, int slotTypeIndex) {
        BodyPart bodyPart = null;
        SlotType slotType = null;
        if(bodyPartString != null) {
            try {
                bodyPart = BodyPart.valueOf(bodyPartString);
                slotType = bodyPart.getSlots()[slotTypeIndex];
            } catch (Exception ignored) {}
        }
        for (Map.Entry<Identifier, CosmeticDefinition> entry : ServerCosmeticStore.INSTANCE.getAll().entrySet()) {
            if(bodyPart != null) {
                //if(!entry.getValue().getBodyPart().equals(bodyPart)) continue;
            }
            if(slotType != null) {
                //if(!entry.getValue().getSlotType().equals(slotType)) continue;
            }

            /*Text tooltip = Text.literal("Name: " + entry.getValue().getName() + ", ")
                    .append(Text.literal("Body Part: " + entry.getValue().getBodyPart().name() + ", "))
                    .append(Text.literal("Slot Type: " + entry.getValue().getSlotType().name()));
            builder.suggest(entry.getKey(), tooltip); // ID con nombre
        */}
        return builder.buildFuture();
    }


}
