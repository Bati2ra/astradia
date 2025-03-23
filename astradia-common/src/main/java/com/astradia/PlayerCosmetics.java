package com.astradia;

import com.astradia.enums.BodyPart;
import com.astradia.enums.SlotType;
import com.astradia.player.EquipmentSlot;
import com.astradia.pojo.Cosmetic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.*;

public abstract class PlayerCosmetics<T extends EquipmentSlot> {
    protected final UUID uuid;
    protected final HashSet<Integer> unlockedCosmetics = new HashSet<>();
    protected final HashMap<BodyPart, List<T>> equipment;
    protected PlayerCosmetics(UUID uuid) {
        this.uuid = uuid;
        equipment = new HashMap<>();
        initializeEquipment();
    }

    protected abstract T addSlot(SlotType slotType);
    /**
     * This must be changed, it should be data-driven and shared from server to client.
     */
    private void initializeEquipment() {
        for (BodyPart value : BodyPart.values()) {
            var slots = new ArrayList<T>();
            for (SlotType slot : value.getSlots()) {
                slots.add(addSlot(slot));
            }
            equipment.put(value, slots);
        }
    }

    public abstract void fromNbt(NbtCompound tag);

    public abstract NbtCompound toNbt();

    public boolean isUnlocked(Integer id) {
        return unlockedCosmetics.contains(id);
    }

    public HashMap<BodyPart, List<T>> getEquipment() {
        return equipment;
    }

    public String showUnlockedCosmetics() {
        StringBuilder builder = new StringBuilder();
        for (Integer unlockedCosmetic : unlockedCosmetics) {
            builder.append(String.format("%s\n", unlockedCosmetic));
        }
        return builder.toString();
    }
}
