package com.astradia;

import com.astradia.api.CosmeticDefinition;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CosmeticStore<T extends CosmeticDefinition> {
    protected final HashMap<Identifier, T> cosmetics = new HashMap<>();

    private void load() {
        cosmetics.clear();
    }

    public T get(Identifier id) {
        return cosmetics.get(id);
    }

    public boolean isValid(Identifier id) {
        return cosmetics.containsKey(id);
    }

    public Map<Identifier, T> getAll() {
        return cosmetics;
    }
}
