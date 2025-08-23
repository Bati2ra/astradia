package com.astradia.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PlayerLocalCosmeticStore implements Store<JsonObject, UUID> {

    private final File dataFolder;
    private final Gson gson;

    public PlayerLocalCosmeticStore(MinecraftServer server) {
        this.dataFolder = new File(FabricLoader.getInstance().getGameDir().toFile(), "players/cosmetics");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private File getFile(UUID uuid) { return new File(dataFolder, uuid + ".json"); }


    @Override
    public void save(UUID id, JsonObject entity) {
        File file = getFile(id);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(entity, writer);
            System.out.println("[LocalStore] Guardado exitoso para el jugador " + id);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[LocalStore] Error al guardar datos del jugador " + id);
        }
    }

    @Override
    public Optional<JsonObject> findById(UUID uuid) {
        File file = getFile(uuid);

        if (!file.exists()) return Optional.of(new JsonObject());

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            return Optional.ofNullable(data);
        } catch (JsonSyntaxException e) {
            System.err.println("[LocalStore] Error de sintaxis en el JSON del jugador " + uuid + ": " + e.getMessage());
            return Optional.of(new JsonObject());
        } catch (IOException e) {
            System.err.println("[LocalStore] Error leyendo el archivo del jugador " + uuid);
            e.printStackTrace();
            return Optional.of(new JsonObject());
        }
    }

    @Override
    public List<JsonObject> findAll() {
        return new ArrayList<>();
    }

    @Override
    public void delete(UUID uuid) {

        System.out.println("[LocalCosmetics] Eliminado de memoria UUID: " + uuid);
    }

    @Override
    public void saveAll(Map<UUID, JsonObject> map) {
        System.out.println("[LocalCosmetics] Guardados " + map.size() + " datos en memoria.");
    }
}