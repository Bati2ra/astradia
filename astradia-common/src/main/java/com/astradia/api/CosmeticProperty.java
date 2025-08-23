package com.astradia.api;

import com.astradia.utils.GsonUtils;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

public abstract class CosmeticProperty<T extends CosmeticProperty.PlayerData> {
    private final String clazz;

    public CosmeticProperty() {
        clazz = getClass().getName();
    }

    public interface PlayerData {

        JsonObject toJson();
        void fromJson(JsonObject json);
    }

    public T createPlayerData() { return null; };

    public abstract Class<T> getPlayerDataClass();

    public abstract Class<? extends CosmeticProperty<T>> getKey();

    public Set<Class<? extends CosmeticProperty<?>>> requiredTypes() {
        return Collections.emptySet();
    }

    public Set<Class<? extends CosmeticProperty<?>>> incompatibleTypes() {
        return Collections.emptySet();
    }

    public void validate(CosmeticInfo cosmeticInfo) {
        for (Class<? extends CosmeticProperty<?>> required : requiredTypes()) {
            boolean hasRequired = cosmeticInfo.getProperties().keySet().stream()
                    .anyMatch(required::isAssignableFrom);
            if (!hasRequired) {
                throw new IllegalArgumentException(
                        getClass().getSimpleName() + " requiere " + required.getSimpleName()
                );
            }
        }

        for (Class<? extends CosmeticProperty<?>> incompatible : incompatibleTypes()) {
            boolean hasIncompatible = cosmeticInfo.getProperties().keySet().stream()
                    .anyMatch(incompatible::isAssignableFrom);
            if (hasIncompatible) {
                throw new IllegalArgumentException(
                        getClass().getSimpleName() + " es incompatible con " + incompatible.getSimpleName()
                );
            }
        }
    }

    public static class Serializer implements JsonDeserializer<CosmeticProperty<?>>, JsonSerializer<CosmeticProperty<?>> {
        public CosmeticProperty<?> deserialize(JsonElement input, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            String className = input.getAsJsonObject().get("clazz").getAsString();
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw  new RuntimeException("Cannot deserialize CosmeticType json: " + input);
            }
            // not a loop because typeAdapters are registered for specific classes not automatically all subclasses
            return (CosmeticProperty<?>) GsonUtils.GSON.fromJson(input, clazz);
        }

        public JsonElement serialize(CosmeticProperty<?> input, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            return GsonUtils.GSON.toJsonTree(input);
        }
    }
}
