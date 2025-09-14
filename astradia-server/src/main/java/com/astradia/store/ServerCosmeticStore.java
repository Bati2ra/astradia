package com.astradia.store;

import com.astradia.CosmeticStore;
import com.astradia.VentoServer;
import com.astradia.network.payloads.CosmeticsDataPayload;
import com.astradia.api.CosmeticInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerCosmeticStore extends CosmeticStore<CosmeticInfo> {
    private static final String LOG_PREFIX = "[CosmeticStore]";
    public static final ServerCosmeticStore INSTANCE = new ServerCosmeticStore();
    public JsonObject cachedSerializedCosmetics;
    public void initialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of(VentoServer.MOD_ID, "cosmetics");
            }

            @Override
            public void reload(ResourceManager manager) {
                VentoServer.LOGGER.info("{} Reloading cosmetic definitions from data packs...", LOG_PREFIX);
                cosmetics.clear();

                var resources = manager.findResources("cosmetics", path -> path.toString().endsWith(".json"));
                VentoServer.LOGGER.info("{} Found {} cosmetic JSON files.", LOG_PREFIX, resources.size());
                for(Map.Entry<Identifier, Resource> resourceEntry : resources.entrySet()) {
                    try(InputStream stream = resourceEntry.getValue().getInputStream();
                        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)
                    ) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        CosmeticInfo cosmetic = new CosmeticInfo(json);
                        cosmetics.put(cosmetic.getId(), cosmetic);
                        VentoServer.LOGGER.info("{} Registered cosmetic '{}' ({})", LOG_PREFIX, cosmetic.getName(), cosmetic.getId());
                    } catch(Exception e) {
                        VentoServer.LOGGER.error("{} Failed to load cosmetic JSON '{}'", LOG_PREFIX, resourceEntry.getKey());
                        VentoServer.LOGGER.debug("{} Exception: {}", LOG_PREFIX, e);
                    }
                }
                cachedSerializedCosmetics = getSerializedCosmetics();
                VentoServer.LOGGER.info("{} Cosmetic registry updated. Total cosmetics: {}", LOG_PREFIX, cosmetics.size());
            }
        });
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((serverPlayer, joined) -> sendToPlayer(serverPlayer));
    }

    public void sendToPlayer(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new CosmeticsDataPayload(cachedSerializedCosmetics.toString()));
        VentoServer.LOGGER.info("{} Sent cosmetic data to player '{}'", LOG_PREFIX, player.getGameProfile().getName());
    }

    private JsonObject getSerializedCosmetics() {
        JsonObject root = new JsonObject();
        JsonArray array = new JsonArray();

        cosmetics.values().forEach(cosmetic -> array.add(cosmetic.toJson()));
        root.add("cosmetics", array);

        return root;
    }
}
