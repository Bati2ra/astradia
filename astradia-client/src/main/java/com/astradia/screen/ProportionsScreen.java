package com.astradia.screen;

import com.astradia.api.player.BodyPartProportion;
import com.astradia.api.player.BodyProportionsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class ProportionsScreen extends Screen {
    private final BodyProportionsConfig config;
    private final PlayerEntity previewPlayer;

    public ProportionsScreen(BodyProportionsConfig config, MinecraftClient client) {
        super(Text.of("Body Proportions Editor"));
        this.config = config;
        this.previewPlayer = client.player; // o un clon si querés evitar side effects
    }

    @Override
    protected void init() {
        int y = 20;
        int spacing = 14;

        for (var entry : config.getAllParts().entrySet()) {
            String partName = entry.getKey();
            BodyPartProportion part = entry.getValue();

            EnumSet<BodyPartProportion.Axis> handled = EnumSet.noneOf(BodyPartProportion.Axis.class);

            for (BodyPartProportion.Axis axis : part.getRanges().keySet()) {
                if (handled.contains(axis)) continue;

                EnumSet<BodyPartProportion.Axis> linked = part.getLinkedAxes();
                boolean isLinkedGroup = linked.contains(axis);

                EnumSet<BodyPartProportion.Axis> group = isLinkedGroup ? linked : EnumSet.of(axis);
                BodyPartProportion.Range range = part.getRanges().get(axis);

                if (range == null) continue; // eje no usado

                this.addDrawableChild(new ProportionSlider(
                        20, y, 150, 10,
                        part, axis, range,
                        Text.of(partName + " " + group.stream()
                                .map(Enum::name)
                                .sorted()
                                .collect(Collectors.joining(",")))
                ));

                handled.addAll(group);
                y += spacing;
            }
        }
        this.addDrawableChild(ButtonWidget.builder(
                Text.of("Reset"),
                button -> {
                    for (BodyPartProportion part : config.getAllParts().values()) {
                        for (BodyPartProportion.Axis axis : part.getRanges().keySet()) {
                            part.setValue(axis, 1.0f);
                        }
                    }
                    for (Element element : this.children()) {
                        if (element instanceof ProportionSlider slider) {
                            slider.updateMessage();
                        }
                    }
                }
        ).dimensions(20, y + 10, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        // Render preview del jugador
        int previewX = this.width - 250;
        int previewY = this.height / 2 - 130;
        int width = 200;
        int height = 250;
        float scale = 40f;
        int i = 50;
        int j = 50;
        InventoryScreen.drawEntity(context, previewX, previewY, previewX + width, previewY + height, 60, 0.0625F, mouseX, mouseY, MinecraftClient.getInstance().player);
    }
}
