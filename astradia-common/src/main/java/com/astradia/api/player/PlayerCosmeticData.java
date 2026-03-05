package com.astradia.api.player;

import com.astradia.CosmeticStore;
import com.astradia.api.CosmeticDefinition;
import com.astradia.api.CosmeticProperty;
import com.astradia.api.CosmeticPropertyRegistry;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
/**
 * Representa los datos de un cosmético asignados a un jugador.
 * Se basa en un CosmeticInfo global y almacena datos específicos del jugador
 * para cada propiedad (CosmeticProperty) que requiera información de jugador.
 */
public class PlayerCosmeticData {
    /** Referencia al cosmético global asociado */
    private final CosmeticDefinition cosmeticDefinition;
    /**
     * Mapa que vincula cada tipo de propiedad que tenga datos de jugador
     * con la instancia concreta de PlayerData para este jugador.
     */
    private final Map<Class<? extends CosmeticProperty<?>>, CosmeticProperty.PlayerData> typeData;

    /**
     * Constructor estándar.
     * Inicializa todos los PlayerData a valores por defecto según las propiedades
     * del CosmeticInfo global.
     */
    public PlayerCosmeticData(CosmeticDefinition cosmetic) {
        this.cosmeticDefinition = cosmetic;
        typeData = new HashMap<>();
        initializeWithDefaultValues(cosmeticDefinition);
    }

    /**
     * Constructor desde JSON.
     * Carga un PlayerCosmeticData a partir de un JSON y un store de CosmeticInfo.
     * Si cualquier propiedad no se encuentra o el JSON tiene un formato incorrecto, lanza excepción.
     *
     * @param store CosmeticStore donde se buscan los CosmeticInfo globales
     * @param json  JsonObject con los datos del jugador
     * @throws Exception si hay discrepancias en los datos o formato incorrecto
     */
    public PlayerCosmeticData(CosmeticStore<? extends CosmeticDefinition> store, JsonObject json) throws Exception {
        String stringId = json.get("id").getAsString();
        Identifier cosmeticId = Identifier.tryParse(stringId);
        typeData = new HashMap<>();
        cosmeticDefinition = store.get(cosmeticId);
        if(cosmeticDefinition == null) {
            throw new Exception(
                    "No se encontró el cosmético '" + stringId + "' en el registro. '"
            );
        }
        initializeWithJsonValues(cosmeticDefinition, json);

    }

    private void initializeWithDefaultValues(CosmeticDefinition cosmeticDefinition) {
        var properties = cosmeticDefinition.getProperties();
        properties.forEach((propertyKey, property) -> {
            var playerDataClazz = property.getPlayerDataClass();
            if(playerDataClazz == null || playerDataClazz.equals(CosmeticProperty.PlayerData.class)) return;

            var playerData = property.createPlayerData();
            if(playerData == null) {
                throw new RuntimeException(
                        "No se pudo instanciar PlayerData para la propiedad '" + property + "' del cosmético '" + cosmeticDefinition.getName() + "'"
                );
            }
            typeData.put(propertyKey, playerData);
        });
    }

    private void initializeWithJsonValues(CosmeticDefinition cosmeticDefinition, JsonObject from) throws Exception {
        var properties = cosmeticDefinition.getProperties();
        for (Map.Entry<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> entry : properties.entrySet()) {
            var propertyKey = entry.getKey();
            var property = entry.getValue();
            var playerDataClazz = property.getPlayerDataClass();
            if(playerDataClazz == null || playerDataClazz.equals(CosmeticProperty.PlayerData.class)) continue;

            var playerData = property.createPlayerData();
            if(playerData == null) {
                throw new RuntimeException(
                        "No se pudo instanciar PlayerData para la propiedad '" + property + "' del cosmético '" + cosmeticDefinition.getName() + "'"
                );
            }
            var optionalKey = CosmeticPropertyRegistry.getTypeByClass(propertyKey);
            if(optionalKey.isPresent()) {
                var key = optionalKey.get();
                JsonObject jsonProperties = from.getAsJsonObject("properties");
                if(!jsonProperties.has(key)) {
                    throw new Exception(
                            "Formato JSON incorrecto: falta PlayerData '" + key +
                                    "' para el cosmético '" + cosmeticDefinition.getName() + "'"
                    );
                }
                JsonObject dataJson = jsonProperties.getAsJsonObject(key);
                playerData.fromJson(dataJson);
                typeData.put(propertyKey, playerData);
            } else {
                throw new Exception(
                        "No se puso encontrar la llave asignada a '" + propertyKey.getName()
                );
            }
        }
    }

    public void fromNbt(CompoundTag nbt) {
        JsonObject json = new JsonObject();
        var jsonProperties = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, nbt).getAsJsonObject();
        json.add("properties", jsonProperties);
        try {
            initializeWithJsonValues(cosmeticDefinition, json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Serializa todos los PlayerData de este jugador a JSON.
     * @return JsonObject representando todos los datos de jugador para este cosmético.
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonObject jsonProperties = new JsonObject();

        json.addProperty("id", cosmeticDefinition.getId().toString());
        for (Map.Entry<Class<? extends CosmeticProperty<?>>, CosmeticProperty.PlayerData> entry : typeData.entrySet()) {
            JsonObject dataJson = entry.getValue().toJson();
            if (dataJson != null) {
                String propertyKey = CosmeticPropertyRegistry.getTypeByClass(entry.getKey()).get();
                jsonProperties.add(propertyKey, dataJson);
            }
        }
        json.add("properties", jsonProperties);
        return json;
    }

    public CosmeticDefinition getCosmetic() {
        return cosmeticDefinition;
    }

    /**
     * Obtiene los datos de jugador para un tipo específico de propiedad.
     *
     * @param typeClass Clase del PlayerData que queremos obtener
     * @param <T> Tipo genérico que extiende PlayerData
     * @return Optional con la instancia si existe, vacío si no
     */
    public <R extends CosmeticProperty<?>, T extends CosmeticProperty.PlayerData> Optional<T> getTypeData(Class<R> clazz, Class<T> typeClass) {
        return Optional.ofNullable(typeClass.cast(typeData.get(clazz)));
    }
}
