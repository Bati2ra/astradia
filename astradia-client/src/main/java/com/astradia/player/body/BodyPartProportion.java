package com.astradia.player.body;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class BodyPartProportion {

    public enum Axis {
        X, Y, Z
    }

    public static class Range {
        public final float min;
        public final float max;

        public Range(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public boolean isValid(float value) {
            return value >= min && value <= max;
        }
    }

    private final EnumSet<Axis> linkedAxes;
    final Map<Axis, Range> ranges;
    final EnumMap<Axis, Float> values; // mutable

    public BodyPartProportion(EnumSet<Axis> linkedAxes,
                              Map<Axis, Range> ranges,
                              Map<Axis, Float> initialValues) {
        this.linkedAxes = linkedAxes;
        this.ranges = ranges;
        this.values = new EnumMap<>(Axis.class);
        this.values.putAll(initialValues); // copia mutable
    }

    public void setValue(Axis axis, float value) {
        if (ranges.get(axis).isValid(value)) {
            for (Axis linked : linkedAxes.contains(axis) ? linkedAxes : EnumSet.of(axis)) {
                values.put(linked, value);
            }
        }
    }

    public float getValue(Axis axis) {
        return values.get(axis);
    }

    public EnumSet<Axis> getLinkedAxes() {
        return linkedAxes;
    }

    public boolean isValid() {
        for (Axis axis : values.keySet()) {
            float value = values.get(axis);
            Range range = ranges.get(axis);
            if (range == null || !range.isValid(value)) {
                return false;
            }
        }
        return true;
    }
}
