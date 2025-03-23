package com.astradia.utils;

import com.astradia.pojo.Cosmetic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonUtils {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(
                    RuntimeTypeAdapterFactory.of(Cosmetic.class, "type")
                            .registerSubtype(Cosmetic.class, "base")
                            //.registerSubtype(EquipableCosmetic.class, "equipable")
                            //.registerSubtype(AnimatedCosmetic.class, "animated")
            )
            .create();

    public static <T> T fromJson(InputStream inputStream, Class<T> classOfT) {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            return GSON.fromJson(reader, classOfT);
        } catch (JsonSyntaxException e) {
            System.err.println("Error de sintaxis en el JSON: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
