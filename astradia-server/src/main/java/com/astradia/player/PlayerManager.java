package com.astradia.player;

import com.astradia.AstradiaServer;
import com.astradia.network.NetworkManager;
import com.astradia.network.payloads.PlayerDataPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    protected final HashMap<UUID, PlayerData> players;

    public PlayerManager() {
        players = new HashMap<>();
    }

    public void initialize() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> onPlayerDisconnect(handler.getPlayer()));
        ServerPlayConnectionEvents.JOIN.register(((serverPlayNetworkHandler, packetSender, minecraftServer) -> onPlayerConnect(serverPlayNetworkHandler.getPlayer())));
    }

    private void onPlayerDisconnect(ServerPlayerEntity player) {
        //var store = AstradiaServer.getPlayerCosmeticStore();
        //assert store != null;
        //store.save(player.getUuid(), getFrom(player.getUuid()));
        players.remove(player.getUuid());
    }

    private void onPlayerConnect(ServerPlayerEntity player) {
        //var store = AstradiaServer.getPlayerCosmeticStore();
        //assert store != null;
        //var optional = store.findById(player.getUuid());
        //optional.ifPresent(data -> cosmetics.put(player.getUuid(), data));
        sendToSelf(player);
    }

    public PlayerData getFromPlayer(PlayerEntity player) {
        return getFromUuid(player.getUuid());
    }

    public PlayerData getFromUuid(UUID uuid) {
        if(!players.containsKey(uuid)) {
            instantiatePlayer(uuid);
        }
        return players.get(uuid);
    }

    public void instantiatePlayer(UUID player) {
        PlayerData playerData = new PlayerData(player);
        players.put(player, playerData);
    }

    public void sendToPlayer(ServerPlayerEntity player, ServerPlayerEntity to) {
        PlayerData playerData = getFromPlayer(player);
        PlayerState status = playerData.getStatus();
        if(!status.equals(PlayerState.READY)) {
            AstradiaServer.LOGGER.info("[PlayerData] El jugador {} no está listo para recibir información.", player.getDisplayName().getString());
            return;
        }
        NbtCompound tag = playerData.toNbt();
        NetworkManager.sendToPlayer(to, new PlayerDataPayload(tag));
        AstradiaServer.LOGGER.info("[PlayerData] Enviando información de {} a {}.", player.getDisplayName().getString(), to.getDisplayName().getString());
    }

    public void sendToSelf(ServerPlayerEntity player) {
        sendToPlayer(player, player);
    }

    private void sendToTrackingPlayers(ServerPlayerEntity player, boolean withSelf) {
        PlayerData playerData = getFromPlayer(player);
        PlayerState status = playerData.getStatus();
        if(!status.equals(PlayerState.READY)) {
            AstradiaServer.LOGGER.info("[PlayerData] El jugador {} no está listo para recibir información.", player.getDisplayName().getString());
            return;
        }
        PlayerDataPayload payload = new PlayerDataPayload(playerData.toNbt());
        if(withSelf) ServerPlayNetworking.send(player, payload);
        for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.tracking(player)) {
            if(!AstradiaServer.getPlayerManager().getFromPlayer(serverPlayerEntity).getStatus().equals(PlayerState.READY)) continue;
            ServerPlayNetworking.send(serverPlayerEntity, payload);
            System.out.print(serverPlayerEntity.getDisplayName().getString() + ", ");
        }
        AstradiaServer.LOGGER.info("[Cosmetics] Enviando información de {} a todos los jugadores en rango.", player.getDisplayName().getString());
    }

    public void sendToTrackingPlayersAndSelf(ServerPlayerEntity player) {
        sendToTrackingPlayers(player, true);
    }

    public void sendToTrackingPlayers(ServerPlayerEntity player) {
        sendToTrackingPlayers(player, false);
    }
}
