package com.astradia;

import com.astradia.pojo.CosmeticBase;

import java.util.HashMap;
import java.util.Map;

public class CosmeticStore<T extends CosmeticBase> {
    protected final HashMap<Integer, T> cosmetics = new HashMap<>();

    private void load() {
        cosmetics.clear();
    }

    public T get(Integer id) {
        return cosmetics.get(id);
    }

    public boolean isValid(Integer id) {
        return cosmetics.containsKey(id);
    }

    public Map<Integer, T> getAll() {
        return cosmetics;
    }
}
