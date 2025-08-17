package com.astradia;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;
/*
public class ClientPlayerManager {
    public static final ClientPlayerManager INSTANCE = new ClientPlayerManager();

    @Override
    public void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            var player = client.player;
            if(player == null) return;

            if(player.age % 1200 == 0) {
                cosmetics.entrySet().removeIf(entry -> entry.getValue().getPlayer() == null);
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(this::clearCache));
    }

    public void receiveServerPlayerData(NbtCompound tag) {
        UUID uuid = tag.getUuid("uuid");
        NbtCompound cosmeticsData = tag.getCompound("cosmetics");
        ClientPlayerCosmetics playerCosmetics = new ClientPlayerCosmetics(uuid);
        playerCosmetics.fromNbt(cosmeticsData);
        cosmetics.put(uuid, playerCosmetics);
        System.out.println("cipote");
    }

    @Override
    public ClientPlayerCosmetics getFrom(UUID uuid) {
        if(!cosmetics.containsKey(uuid)) {
            cosmetics.put(uuid, new ClientPlayerCosmetics(uuid));
        }
        return super.getFrom(uuid);
    }

    private void clearCache() {
        cosmetics.clear();
        AstradiaClient.LOGGER.info("[Cosmetics] Limpiando caché de jugadores");
    }
}
*/