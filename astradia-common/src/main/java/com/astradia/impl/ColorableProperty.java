package com.astradia.impl;

import com.astradia.api.CosmeticProperty;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Set;

public class ColorableProperty extends CosmeticProperty<ColorableProperty.PlayerData>{
    public static class PlayerData implements CosmeticProperty.PlayerData {
        private int color;

        public PlayerData(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("color", color);
            return jsonObject;
        }

        @Override
        public void fromJson(JsonObject json) {
            color = json.get("color").getAsInt();
        }

        @Override
        public void fromNbt(CompoundTag nbt) {
            color = nbt.getInt("color").orElse(-1);
        }

        @Override
        public CompoundTag toNbt() {
            CompoundTag compound = new CompoundTag();
            compound.putInt("color", color);
            return compound;
        }
    }
    @Override
    public Class<PlayerData> getPlayerDataClass() {
        return PlayerData.class;
    }

    @Override
    public Class<? extends CosmeticProperty<PlayerData>> getKey() {
        return ColorableProperty.class;
    }

    @Override
    public PlayerData createPlayerData() {
        return new PlayerData(-1);
    }

    @Override
    public List<Set<Class<? extends CosmeticProperty<?>>>> requiredProperties() {
        return List.of(Set.of(ModelProperty.class, TextureProperty.class));
    }
}
