package com.astradia.pojo;

import com.astradia.AstradiaClient;
import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import com.astradia.render.CosmeticRenderer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ClientCosmetic implements CosmeticBase {
    private final Integer id;
    private final String name;

    private final Identifier modelPath;
    private final Identifier texturePath;
    private final Identifier animationPath;

    private final CosmeticRenderer renderer;

    private final BodyPart bodyPart;
    private final SlotType slotType;
    private final boolean colorable;
    public ClientCosmetic(NbtCompound tag) {
        id = tag.getInt("id");
        name = tag.getString("name");
        this.modelPath = Identifier.of(AstradiaClient.MOD_ID, String.format("geo/%s.geo.json", !tag.contains("mp") ? id : tag.getString("mp")));
        this.texturePath = Identifier.of(AstradiaClient.MOD_ID, String.format("textures/%s.png", !tag.contains("tp") ? id : tag.getString("tp")));
        if(tag.contains("ap")) {
            animationPath = Identifier.of(AstradiaClient.MOD_ID, "animations/" + tag.getString("ap") + ".animation.json");
        } else {
            animationPath = null;
        }
        renderer = new CosmeticRenderer(this, new CosmeticAnimatable(this));
        bodyPart = BodyPart.valueOf(tag.getString("bodySlot"));
        slotType = SlotType.valueOf(tag.getString("slot"));
        colorable = tag.getBoolean("colorable");
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Identifier getModelPath() {
        return modelPath;
    }

    public Identifier getTexturePath() {
        return texturePath;
    }

    public Identifier getAnimationPath() {
        return animationPath;
    }

    public CosmeticRenderer getRenderer() {
        return renderer;
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
}
