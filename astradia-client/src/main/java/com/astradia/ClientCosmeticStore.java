package com.astradia;

import com.astradia.pojo.ClientCosmeticDefinition;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.Identifier;

public class ClientCosmeticStore extends CosmeticStore<ClientCosmeticDefinition> {
    public static final ClientCosmeticStore INSTANCE = new ClientCosmeticStore();
    private boolean isReady;

    public void initialize() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(this::onLeave));
    }
    public void receiveServerStore(JsonObject json) {
        cosmetics.clear();
        System.out.println("Client Cosmetics: ");
        System.out.print(json.toString());
        if(json.has("cosmetics") && json.get("cosmetics").isJsonArray()) {
            JsonArray cosmeticsJson = json.getAsJsonArray("cosmetics");
            for (JsonElement jsonElement : cosmeticsJson) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                try {
                    Identifier id = Identifier.of(jsonObject.get("id").getAsString());
                    cosmetics.put(id, new ClientCosmeticDefinition(jsonObject));
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
}
