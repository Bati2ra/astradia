package com.astradia.player;

import com.astradia.VentoClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final UUID playerId;
    private final PlayerCosmetics cosmetics;
    private final PlayerBodyProportions proportions;
    protected final Map<Class<? extends PlayerFeature>, PlayerFeature> features;

    private static final Map<String, Class<? extends PlayerFeature>> FEATURE_TYPES = Map.of(
            "cosmetics", PlayerCosmetics.class,
            "proportions", PlayerBodyProportions.class
    );

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.features = new HashMap<>();
        cosmetics = new PlayerCosmetics(playerId);
        proportions = new PlayerBodyProportions(playerId);

        features.put(PlayerCosmetics.class, cosmetics);
        features.put(PlayerBodyProportions.class, proportions);
    }

    public PlayerData(JsonObject json) {
        this(UUID.fromString(json.get("uuid").getAsString()));
        JsonObject featuresJson = json.get("features").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : featuresJson.entrySet()) {
            String id = entry.getKey();
            JsonObject featureJson = entry.getValue().getAsJsonObject();
            var clazz = FEATURE_TYPES.get(id);
            if(clazz == null) {
                VentoClient.LOGGER.warn("Unknown feature key in JSON: {}", id);
                continue;
            };
            var feature = features.get(clazz);
            feature.fromJson(featureJson);
        }
    }

    public <T extends PlayerFeature> T getFeature(Class<T> featureClass) {
        return (T) features.get(featureClass);
    }

    public UUID getUuid() {
        return playerId;
    }

    public PlayerCosmetics getCosmetics() {
        return cosmetics;
    }

    public PlayerBodyProportions getProportions() {
        return proportions;
    }
}
