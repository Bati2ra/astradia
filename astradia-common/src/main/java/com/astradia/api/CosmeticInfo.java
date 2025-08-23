package com.astradia.api;

import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import com.astradia.utils.GsonUtils;
import com.google.gson.*;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CosmeticInfo {
    private final Integer id;
    private final String name;

    private final Identifier slotId;

    private final String clazz;

    private final Map<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> properties;

    public CosmeticInfo(Integer id, String name, Identifier slotId) {
        this.id = id;
        this.name = name;
        this.slotId = slotId;
        clazz = getClass().getName();
        properties = new HashMap<>();
    }

    public <T extends CosmeticProperty<?>> void addType(T type) {
        type.validate(this);
        properties.put(type.getKey(), type);
    }

    @SuppressWarnings("unchecked")
    public <T extends CosmeticProperty<?>> Optional<T> getType(Class<T> typeClass) {
        return Optional.ofNullable((T) properties.get(typeClass));
    }

    public Map<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> getProperties() {
        return properties;
    }

    public Integer getId() {
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
        json.addProperty("id", id);
        json.addProperty("name", name);
        json.addProperty("slotId", slotId.toString());

        // Serializamos el map como array
        JsonArray typesArray = new JsonArray();
        for (CosmeticProperty<?> type : properties.values()) {
            typesArray.add(GsonUtils.GSON.toJsonTree(type));
        }
        json.add("properties", typesArray);

        return json;
    }

    public static CosmeticInfo fromJson(JsonObject json) {
        Integer id = json.get("id").getAsInt();
        String name = json.get("name").getAsString();
        Identifier slotId = Identifier.of(json.get("slotId").getAsString());

        CosmeticInfo cosmetic = new CosmeticInfo(id, name, slotId);

        JsonArray typesArray = json.getAsJsonArray("properties");
        for (JsonElement el : typesArray) {
            CosmeticProperty<?> typeInstance = GsonUtils.GSON.fromJson(el, CosmeticProperty.class);
            if (typeInstance != null) {
                cosmetic.addType(typeInstance);
            }
        }

        return cosmetic;
    }
}
