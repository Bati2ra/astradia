package com.astradia.player;

import com.astradia.BodyProportionsConfigLoader;
import com.astradia.api.player.BodyPartProportion;
import com.astradia.api.player.BodyProportionsConfig;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.UUID;

public class PlayerBodyProportions extends PlayerFeature {
    private final BodyProportionsConfig config;

    public PlayerBodyProportions(UUID uuid) {
        super("proportions", uuid);
        config = BodyProportionsConfigLoader.CONFIG;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        for (Map.Entry<String, BodyPartProportion> entry : config.getAllParts().entrySet()) {
            String partName = entry.getKey();
            BodyPartProportion part = entry.getValue();

            JsonObject partJson = new JsonObject();
            for (BodyPartProportion.Axis axis : part.getRanges().keySet()) {
                float value = part.getValue(axis);
                partJson.addProperty(axis.name().toLowerCase(), value);
            }
            root.add(partName.toLowerCase().replace(" ", "_"), partJson);
        }
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        for (Map.Entry<String, BodyPartProportion> entry : config.getAllParts().entrySet()) {
            String partName = entry.getKey();
            BodyPartProportion part = entry.getValue();

            String key = partName.toLowerCase().replace(" ", "_");
            if (!json.has(key)) continue;

            JsonObject partJson = json.getAsJsonObject(key);
            for (BodyPartProportion.Axis axis : part.getRanges().keySet()) {
                String axisKey = axis.name().toLowerCase();
                if (partJson.has(axisKey)) {
                    float value = partJson.get(axisKey).getAsFloat();
                    part.setValue(axis, value);
                }
            }
        }
    }
}
