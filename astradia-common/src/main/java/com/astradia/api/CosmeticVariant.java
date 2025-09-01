package com.astradia.api;

import com.astradia.utils.GsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CosmeticVariant {
    private final String id;
    private final String displayName;

    private final Map<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> properties;

    public CosmeticVariant(JsonObject node) {
        this.id             = node.get("id").getAsString();
        this.displayName    = node.get("displayName").getAsString();
        properties = new HashMap<>();
        JsonArray typesArray = node.getAsJsonArray("properties");
        for (JsonElement el : typesArray) {
            CosmeticProperty<?> typeInstance = GsonUtils.GSON.fromJson(el, CosmeticProperty.class);
            if (typeInstance != null) {
                addProperty(typeInstance);
            }
        }
    }
    public CosmeticVariant(CosmeticDefinition cosmeticDefinition, JsonObject node) {
        this(node);
        validate(cosmeticDefinition);
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    private void validate(CosmeticDefinition cosmeticDefinition) {
        for (CosmeticProperty<?> value : properties.values()) {
            value.validate(cosmeticDefinition, this);
        }
    }

    public <T extends CosmeticProperty<?>> void addProperty(T type) {
        properties.put(type.getKey(), type);
    }

    @SuppressWarnings("unchecked")
    public <T extends CosmeticProperty<?>> Optional<T> getProperty(Class<T> typeClass) {
        return Optional.ofNullable((T) properties.get(typeClass));
    }

    public Map<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> getProperties() {
        return properties;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("displayName", displayName);
        // Serializamos el map como array
        JsonArray typesArray = new JsonArray();
        for (CosmeticProperty<?> type : properties.values()) {
            typesArray.add(GsonUtils.GSON.toJsonTree(type, CosmeticProperty.class));
        }
        json.add("properties", typesArray);
        return json;
    }
}
