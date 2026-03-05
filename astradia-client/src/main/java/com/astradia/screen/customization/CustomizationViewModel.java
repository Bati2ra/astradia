package com.astradia.screen.customization;

import com.astradia.ClientCosmeticStore;
import com.astradia.pojo.ClientCosmeticDefinition;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CustomizationViewModel {
    private static final boolean USE_FAKE_DATA = false;
    private static final int FAKE_AMOUNT = 200; //

    private Consumer<Identifier> onCategoryChanged;
    private Identifier selectedCategory;

    public CustomizationViewModel() {
    }

    public void setOnCategoryChanged(Consumer<Identifier> listener) {
        this.onCategoryChanged = listener;
    }

    public void setSelectedCategory(Identifier category) {
        this.selectedCategory = category;
        if (onCategoryChanged != null) {
            onCategoryChanged.accept(category);
        }
    }

    public Identifier getSelectedCategory() {
        return selectedCategory;
    }

    public Set<Identifier> getCategories() {
        return ClientCosmeticStore.INSTANCE.getCategories();
    }

    public Collection<ClientCosmeticDefinition> getUnlockedCosmetics() {
        if (!USE_FAKE_DATA) {
            return ClientCosmeticStore.INSTANCE.getAll().values();
        }

        Set<ClientCosmeticDefinition> fake = new java.util.HashSet<>();

        for (int i = 0; i < FAKE_AMOUNT; i++) {
            try {
                fake.add(createFakeCosmetic(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return fake;
    }

    public Collection<ClientCosmeticDefinition> getUnlockedCosmeticsBySlot(Identifier slot) {
        return getUnlockedCosmetics()
                .stream()
                .filter(definition -> definition.getCategoryId().equals(slot))
                .collect(Collectors.toSet());
    }

    private ClientCosmeticDefinition createFakeCosmetic(int index) throws Exception {

        com.google.gson.JsonObject json = new com.google.gson.JsonObject();

        json.addProperty("id", "astradia:fake_cosmetic_" + index);


        // Alternamos slots para probar filtros
        String slot = (index % 3 == 0)
                ? "head:hair"
                : (index % 3 == 1)
                ? "torso:accessory"
                : "head:hair";

        json.addProperty("slotId", slot);
        json.addProperty("name", slot.split(":")[1] + " " + index);
        // Properties vacío (no necesitamos nada para UI)
        json.add("properties", new com.google.gson.JsonArray());

        return new ClientCosmeticDefinition(json);
    }
}
