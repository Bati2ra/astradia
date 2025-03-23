package com.astradia;
import com.astradia.network.payloads.PlayerCosmeticsDataPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class ServerPlayerCosmeticManager extends PlayerCosmeticManager<ServerPlayerCosmetics> {
    public static final ServerPlayerCosmeticManager INSTANCE = new ServerPlayerCosmeticManager();

    @Override
    public void initialize() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            var player = handler.getPlayer();
            remove(player.getUuid());
        });
        ServerPlayConnectionEvents.JOIN.register(((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            var player = serverPlayNetworkHandler.getPlayer();
            sendToSelf(player);
        }));
    }

    @Override
    public ServerPlayerCosmetics getFrom(UUID uuid) {
        if(!cosmetics.containsKey(uuid)) {
            create(uuid);
        }
        return cosmetics.get(uuid);
    }

    private void create(UUID uuid) {
        cosmetics.put(uuid, new ServerPlayerCosmetics(uuid));
    }

    public void sendToPlayer(ServerPlayerEntity player, ServerPlayerEntity to) {
        ServerPlayerCosmetics cosmeticsData = getFrom(to);
        ServerPlayNetworking.send(player, new PlayerCosmeticsDataPayload(cosmeticsData.toNbt()));
        AstradiaServer.LOGGER.info("[Cosmetics] Enviando información de equipamiento de {} a {}.", player.getDisplayName().getString(), to.getDisplayName().getString());
    }

    public void sendToSelf(ServerPlayerEntity player) {
        sendToPlayer(player, player);
    }

    private void sendToTrackingPlayers(ServerPlayerEntity player, boolean withSelf) {
        ServerPlayerCosmetics cosmeticsData = getFrom(player);
        var payload = new PlayerCosmeticsDataPayload(cosmeticsData.toNbt());
        if(withSelf) ServerPlayNetworking.send(player, payload);
        for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.tracking(player)) {
            ServerPlayNetworking.send(serverPlayerEntity, payload);
            System.out.print(serverPlayerEntity.getDisplayName().getString() + ", ");
        }
        AstradiaServer.LOGGER.info("[Cosmetics] Enviando información de equipamiento de {} a todos los jugadores en rango.", player.getDisplayName().getString());
    }

    public void sendToTrackingPlayersAndSelf(ServerPlayerEntity player) {
        sendToTrackingPlayers(player, true);
    }

    public void sendToTrackingPlayers(ServerPlayerEntity player) {
        sendToTrackingPlayers(player, false);
    }
}
