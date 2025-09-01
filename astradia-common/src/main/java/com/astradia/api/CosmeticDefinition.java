package com.astradia.api;

import com.google.gson.*;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Representa un cosmético completo, con todas sus variantes
 * y reglas globales
 */
public class CosmeticDefinition {
    private final Identifier id;
    private final String displayName;
    private final Identifier slotId;
    private final String clazz;

    private final List<CosmeticVariant> variants;
    private final Map<String, CosmeticVariant> variantMap;

    public CosmeticDefinition(Identifier id, String displayName, Identifier slotId) {
        this.id = id;
        this.displayName = displayName;
        this.slotId = slotId;
        clazz = getClass().getName();
        variants = new ArrayList<>();
        variantMap = new HashMap<>();
    }

    public CosmeticDefinition(JsonObject json) throws Exception {
        clazz = getClass().getName();
        variants = new ArrayList<>();
        
        this.id = Identifier.of(json.get("id").getAsString());
        this.displayName = json.get("displayName").getAsString();
        this.slotId = Identifier.of(json.get("slotId").getAsString());

        JsonArray variantArray = json.getAsJsonArray("variants");
        for (JsonElement element : variantArray) {
            variants.add(new CosmeticVariant(this, element.getAsJsonObject()));
        }
        this.variantMap = variants.stream()
                .collect(Collectors.toMap(CosmeticVariant::getId, Function.identity()));
    }

    public Identifier getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Identifier getSlotId() {
        return slotId;
    }

    /** Devuelve la lista de todas las variantes definidas. */
    public List<CosmeticVariant> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    /** Busca una variante por su ID, o lanza si no existe. */
    public CosmeticVariant getVariant(String variantId) {
        CosmeticVariant v = variantMap.get(variantId);
        if (v == null) {
            throw new IllegalArgumentException("Variant no encontrada: " + variantId);
        }
        return v;
    }

    @Override
    public String toString() {
        return "Cosmetic{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", variants=" + variantMap.keySet() +
                '}';
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("clazz", clazz);
        json.addProperty("id", id.toString());
        json.addProperty("displayName", displayName);
        json.addProperty("slotId", slotId.toString());

        JsonArray variantsArray = new JsonArray();
        for (CosmeticVariant variant : variants) {
            variantsArray.add(variant.toJson());
        }
        json.add("variants", variantsArray);
        return json;
    }
}
