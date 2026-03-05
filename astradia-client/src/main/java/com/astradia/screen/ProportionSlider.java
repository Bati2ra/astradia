package com.astradia.screen;

import com.astradia.api.player.ScaleParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class ProportionSlider extends AbstractSliderButton {
    private final ScaleParameter target;
    private final float min;
    private final float max;
    private double lastValue;
    private Component label;
    public ProportionSlider(int x, int y, int width, int height,
                            ScaleParameter target,
                            Component label) {
        super(x, y, width, height, label, target.getValue());
        this.target = target;
        this.min = target.min;
        this.max = target.max;
        this.lastValue = this.value;
        this.label = label;
        updateMessage();
    }

    private double normalize(float value) {
        return (value - min) / (max - min);
    }

    private float denormalize(double value) {
        return (float) (min + value * (max - min));
    }

    @Override
    protected void updateMessage() {
        float actual = denormalize(this.value);
        this.setMessage(Component.nullToEmpty(String.format("%s %.2f", ".", actual)));
    }

    @Override
    protected void applyValue() {
        if (value != lastValue) {
            float actual = denormalize(value);
            target.setValue(actual);
            lastValue = value;
        }
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {

        super.renderWidget(context, mouseX, mouseY, deltaTicks);
// Dibujar el label arriba del slider
        int labelX = this.getX() + this.getWidth() / 2 + 50;
        int labelY = this.getY() - 10;

        context.drawCenteredString(
                Minecraft.getInstance().font,
                label,
                labelX,
                labelY,
                -1
        );
    }
}
