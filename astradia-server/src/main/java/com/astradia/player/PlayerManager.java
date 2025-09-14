package com.astradia.player;

import com.astradia.VentoServer;
import com.astradia.network.NetworkManager;
import com.astradia.network.payloads.PlayerDataPayload;
import com.astradia.store.PlayerDataStore;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    private static final String LOG_PREFIX = "[PlayerManager]";
    private static final String DATA_PREFIX = "[PlayerData]";

    protected final HashMap<UUID, PlayerData> players;
    private final PlayerDataStore store;
    public PlayerManager(MinecraftServer server) {
        players = new HashMap<>();
        store = new PlayerDataStore(server);
    }

    public void initialize() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> onPlayerDisconnect(handler.getPlayer()));
        ServerPlayConnectionEvents.JOIN.register(((serverPlayNetworkHandler, packetSender, minecraftServer) -> onPlayerConnect(serverPlayNetworkHandler.getPlayer())));
        VentoServer.LOGGER.info("{} Initialized and listening for connection/disconnection events.", LOG_PREFIX);
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
        VentoServer.LOGGER.debug("{} Created in-memory PlayerData for UUID {}", LOG_PREFIX, player);
    }

    private void onPlayerDisconnect(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        VentoServer.LOGGER.info("{} Player '{}' (UUID {}) disconnected. Saving and clearing data...", LOG_PREFIX, player.getName().getString(), uuid);
        store.save(uuid, getFromPlayer(player));
    }

    private void onPlayerConnect(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        VentoServer.LOGGER.info("{} Player '{}' (UUID {}) connected. Loading data...", LOG_PREFIX, player.getName().getString(), uuid);

        PlayerData playerData = store.load(uuid);
        playerData.setLoading(false);
        players.put(uuid, playerData);

        VentoServer.LOGGER.info("{} Data for '{}' loaded successfully. Dispatching to self and nearby players.", DATA_PREFIX, player.getName().getString());
        sendToTrackingPlayersAndSelf(player);
    }

    public void onPlayerTracking(ServerPlayerEntity player, ServerPlayerEntity trackedPlayer) {
        PlayerData trackedData = VentoServer.getPlayerManager().getFromPlayer(trackedPlayer);

        if (trackedData.isLoading()) {
            VentoServer.LOGGER.debug("{} '{}' attempted to track '{}', but their data is still loading. Skipping transmission.",
                    DATA_PREFIX, player.getName().getString(), trackedPlayer.getName().getString());
            return;
        }

        sendToPlayer(trackedPlayer, player);
    }

    public void sendToPlayer(ServerPlayerEntity source, ServerPlayerEntity target) {
        PlayerData playerData = getFromPlayer(source);
        NetworkManager.sendToPlayer(target, new PlayerDataPayload(playerData.toJson().toString()));
        VentoServer.LOGGER.debug("{} Sent PlayerData from '{}' to '{}'.", DATA_PREFIX, source.getName().getString(), target.getName().getString());
    }

    private void sendToTrackingPlayers(ServerPlayerEntity player, boolean withSelf) {
        PlayerData playerData = getFromPlayer(player);

        PlayerDataPayload payload = new PlayerDataPayload(playerData.toJson().toString());
        if(withSelf) ServerPlayNetworking.send(player, payload);
        Collection<ServerPlayerEntity> trackingPlayers = PlayerLookup.tracking(player);
        for (ServerPlayerEntity tracker  : trackingPlayers) {
            ServerPlayNetworking.send(tracker , payload);
            VentoServer.LOGGER.debug("{} Sent PlayerData from '{}' to '{}'.", DATA_PREFIX, player.getName().getString(), tracker.getName().getString());
        }
        VentoServer.LOGGER.debug("{} Dispatched PlayerData for '{}' to {} tracking players (self included: {}).",
                DATA_PREFIX,
                player.getName().getString(),
                trackingPlayers.size(),
                withSelf);
    }

    public void sendToTrackingPlayersAndSelf(ServerPlayerEntity player) {
        sendToTrackingPlayers(player, true);
    }

    public void sendToTrackingPlayers(ServerPlayerEntity player) {
        sendToTrackingPlayers(player, false);
    }
}
