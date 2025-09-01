package com.astradia.api.player;

import com.astradia.CosmeticStore;
import com.astradia.api.CosmeticDefinition;
import com.astradia.api.CosmeticProperty;
import com.astradia.api.CosmeticVariant;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

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
    private final CosmeticVariant cosmeticVariant;
    /**
     * Mapa que vincula cada tipo de propiedad que tenga datos de jugador
     * con la instancia concreta de PlayerData para este jugador.
     */
    private final Map<Class<? extends CosmeticProperty.PlayerData>, CosmeticProperty.PlayerData> typeData;

    /**
     * Constructor estándar.
     * Inicializa todos los PlayerData a valores por defecto según las propiedades
     * del CosmeticInfo global.
     */
    public PlayerCosmeticData(CosmeticDefinition cosmetic, String variantId) {
        this.cosmeticDefinition = cosmetic;
        this.cosmeticVariant = cosmetic.getVariant(variantId);
        typeData = new HashMap<>();
        for (CosmeticProperty<?> property : cosmeticVariant.getProperties().values()) {
            Class<? extends CosmeticProperty.PlayerData> dataClass = property.getPlayerDataClass();
            if (dataClass != null) {
                if(dataClass.equals(CosmeticProperty.PlayerData.class)) continue;

                try {
                    // Crear instancia por defecto de PlayerData
                    typeData.put(dataClass, property.createPlayerData());
                } catch (Exception e) {
                    throw new RuntimeException(
                            "No se pudo instanciar PlayerData para la propiedad '" + property + "' del cosmético '" + cosmetic.getDisplayName() + "', variante '" + cosmeticVariant.getDisplayName() + "'",
                            e
                    );
                }
            }
        }
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
        cosmeticDefinition = store.get(Identifier.of(json.get("id").getAsString()));
        cosmeticVariant = cosmeticDefinition.getVariant(json.get("variantId").getAsString());
        typeData = new HashMap<>();
        JsonObject jsonProperties = json.getAsJsonObject("properties");
        for (CosmeticProperty<?> property : cosmeticVariant.getProperties().values()) {
            Class<? extends CosmeticProperty.PlayerData> dataClass = property.getPlayerDataClass();
            if(dataClass == null) {
                throw new Exception(
                        "No se encontró la clase PlayerData para la propiedad '" + property + "' del cosmético '" + cosmeticDefinition.getDisplayName() + "', variante '" + cosmeticVariant.getDisplayName() + "'"
                );
            }
            if(dataClass.equals(CosmeticProperty.PlayerData.class)) {
                continue;
            }
            CosmeticProperty.PlayerData dataInstance = property.createPlayerData();
            if(!jsonProperties.has(dataClass.getName())) {
                throw new Exception(
                        "Formato JSON incorrecto: falta PlayerData '" + dataClass.getName() +
                                "' para el cosmético '" + cosmeticDefinition.getDisplayName() + "', variante '" + cosmeticVariant.getDisplayName() + "'"
                );
            }
            JsonObject dataJson = jsonProperties.getAsJsonObject(dataClass.getName());
            try {
                dataInstance.fromJson(dataJson);
            } catch (Exception e) {
                throw new Exception(
                        "Error al deserializar PlayerData '" + dataClass.getName() +
                                "' para el cosmético '" + cosmeticDefinition.getDisplayName() + "', variante '" + cosmeticVariant.getDisplayName() + "'" + "': " + e.getMessage(), e
                );
            }
            typeData.put(dataClass, dataInstance);
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
        json.addProperty("variantId", cosmeticVariant.getId());
        for (Map.Entry<Class<? extends CosmeticProperty.PlayerData>, CosmeticProperty.PlayerData> entry : typeData.entrySet()) {
            JsonObject dataJson = entry.getValue().toJson();
            if (dataJson != null) {
                String propertyKey = entry.getKey().getName();
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
    public <T extends CosmeticProperty.PlayerData> Optional<T> getTypeData(Class<T> typeClass) {
        return Optional.ofNullable(typeClass.cast(typeData.get(typeClass)));
    }
}
