package com.astradia;

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
    public static final ServerCosmeticStore INSTANCE = new ServerCosmeticStore();
    public JsonObject cachedSerializedCosmetics;
    public void initialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of(AstradiaServer.MOD_ID, "cosmetics");
            }

            @Override
            public void reload(ResourceManager manager) {
                cosmetics.clear();
                for(Map.Entry<Identifier, Resource> resourceEntry : manager.findResources("cosmetics", path -> path.toString().endsWith(".json")).entrySet()) {
                    try(InputStream stream = resourceEntry.getValue().getInputStream();
                        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)
                    ) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        CosmeticInfo cosmetic = new CosmeticInfo(json);
                        cosmetics.put(cosmetic.getId(), cosmetic);
                        AstradiaServer.LOGGER.info("Registering cosmetic {}", cosmetic.getName());
                    } catch(Exception e) {
                        AstradiaServer.LOGGER.error("Error occurred while loading resource json {}", resourceEntry.getKey().toString(), e);
                    }
                }
                cachedSerializedCosmetics = getSerializedCosmetics();
            }
        });
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((serverPlayer, joined) -> sendToPlayer(serverPlayer));
    }

    public void sendToPlayer(ServerPlayerEntity player) {
        System.out.println("test: " + cachedSerializedCosmetics.toString());
        ServerPlayNetworking.send(player, new CosmeticsDataPayload(cachedSerializedCosmetics.toString()));
        AstradiaServer.LOGGER.info("[Cosmetics] Enviando información de cosméticos a {}.", player.getDisplayName().getString());
    }

    private JsonObject getSerializedCosmetics() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        var iterator = cosmetics.entrySet().stream().iterator();
        while(iterator.hasNext()) {
            var entry = iterator.next();
            jsonArray.add(entry.getValue().toJson());
        }
        jsonObject.add("cosmetics", jsonArray);
        return jsonObject;
    }
}
