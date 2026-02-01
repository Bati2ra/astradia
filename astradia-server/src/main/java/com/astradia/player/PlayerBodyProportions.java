package com.astradia.player;

import com.astradia.BodyProportionsConfigLoader;
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
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        JsonObject paramsObj = new JsonObject();

        for (var entry : values.getAll().entrySet()) {
            String paramId = entry.getKey();
            float value = entry.getValue();

            // Solo guardar si el parámetro existe en la config actual
            ScaleParameter configParam = BodyProportionsConfigLoader.CONFIG.getParameterById(paramId);
            if (configParam != null && !configParam.getId().equals("default")) {
                // Clampear el valor según los límites de la config
                float clampedValue = configParam.clamp(value);
                paramsObj.addProperty(paramId, clampedValue);
            }
        }

        root.add("parameters", paramsObj);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonObject paramsObj = json.getAsJsonObject("parameters");
        if (paramsObj == null) {
            return;
        }

        values.getAll().clear(); // Limpia valores anteriores

        for (var entry : paramsObj.entrySet()) {
            String paramId = entry.getKey();

            // Solo cargar si el parámetro existe en la config actual
            ScaleParameter configParam = BodyProportionsConfigLoader.CONFIG.getParameterById(paramId);
            if (configParam != null && !configParam.getId().equals("default")) {
                float value = entry.getValue().getAsFloat();
                // Clampear el valor según los límites actuales de la config
                float clampedValue = configParam.clamp(value);
                values.getAll().put(paramId, clampedValue);
            }
        }
    }

    public BodyProportionValues getValues() {
        return values;
    }
}
