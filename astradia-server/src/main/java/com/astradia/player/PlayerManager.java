package com.astradia.player;

import com.astradia.AstradiaServer;
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
    protected final HashMap<UUID, PlayerData> players;
    private final PlayerDataStore store;
    public PlayerManager(MinecraftServer server) {
        players = new HashMap<>();
        store = new PlayerDataStore(server);
    }

    public void initialize() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> onPlayerDisconnect(handler.getPlayer()));
        ServerPlayConnectionEvents.JOIN.register(((serverPlayNetworkHandler, packetSender, minecraftServer) -> onPlayerConnect(serverPlayNetworkHandler.getPlayer())));
        AstradiaServer.LOGGER.info("[PlayerManager] Inicializado y escuchando eventos de conexión/desconexión.");
    }

    /**
     * Obtiene PlayerData desde una entidad de jugador.
     */
    public PlayerData getFromPlayer(PlayerEntity player) {
        return getFromUuid(player.getUuid());
    }

    /**
     * Obtiene PlayerData desde un UUID.
     * Si el jugador aún no existe en el mapa, se instancia.
     */
    public PlayerData getFromUuid(UUID uuid) {
        if(!players.containsKey(uuid)) {
            instantiatePlayer(uuid);
        }
        return players.get(uuid);
    }

    /**
     * Crea y almacena un PlayerData vacío para un UUID específico.
     */
    public void instantiatePlayer(UUID player) {
        PlayerData playerData = new PlayerData(player);
        players.put(player, playerData);
        AstradiaServer.LOGGER.debug("[PlayerManager] PlayerData creado en memoria para UUID {}.", player);
    }

    /**
     * Evento: cuando un jugador se desconecta.
     * Aquí es buen lugar para guardar datos en la base de datos y limpiar memoria.
     */
    private void onPlayerDisconnect(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        AstradiaServer.LOGGER.info("[PlayerManager] Jugador {} (UUID {}) se desconectó. Guardando y removiendo datos...", player.getName().getString(), uuid);
        // TODO: guardar PlayerData en la base de datos de manera asíncrona.
        store.saveAll(uuid, getFromPlayer(player));
    }

    /**
     * Evento: cuando un jugador se conecta al servidor.
     * Aquí se inicia la carga de datos desde la base de datos.
     */
    private void onPlayerConnect(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        AstradiaServer.LOGGER.info("[PlayerManager] Jugador {} (UUID {}) se conectó. Iniciando carga de datos...", player.getName().getString(), uuid);

        // cargar datos de BD
        PlayerData playerData = store.load(uuid);
        playerData.setLoading(false);
        players.put(uuid, playerData);

        AstradiaServer.LOGGER.info("[PlayerData] Datos de {} cargados correctamente. Enviando información inicial a sí mismo y jugadores cercanos.", player.getName().getString());

        sendToTrackingPlayersAndSelf(player);
    }

    /**
     * Evento: cuando un jugador comienza a trackear a otro.
     */
    public void onPlayerTracking(ServerPlayerEntity player, ServerPlayerEntity trackedPlayer) {
        PlayerData trackedData = AstradiaServer.getPlayerManager().getFromPlayer(trackedPlayer);

        if (trackedData.isLoading()) {
            AstradiaServer.LOGGER.warn("[PlayerData] {} intentó trackear a {}, pero sus datos aún están cargando. Se ignorará.",
                    player.getName().getString(), trackedPlayer.getName().getString());
            return;
        }

        sendToPlayer(trackedPlayer, player);
    }

    /**
     * Envía los datos de un jugador a otro jugador específico.
     */
    public void sendToPlayer(ServerPlayerEntity source, ServerPlayerEntity target) {
        PlayerData playerData = getFromPlayer(source);
        NetworkManager.sendToPlayer(target, new PlayerDataPayload(playerData.toNbt()));

        AstradiaServer.LOGGER.debug("[PlayerData] Enviando datos de {} -> {}.", source.getName().getString(), target.getName().getString());
    }

    /**
     * Envía los datos de un jugador a sí mismo (útil al conectarse).
     */
    public void sendToSelf(ServerPlayerEntity player) {
        sendToPlayer(player, player);
    }

    /**
     * Envía los datos de un jugador a todos los que lo están trackeando.
     * Si withSelf es true, también se los envía al propio jugador.
     */
    private void sendToTrackingPlayers(ServerPlayerEntity player, boolean withSelf) {
        PlayerData playerData = getFromPlayer(player);

        PlayerDataPayload payload = new PlayerDataPayload(playerData.toNbt());
        if(withSelf) ServerPlayNetworking.send(player, payload);
        Collection<ServerPlayerEntity> trackingPlayers = PlayerLookup.tracking(player);
        for (ServerPlayerEntity serverPlayerEntity : trackingPlayers) {
            ServerPlayNetworking.send(serverPlayerEntity, payload);
            AstradiaServer.LOGGER.debug("[PlayerData] Enviando datos de {} -> {}.", player.getName().getString(), serverPlayerEntity.getName().getString());
        }
        AstradiaServer.LOGGER.info("[PlayerData] Datos de {} enviados a {} jugadores en rango ({} incluido: {}).",
                player.getName().getString(),
                trackingPlayers.size(),
                player.getName().getString(),
                withSelf);
    }

    public void sendToTrackingPlayersAndSelf(ServerPlayerEntity player) {
        sendToTrackingPlayers(player, true);
    }

    public void sendToTrackingPlayers(ServerPlayerEntity player) {
        sendToTrackingPlayers(player, false);
    }
}
