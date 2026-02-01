package com.astradia.api.player;

import java.util.Collection;
import java.util.HashMap;

public class BodyProportionValues {
    private final HashMap<String, Float> parameters;

    public BodyProportionValues() {
        parameters = new HashMap<>();
    }

    public Float getParameterById(String parameter) {
        return parameters.getOrDefault(parameter, 1.0F);
    }

    public HashMap<String, Float> getAll() {
        return parameters;
    }
}
