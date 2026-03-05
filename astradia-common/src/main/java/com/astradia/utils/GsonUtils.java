package com.astradia.utils;

import com.astradia.api.CosmeticProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.Identifier;

import java.io.IOException;

public class GsonUtils {
    public static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, new TypeAdapter<Identifier>() {
                public void write(JsonWriter out, Identifier id) throws IOException {
                    out.value(id.toString()); }
                public Identifier read(JsonReader in) throws IOException {
                    String v = in.nextString();
                    return Identifier.tryParse(v); }
            })
            .registerTypeAdapter(CosmeticProperty.class, new CosmeticProperty.Serializer())
            .setPrettyPrinting()
            .create();
}
