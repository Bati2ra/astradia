package com.astradia.store.impl;

import com.astradia.VentoServer;
import com.astradia.store.Store;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PlayerLocalProportionsStore implements Store<JsonObject, UUID> {
    private static final String LOG_PREFIX = "[ProportionsStore]";
    private final File dataFolder;
    private final Gson gson;

    public PlayerLocalProportionsStore(MinecraftServer server) {
        this.dataFolder = new File(FabricLoader.getInstance().getGameDir().toFile(), "vento/players/proportions");
        if (!dataFolder.exists() && dataFolder.mkdirs()) {
            VentoServer.LOGGER.info("{} Created data folder at '{}'", LOG_PREFIX, dataFolder.getAbsolutePath());
        }
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private File getFile(UUID uuid) { return new File(dataFolder, uuid + ".json"); }


    @Override
    public void save(UUID id, JsonObject entity) {
        File file = getFile(id);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(entity, writer);
            VentoServer.LOGGER.info("{} Successfully saved proportions for player {}", LOG_PREFIX, id);
        } catch (IOException e) {
            VentoServer.LOGGER.error("{} Failed to save proportions for player {}", LOG_PREFIX, id);
            VentoServer.LOGGER.debug("{} IOException: {}", LOG_PREFIX, e.getMessage());
        }
    }

    @Override
    public Optional<JsonObject> findById(UUID uuid) {
        File file = getFile(uuid);
        if (!file.exists()) {
            VentoServer.LOGGER.warn("{} No proportions file found for player {}. Returning empty object.", LOG_PREFIX, uuid);
            return Optional.of(new JsonObject());
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            VentoServer.LOGGER.info("{} Loaded proportions for player {}", LOG_PREFIX, uuid);
            return Optional.ofNullable(data);
        } catch (JsonSyntaxException e) {
            VentoServer.LOGGER.error("{} Malformed JSON in proportions file for player {}", LOG_PREFIX, uuid);
            VentoServer.LOGGER.debug("{} JsonSyntaxException: {}", LOG_PREFIX, e.getMessage());
            return Optional.of(new JsonObject());
        } catch (IOException e) {
            VentoServer.LOGGER.error("{} Failed to read proportions file for player {}", LOG_PREFIX, uuid);
            VentoServer.LOGGER.debug("{} IOException: {}", LOG_PREFIX, e.getMessage());
            return Optional.of(new JsonObject());
        }
    }

    @Override
    public List<JsonObject> findAll() {
        VentoServer.LOGGER.warn("{} findAll() is not implemented. Returning empty list.", LOG_PREFIX);
        return new ArrayList<>();
    }

    @Override
    public void delete(UUID uuid) {
        VentoServer.LOGGER.warn("{} delete() is not implemented. Ignoring call.", LOG_PREFIX);
    }

    @Override
    public void saveAll(Map<UUID, JsonObject> map) {
        int successCount = 0;
        for (Map.Entry<UUID, JsonObject> entry : map.entrySet()) {
            try {
                save(entry.getKey(), entry.getValue());
                successCount++;
            } catch (Exception e) {
                VentoServer.LOGGER.warn("{} Failed to save proportions for player {}", LOG_PREFIX, entry.getKey());
            }
        }
        VentoServer.LOGGER.info("{} Batch save completed. {} entries saved.", LOG_PREFIX, successCount);
    }
}