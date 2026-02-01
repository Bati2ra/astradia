package com.astradia.api.player;

import java.util.EnumSet;

public final class ScaleParameter {

    public String id; // ej: "torso.width"
    private final EnumSet<Axis> axes;
    public final float min;
    public final float max;
    float value;

    public ScaleParameter(
            String id,
            EnumSet<Axis> axes,
            float min,
            float max,
            float defaultValue
    ) {
        this.id = id;
        this.axes = axes;
        this.min = min;
        this.max = max;
        this.value = defaultValue;
    }

    public float clamp(float v) {
        return Math.max(min, Math.min(max, v));
    }

    public EnumSet<Axis> getAxes() {
        return axes;
    }

    public String getId() {
        return id;
    }

    public float getValue() {
        return clamp(value);
    }

    public void setValue(float value) {
        this.value = clamp(value);
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public static enum Axis {
        X, Y, Z
    }
}
