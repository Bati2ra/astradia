package com.astradia.api;

import com.astradia.utils.GsonUtils;
import com.google.gson.*;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CosmeticInfo {
    private final Identifier id;
    private final String name;

    private final Identifier slotId;

    private final String clazz;

    private final Map<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> properties;

    public CosmeticInfo(Identifier id, String name, Identifier slotId) {
        this.id = id;
        this.name = name;
        this.slotId = slotId;
        clazz = getClass().getName();
        properties = new HashMap<>();
    }

    public CosmeticInfo(JsonObject json) throws Exception {
        clazz = getClass().getName();
        properties = new HashMap<>();

        this.id = Identifier.tryParse(json.get("id").getAsString());
        this.name = json.get("name").getAsString();
        this.slotId = Identifier.of(json.get("slotId").getAsString());

        JsonArray typesArray = json.getAsJsonArray("properties");
        for (JsonElement el : typesArray) {
            CosmeticProperty<?> typeInstance = GsonUtils.GSON.fromJson(el, CosmeticProperty.class);
            if (typeInstance != null) {
                addProperty(typeInstance);
            }
        }
        validateProperties();
    }

    public void validateProperties() {
        for (CosmeticProperty<?> value : properties.values()) {
            value.validate(this);
        }
    }

    public <T extends CosmeticProperty<?>> void addProperty(T type) {
        //type.validate(this);
        properties.put(type.getKey(), type);
    }

    @SuppressWarnings("unchecked")
    public <T extends CosmeticProperty<?>> Optional<T> getProperty(Class<T> typeClass) {
        return Optional.ofNullable((T) properties.get(typeClass));
    }

    public Map<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> getProperties() {
        return properties;
    }

    public Identifier getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Identifier getSlotId() {
        return slotId;
    }

    @Override
    public String toString() {
        return "Cosmetic{" +
                "id='" + id + '\'' +
                ", displayName='" + name + '\'' +
                ", types=" + properties.keySet() +
                '}';
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("clazz", clazz);
        json.addProperty("id", id.toString());
        json.addProperty("name", name);
        json.addProperty("slotId", slotId.toString());

        // Serializamos el map como array
        JsonArray typesArray = new JsonArray();
        for (CosmeticProperty<?> type : properties.values()) {
            typesArray.add(GsonUtils.GSON.toJsonTree(type, CosmeticProperty.class));
        }
        json.add("properties", typesArray);

        return json;
    }
}
