package com.astradia.api.player;

import java.util.*;

public class BodyProportionsConfig {

    private final HashMap<String, ScaleParameter> parameters;

    private final ScaleParameter defaulted = new ScaleParameter("default", EnumSet.of(ScaleParameter.Axis.X, ScaleParameter.Axis.Y, ScaleParameter.Axis.Z), 1, 1, 1);

    public BodyProportionsConfig() {
        parameters = new HashMap<>();
    }

    public ScaleParameter getParameterById(String parameter) {
        return parameters.getOrDefault(parameter, defaulted);
    }

    public void initialize(List<ScaleParameter> parameters) {
        this.parameters.clear();
        for (ScaleParameter parameter : parameters) {
            this.parameters.put(parameter.getId(), parameter);
        }
    }

    public Collection<ScaleParameter> getAll() {
        return parameters.values();
    }

    public BodyProportionsConfig copy() {
        BodyProportionsConfig config = new BodyProportionsConfig();
        for (Map.Entry<String, ScaleParameter> entry : this.parameters.entrySet()) {
            ScaleParameter original = entry.getValue();
            ScaleParameter copied = new ScaleParameter(
                    original.getId(),
                    EnumSet.copyOf(original.getAxes()),
                    original.min,
                    original.max,
                    original.value
            );
            config.parameters.put(entry.getKey(), copied);
        }

        return config;
    }
}
