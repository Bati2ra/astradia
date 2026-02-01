package com.astradia.player;

import com.astradia.api.player.BodyProportionValues;
import com.astradia.api.player.ScaleParameter;
import com.google.gson.JsonObject;

import java.util.UUID;

public class PlayerBodyProportions extends PlayerFeature {
    private final BodyProportionValues values;

    public PlayerBodyProportions(UUID uuid) {
        super("proportions", uuid);
        values = new BodyProportionValues();
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonObject paramsObj = json.getAsJsonObject("parameters");
        if (paramsObj == null) {
            return;
        }

        values.getAll().clear();

        for (var entry : paramsObj.entrySet()) {
            String paramId = entry.getKey();
            float value = entry.getValue().getAsFloat();
            values.getAll().put(paramId, value);
        }
    }

    public BodyProportionValues getValues() {
        return values;
    }
}
