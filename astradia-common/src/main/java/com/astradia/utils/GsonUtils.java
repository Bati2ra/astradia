package com.astradia.utils;

import com.astradia.api.CosmeticProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.Identifier;

public class GsonUtils {
    public static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
            .registerTypeAdapter(CosmeticProperty.class, new CosmeticProperty.Serializer())
            .setPrettyPrinting()
            .create();
}
