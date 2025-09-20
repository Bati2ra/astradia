package com.astradia.player;

import com.astradia.api.player.BodyProportionsConfig;
import com.google.gson.JsonObject;

import java.util.UUID;

public class PlayerBodyProportions extends PlayerFeature {
    private final BodyProportionsConfig bodyProportionsConfig;

    public PlayerBodyProportions(UUID uuid) {
        super("proportions", uuid);
        bodyProportionsConfig = null;
    }

    @Override
    public void fromJson(JsonObject json) {
    }

    public BodyProportionsConfig getConfig() {
        return bodyProportionsConfig;
    }
}
