package com.astradia.impl;

import com.astradia.api.CosmeticProperty;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Set;

public class AnimatableType extends CosmeticProperty<AnimatableType.PlayerData> {
    public static class PlayerData implements CosmeticProperty.PlayerData {
        private Identifier animationPath;

        public PlayerData(Identifier animation) {
            this.animationPath = animation;
        }

        public Identifier getAnimation() { return animationPath; }
        public void setAnimation(Identifier animation) { this.animationPath = animation; }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("animationPath", animationPath.toString());
            return jsonObject;
        }

        @Override
        public void fromJson(JsonObject json) {
            animationPath = Identifier.of(json.get("animationPath").getAsString());
        }
    }
    private final Identifier animationPath;

    public AnimatableType(Identifier animationPath) {
        super();
        this.animationPath = animationPath;
    }

    public Identifier getAnimationPath() {
        return animationPath;
    }

    @Override
    public PlayerData createPlayerData() {
        return new PlayerData(Identifier.of("", ""));
    }

    @Override
    public Class<PlayerData> getPlayerDataClass() {
        return PlayerData.class;
    }

    @Override
    public Class<? extends CosmeticProperty<PlayerData>> getKey() {
        return AnimatableType.class;
    }

    @Override
    public Set<Class<? extends CosmeticProperty<?>>> requiredTypes() {
        return Set.of(ModelType.class);
    }
}
