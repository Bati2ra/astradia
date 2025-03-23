package com.astradia.pojo;

import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import net.minecraft.nbt.NbtCompound;

public class Cosmetic implements CosmeticBase {
    private final Integer id;
    private final String name;
    private final String modelPath;
    private final String texturePath;
    private final String animationPath;

    private final BodyPart bodyPart;
    private final SlotType slotType;
    private final boolean colorable;

    public Cosmetic(Integer id, String name, String modelPath, String texturePath, BodyPart bodyPart, SlotType slotType) {
        this.id = id;
        this.name = name;
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.bodyPart = bodyPart;
        this.slotType = slotType;
        this.colorable = false;
        animationPath = null;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModelPath() {
        return modelPath;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getAnimationPath() {
        return animationPath;
    }

    public BodyPart getBodyPart() {
        return bodyPart;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public boolean isColorable() {
        return colorable;
    }

    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("id", id);
        tag.putString("name", name);
        tag.putString("mp", modelPath);
        tag.putString("tp", texturePath);
        tag.putString("bodySlot", bodyPart.name());
        tag.putString("slot", slotType.name());
        tag.putBoolean("colorable", colorable);
        return tag;
    }
}
