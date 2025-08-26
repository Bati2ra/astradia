package com.astradia.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CosmeticPropertyRegistry {
    private static final Map<String, Class<? extends CosmeticProperty<?>>> TYPE_TO_CLASS = new HashMap<>();
    private static final Map<Class<? extends CosmeticProperty<?>>, String> CLASS_TO_TYPE = new HashMap<>();

    public static void register(String typeId, Class<? extends CosmeticProperty<?>> clazz) {
        TYPE_TO_CLASS.put(typeId, clazz);
        CLASS_TO_TYPE.put(clazz, typeId);
    }

    public static Optional<Class<? extends CosmeticProperty<?>>> getClassByType(String typeId) {
        return Optional.ofNullable(TYPE_TO_CLASS.get(typeId));
    }

    public static Optional<String> getTypeByClass(Class<? extends CosmeticProperty<?>> clazz) {
        return Optional.ofNullable(CLASS_TO_TYPE.get(clazz));
    }
}
