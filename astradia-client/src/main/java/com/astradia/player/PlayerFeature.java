package com.astradia.player;

import com.google.gson.JsonObject;
import java.util.UUID;

public abstract class PlayerFeature {
    protected final String identifier;
    protected final UUID uuid;

    public PlayerFeature(String identifier, UUID uuid) {
        this.identifier = identifier;
        this.uuid = uuid;
    }

    public abstract void fromJson(JsonObject json);

    public void onDisconnect() {};
}
