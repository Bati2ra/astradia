package com.astradia.screen;

import com.astradia.api.player.BodyPartProportion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ProportionSlider extends SliderWidget {
    private final BodyPartProportion target;
    private final BodyPartProportion.Axis axis;
    private final float min;
    private final float max;
    private double lastValue;
    private Text label;
    public ProportionSlider(int x, int y, int width, int height,
                            BodyPartProportion target,
                            BodyPartProportion.Axis axis,
                            BodyPartProportion.Range range,
                            Text label) {
        super(x, y, width, height, label, normalize(target.getValue(axis), range));
        this.target = target;
        this.axis = axis;
        this.min = range.min;
        this.max = range.max;
        this.lastValue = this.value;
        this.label = label;
        updateMessage();
    }

    private static double normalize(float value, BodyPartProportion.Range range) {
        return (value - range.min) / (range.max - range.min);
    }

    private float denormalize(double value) {
        return (float) (min + value * (max - min));
    }

    @Override
    protected void updateMessage() {
        float actual = denormalize(this.value);
        this.setMessage(Text.of(String.format("%s %.2f", axis.name(), actual)));
    }

    @Override
    protected void applyValue() {
        if (value != lastValue) {
            float actual = denormalize(value);
            target.setValue(axis, actual);
            lastValue = value;
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        super.renderWidget(context, mouseX, mouseY, deltaTicks);
// Dibujar el label arriba del slider
        int labelX = this.getX() + this.getWidth() / 2 + 50;
        int labelY = this.getY() - 10;

        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                label,
                labelX,
                labelY,
                -1
        );
    }
}
