package com.astradia.player;

import com.google.gson.JsonObject;
import java.util.UUID;

public abstract class PlayerFeature {
    protected final String identifier;
    protected final UUID uuid;
    protected boolean isDirty;

    public PlayerFeature(String identifier, UUID uuid) {
        this.identifier = identifier;
        this.uuid = uuid;
        this.isDirty = false;
    }

    public abstract JsonObject toJson();

    public abstract void fromJson(JsonObject json);

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }
}
