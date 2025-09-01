package com.astradia.api;

import com.astradia.utils.GsonUtils;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class CosmeticProperty<T extends CosmeticProperty.PlayerData> {

    public CosmeticProperty() {}

    public interface PlayerData {

        default JsonObject toJson() { return null; };
        void fromJson(JsonObject json);
    }

    public T createPlayerData() { return null; };

    public abstract Class<T> getPlayerDataClass();

    public abstract Class<? extends CosmeticProperty<T>> getKey();

    /**
     * Returns a list of required property groups.
     * Each group is a set of properties where at least one must be present (OR).
     * All groups must be satisfied (AND between groups).
     */
    public List<Set<Class<? extends CosmeticProperty<?>>>> requiredProperties() {
        return new ArrayList<>();
    }

    /**
     * Returns a set of incompatible property classes.
     */
    public Set<Class<? extends CosmeticProperty<?>>> incompatibleTypes() {
        return Collections.emptySet();
    }

    /**
     * Validates that all required property groups are satisfied
     * and no incompatible types are present.
     */
    public void validate(CosmeticDefinition cosmeticDefinition, CosmeticVariant cosmeticVariant) {
        // Validate required property groups (AND between groups, OR within group)
        for (Set<Class<? extends CosmeticProperty<?>>> group : requiredProperties()) {
            boolean satisfied = cosmeticVariant.getProperties().keySet().stream()
                    .anyMatch(c -> group.stream().anyMatch(req -> req.isAssignableFrom(c)));

            if (!satisfied) {
                throw new IllegalArgumentException(
                        String.format("%s requires at least one of: %s",
                                getClass().getSimpleName(),
                                group.stream().map(Class::getSimpleName).toList())
                );
            }
        }

        // Validate incompatible properties
        for (Class<? extends CosmeticProperty<?>> incompatible : incompatibleTypes()) {
            boolean hasIncompatible = cosmeticVariant.getProperties().keySet().stream()
                    .anyMatch(incompatible::isAssignableFrom);
            if (hasIncompatible) {
                throw new IllegalArgumentException(
                        String.format("%s is incompatible with %s",
                                getClass().getSimpleName(),
                                incompatible.getSimpleName())
                );
            }
        }
    }

    /**
     * Custom serializer/deserializer for CosmeticProperty.
     * Supports both the registry-based "type" field and generates detailed debugging messages.
     */
    public static class Serializer implements JsonDeserializer<CosmeticProperty<?>>, JsonSerializer<CosmeticProperty<?>> {
        public CosmeticProperty<?> deserialize(JsonElement input, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject json = input.getAsJsonObject();
            Class<? extends CosmeticProperty<?>> clazz = null;

            // Attempt to load class from "type" field using registry
            if (json.has("type")) {
                String typeId = json.get("type").getAsString();
                clazz = CosmeticPropertyRegistry.getClassByType(typeId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "CosmeticProperty type is not registered in registry: " + typeId
                        ));
            }
            if (clazz == null) {
                throw new JsonParseException("Cannot determine CosmeticProperty class from JSON: " + json);
            }
            try {
                return GsonUtils.GSON.fromJson(json, clazz);
            } catch (Exception e) {
                throw new JsonParseException("Failed to deserialize CosmeticProperty of type: " + clazz.getSimpleName(), e);
            }
        }

        @Override
        public JsonElement serialize(CosmeticProperty<?> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = GsonUtils.GSON.toJsonTree(src).getAsJsonObject();

            // Add "type" from registry
            String typeId = CosmeticPropertyRegistry.getTypeByClass((Class<? extends CosmeticProperty<?>>) src.getClass())
                    .orElseThrow(() -> new IllegalStateException(
                            "CosmeticProperty class is not registered in registry: " + src.getClass()
                    ));
            json.addProperty("type", typeId);

            return json;
        }
    }
}
