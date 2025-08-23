package com.astradia;

import com.astradia.api.CosmeticInfo;
import com.astradia.api.CosmeticProperty;
import com.astradia.api.player.CosmeticSlot;
import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import com.astradia.impl.AnimatableType;
import com.astradia.impl.ModelType;
import com.astradia.utils.GsonUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AstradiaTest implements ModInitializer  {
    @Override
    public void onInitialize() {
        CosmeticInfo cosmeticInfo = new CosmeticInfo(0, "Barba negra", Identifier.of("test"));
        cosmeticInfo.addType(new ModelType(Identifier.of("dpz", "textures/black_beard"), Identifier.of("dpz", "models/black_beard")));
        cosmeticInfo.addType(new AnimatableType());

        //var slot = new CosmeticSlot(Identifier.of("test"));
        //slot.equip(cosmeticInfo);

        //var cosmeticData = slot.getCosmeticData();
        //cosmeticData.getTypeData(AnimatableType.PlayerData.class).ifPresent(t -> t.setAnimation(Identifier.of("dpz", "coetenegro")));

        //printCosmetics(readCosmetics());
    }

    public List<CosmeticInfo> readCosmetics() {
        File dataFolder = new File(FabricLoader.getInstance().getGameDir().toFile(), "players/cosmetics");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        List<CosmeticInfo> allData = new ArrayList<>();
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null) return allData;

        for (File file : files) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                CosmeticInfo cosmetic = CosmeticInfo.fromJson(json);
                cosmetic.getProperties().values().forEach(t -> t.validate(cosmetic));
                allData.add(cosmetic);
            } catch (JsonSyntaxException e) {
                System.err.println("[LocalStore] Error de sintaxis en el JSON del archivo " + file.getName() + ": " + e.getMessage());
            } catch (IOException e) {
                System.err.println("[LocalStore] Error leyendo el archivo " + file.getName());
                e.printStackTrace();
            }
        }

        return allData;
    }

    public void writeCosmetics(List<CosmeticInfo> cosmeticInfos) {
        for (CosmeticInfo cosmeticInfo : cosmeticInfos) {
            writeCosmetic(cosmeticInfo);
        }
    }

    public void writeCosmetic(CosmeticInfo cosmeticInfo) {
        File dataFolder = new File(FabricLoader.getInstance().getGameDir().toFile(), "players/cosmetics");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File file = new File(dataFolder, cosmeticInfo.getId() + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            JsonObject json = cosmeticInfo.toJson();
            GsonUtils.GSON.toJson(json, writer); // Gson lo convierte a string en el archivo

            System.out.println("[LocalStore] Guardado exitoso para el jugador");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[LocalStore] Error al guardar datos del jugador");
        }
    }

    public void printCosmetics(List<CosmeticInfo> cosmeticInfos) {
        for (CosmeticInfo cosmeticInfo : cosmeticInfos) {
            printCosmetic(cosmeticInfo);
        }
    }

    public void printCosmetic(CosmeticInfo cosmeticInfo) {
        System.out.println("ID: " + cosmeticInfo.getId());
        System.out.println("NAME: " + cosmeticInfo.getName());
        System.out.println("SLOT ID: " + cosmeticInfo.getSlotId());
        System.out.println("TYPES: ");
        for (Map.Entry<Class<? extends CosmeticProperty<?>>, CosmeticProperty<?>> classCosmeticTypeEntry : cosmeticInfo.getProperties().entrySet()) {
            System.out.println("CLASS NAME: " + classCosmeticTypeEntry.getKey().getName());
            System.out.println("JSON: ");
            System.out.println(GsonUtils.GSON.toJsonTree(classCosmeticTypeEntry.getValue()));
            System.out.println("---------------");
        }
        System.out.println("-------------------");
    }
}
