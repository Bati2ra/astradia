package com.astradia.api;

import com.astradia.utils.GsonUtils;
import com.google.gson.*;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CosmeticDefinition {
    private final Identifier id;
    private final String name;
    private final Identifier categoryId;
    private final CosmeticRarity rarity;

    private final String clazz;

    private final Map<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> properties;

    public CosmeticDefinition(Identifier id, String name, Identifier categoryId, CosmeticRarity rarity) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.rarity = rarity;
        clazz = getClass().getName();
        properties = new HashMap<>();
    }

    public CosmeticDefinition(JsonObject json) throws Exception {
        clazz = getClass().getName();
        properties = new HashMap<>();

        this.id = Identifier.tryParse(json.get("id").getAsString());
        this.name = json.get("name").getAsString();
        this.categoryId = Identifier.tryParse(json.get("categoryId").getAsString());
        this.rarity = CosmeticRarity.valueOf(json.get("rarity").getAsString().toUpperCase());

        JsonArray propsArray = json.getAsJsonArray("properties");
        for (JsonElement el : propsArray) {
            CosmeticProperty<?> prop = GsonUtils.GSON.fromJson(el, CosmeticProperty.class);
            if (prop != null) {
                addProperty(prop);
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

    public CosmeticRarity getRarity() {
        return rarity;
    }

    public Identifier getCategoryId() {
        return categoryId;
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
        json.addProperty("categoryId", categoryId.toString());
        json.addProperty("rarity", rarity.getId());

        JsonArray propsArray = new JsonArray();
        for (CosmeticProperty<?> prop : properties.values()) {
            propsArray.add(GsonUtils.GSON.toJsonTree(prop, CosmeticProperty.class));
        }
        json.add("properties", propsArray);

        return json;
    }
}
