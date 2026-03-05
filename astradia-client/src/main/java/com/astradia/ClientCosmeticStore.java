package com.astradia;

import com.astradia.pojo.ClientCosmeticDefinition;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;

public class ClientCosmeticStore extends CosmeticStore<ClientCosmeticDefinition> {
    public static final ClientCosmeticStore INSTANCE = new ClientCosmeticStore();
    private boolean isReady;

    private final Set<Identifier> categories = new HashSet<>();

    public void initialize() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(this::onLeave));
    }
    public void receiveServerStore(JsonObject json) {
        cosmetics.clear();
        categories.clear();
        if(json.has("cosmetics") && json.get("cosmetics").isJsonArray()) {
            JsonArray cosmeticsJson = json.getAsJsonArray("cosmetics");
            for (JsonElement jsonElement : cosmeticsJson) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                try {
                    ClientCosmeticDefinition cosmeticInfo = new ClientCosmeticDefinition(jsonObject);
                    cosmetics.put(cosmeticInfo.getId(), cosmeticInfo);
                    categories.add(cosmeticInfo.getCategoryId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        isReady = true;
    }

    public boolean isReady() {
        return isReady;
    }

    public void onLeave() {
        cosmetics.clear();
        isReady = false;
    }

    public Set<Identifier> getCategories() {
        return categories;
    }
}
