package com.astradia.player;

import com.astradia.player.body.BodyProportionsConfig;
import com.google.gson.JsonObject;

import java.util.UUID;

public class PlayerBodyProportions extends PlayerFeature {
    private final BodyProportionsConfig bodyProportionsConfig;

    public PlayerBodyProportions(UUID uuid) {
        super("proportions", uuid);
        bodyProportionsConfig = BodyProportionsConfig.createDefaultConfig();
    }

    @Override
    public void fromJson(JsonObject json) {
    }

    public BodyProportionsConfig getConfig() {
        return bodyProportionsConfig;
    }
}
