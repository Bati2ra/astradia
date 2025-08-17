package com.astradia;

import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.UUID;
/*
public class ClientPlayerCosmetics {
    private final PlayerEntity player;

    protected ClientPlayerCosmetics(UUID uuid) {
        super(uuid);
        // Solución temporal
        assert MinecraftClient.getInstance().world != null;
        player = MinecraftClient.getInstance().world.getPlayerByUuid(uuid);
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    protected ClientEquipmentSlot addSlot(SlotType slotType) {
        return new ClientEquipmentSlot(slotType);
    }

    @Override
    public void fromNbt(NbtCompound tag) {
        for (String key : tag.getKeys()) {
            BodyPart bodyPart = BodyPart.valueOf(key);
            NbtList list = (NbtList) tag.get(key);
            System.out.println(key);
            for (NbtElement nbtElement : list) {
                NbtCompound element = (NbtCompound) nbtElement;
                var equipmentSlots = equipment.get(bodyPart);

                    int index = element.getByte("_");
                    var equipmentSlot = equipmentSlots.get(index);
                    equipmentSlot.getCachedCosmetic().setId(element.getInt("id"));
                    if (element.contains("data")) {
                        equipmentSlot.setStoredData(element.getCompound("data"));
                    }

            }
        }
    }

    @Override
    public NbtCompound toNbt() {
        return null;
    }
}*/
