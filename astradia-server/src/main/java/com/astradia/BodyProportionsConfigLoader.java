package com.astradia;

import com.astradia.api.player.BodyProportionsConfig;
import com.astradia.api.player.ScaleParameter;
import com.astradia.utils.GsonUtils;
import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BodyProportionsConfigLoader {
    private static final String LOG_PREFIX = "[BodyProportionsLoader]";
    public static BodyProportionsConfig CONFIG;

    private static final String DEFAULT_BODY_PROPORTIONS_JSON;

    public static void onReload() {
        File file = new File(VentoServer.getDataFolder(), "body_proportions.json");
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                JsonObject jsonObject = JsonParser.parseString(DEFAULT_BODY_PROPORTIONS_JSON).getAsJsonObject();
                GsonUtils.GSON.toJson(jsonObject, writer);
                VentoServer.LOGGER.info("[BodyProportionsLoader] Created default body proportions config file at '{}'", file.getAbsolutePath());
            } catch (IOException e) {
                VentoServer.LOGGER.error("[BodyProportionsLoader] Failed to write default body proportions config file at '{}'", file.getAbsolutePath());
                VentoServer.LOGGER.debug(e.getMessage());
            }
        }
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonObject data = GsonUtils.GSON.fromJson(reader, JsonObject.class);
            VentoServer.LOGGER.info("{} Loaded body proportions config", LOG_PREFIX);
            CONFIG = fromJson(data);
        } catch (JsonSyntaxException e) {
            VentoServer.LOGGER.error("{} Malformed JSON in body proportions config file", LOG_PREFIX);
            VentoServer.LOGGER.debug("{} JsonSyntaxException: {}", LOG_PREFIX, e.getMessage());
            CONFIG = getDefaultConfig();
        } catch (IOException e) {
            VentoServer.LOGGER.error("{} Failed to read body proportions config file", LOG_PREFIX);
            VentoServer.LOGGER.debug("{} IOException: {}", LOG_PREFIX, e.getMessage());
            CONFIG = getDefaultConfig();
        }
    }

    private static BodyProportionsConfig getDefaultConfig() {
        JsonObject json = JsonParser.parseString(DEFAULT_BODY_PROPORTIONS_JSON).getAsJsonObject();
        return fromJson(json);
    }

    public static BodyProportionsConfig fromJson(JsonObject json) {
        BodyProportionsConfig config = new BodyProportionsConfig();

        JsonObject paramsObj = json.getAsJsonObject("parameters");
        if (paramsObj == null) {
            return config;
        }
        List<ScaleParameter> parameters = new ArrayList<>();
        for (String key : paramsObj.keySet()) {
            JsonObject paramJson = paramsObj.getAsJsonObject(key);

            ScaleParameter param = GsonUtils.GSON.fromJson(paramJson, ScaleParameter.class);
            param.id = key;
            parameters.add(param);
        }
        config.initialize(parameters);
        return config;
    }

    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        JsonObject paramsObj = new JsonObject();

        for (var entry : CONFIG.getAll()) {
            paramsObj.add(entry.id, GsonUtils.GSON.toJsonTree(entry));
        }

        root.add("parameters", paramsObj);
        return root;
    }

    static {
        DEFAULT_BODY_PROPORTIONS_JSON = """
{
  "parameters": {
    "head.scale": {
      "axes": [
        "X",
        "Y",
        "Z"
      ],
      "min": 0.8,
      "max": 1.2,
      "value": 1.0
    },
    "torso.width": {
      "axes": [
        "X",
        "Z"
      ],
      "min": 0.6,
      "max": 1.6,
      "value": 1.0
    },
    "torso.height": {
      "axes": [
        "Y"
      ],
      "min": 0.5,
      "max": 1.5,
      "value": 1.0
    },
    "leftArm.width": {
      "axes": [
        "X",
        "Z"
      ],
      "min": 0.7,
      "max": 1.2,
      "value": 1.0
    },
    "leftArm.length": {
      "axes": [
        "Y"
      ],
      "min": 0.5,
      "max": 1.6,
      "value": 1.0
    },
    "rightArm.width": {
      "axes": [
        "X",
        "Z"
      ],
      "min": 0.7,
      "max": 1.2,
      "value": 1.0
    },
    "rightArm.length": {
      "axes": [
        "Y"
      ],
      "min": 0.5,
      "max": 1.6,
      "value": 1.0
    },
    "leftLeg.width": {
      "axes": [
        "X",
        "Z"
      ],
      "min": 0.8,
      "max": 1.2,
      "value": 1.0
    },
    "leftLeg.length": {
      "axes": [
        "Y"
      ],
      "min": 0.5,
      "max": 1.8,
      "value": 1.0
    },
    "rightLeg.width": {
      "axes": [
        "X",
        "Z"
      ],
      "min": 0.8,
      "max": 1.2,
      "value": 1.0
    },
    "rightLeg.length": {
      "axes": [
        "Y"
      ],
      "min": 0.5,
      "max": 1.8,
      "value": 1.0
    }
  }
}
""";
    }
}
