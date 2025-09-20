package com.astradia;

import com.astradia.api.player.BodyPartProportion;
import com.astradia.api.player.BodyProportionsConfig;
import com.astradia.utils.GsonUtils;
import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

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

    private static BodyProportionsConfig fromJson(JsonObject root) {
        Map<String, BodyPartProportion> cache = new HashMap<>();

        Function<String, BodyPartProportion> resolve = name -> {
            JsonElement node = root.get(name);
            if (node.isJsonPrimitive() && node.getAsString().startsWith("@")) {
                String ref = node.getAsString().substring(1);
                return cache.get(ref);
            }

            JsonObject obj = node.getAsJsonObject();

            EnumSet<BodyPartProportion.Axis> linkedAxes = parseLinkedAxes(obj.get("linked").getAsString());
            Map<BodyPartProportion.Axis, BodyPartProportion.Range> ranges = parseRanges(obj.getAsJsonObject("range"), linkedAxes);
            Map<BodyPartProportion.Axis, Float> values = parseValues(obj.getAsJsonObject("value"), linkedAxes);

            BodyPartProportion part = new BodyPartProportion(linkedAxes, ranges, values);
            cache.put(name, part);
            return part;
        };

        return new BodyProportionsConfig(
                resolve.apply("head"),
                resolve.apply("torso"),
                resolve.apply("leftArm"),
                resolve.apply("rightArm"),
                resolve.apply("leftLeg"),
                resolve.apply("rightLeg"),
                resolve.apply("width"),
                resolve.apply("height")
        );
    }

    private static EnumSet<BodyPartProportion.Axis> parseLinkedAxes(String raw) {
        EnumSet<BodyPartProportion.Axis> set = EnumSet.noneOf(BodyPartProportion.Axis.class);
        for (char c : raw.toLowerCase().toCharArray()) {
            switch (c) {
                case 'x' -> set.add(BodyPartProportion.Axis.X);
                case 'y' -> set.add(BodyPartProportion.Axis.Y);
                case 'z' -> set.add(BodyPartProportion.Axis.Z);
            }
        }
        return set;
    }

    private static Map<BodyPartProportion.Axis, BodyPartProportion.Range> parseRanges(JsonObject obj, EnumSet<BodyPartProportion.Axis> linkedAxes) {
        Map<BodyPartProportion.Axis, BodyPartProportion.Range> map = new EnumMap<>(BodyPartProportion.Axis.class);
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            EnumSet<BodyPartProportion.Axis> axes = parseLinkedAxes(entry.getKey());
            JsonArray arr = entry.getValue().getAsJsonArray();
            float min = arr.get(0).getAsFloat();
            float max = arr.get(1).getAsFloat();
            for (BodyPartProportion.Axis axis : axes) {
                map.put(axis, new BodyPartProportion.Range(min, max));
            }
        }
        return map;
    }

    private static Map<BodyPartProportion.Axis, Float> parseValues(JsonObject obj, EnumSet<BodyPartProportion.Axis> linkedAxes) {
        Map<BodyPartProportion.Axis, Float> map = new EnumMap<>(BodyPartProportion.Axis.class);
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            EnumSet<BodyPartProportion.Axis> axes = parseLinkedAxes(entry.getKey());
            float value = entry.getValue().getAsFloat();
            for (BodyPartProportion.Axis axis : axes) {
                map.put(axis, value);
            }
        }
        return map;
    }

    static {
        DEFAULT_BODY_PROPORTIONS_JSON = """
{
  "head": {
    "linked": "xyz",
    "range": {
      "xyz": [
        0.8,
        1.2
      ]
    },
    "value": {
      "xyz": 1.0
    }
  },
  "torso": {
    "linked": "xz",
    "range": {
      "xz": [
        0.6,
        1.6
      ],
      "y": [
        0.5,
        1.5
      ]
    },
    "value": {
      "xz": 1.0,
      "y": 1.0
    }
  },
  "leftArm": {
    "linked": "xz",
    "range": {
      "xz": [
        0.7,
        1.2
      ],
      "y": [
        0.5,
        1.6
      ]
    },
    "value": {
      "xz": 1.0,
      "y": 1.0
    }
  },
  "rightArm": "@leftArm",
  "leftLeg": {
    "linked": "xz",
    "range": {
      "xz": [
        0.8,
        1.2
      ],
      "y": [
        0.5,
        1.8
      ]
    },
    "value": {
      "xz": 1.0,
      "y": 1.0
    }
  },
  "rightLeg": "@leftLeg",
  "width": {
    "linked": "xz",
    "range": {
      "xz": [
        0.6,
        1.6
      ]
    },
    "value": {
      "xz": 1.0
    }
  },
  "height": {
    "linked": "y",
    "range": {
      "y": [
        0.5,
        2.0
      ]
    },
    "value": {
      "y": 1.0
    }
  }
}
""";
    }
}
