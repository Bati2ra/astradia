package com.astradia.screen.customization;

import com.astradia.screen.customization.preview.CosmeticPreviewRenderer;
import net.bati.miniui.layout.Alignment;
import net.bati.miniui.layout.FlexConstraints;
import net.bati.miniui.layout.LayoutConstraints;
import net.bati.miniui.layout.SizeConstraint;
import net.bati.miniui.rendering.Background;
import net.bati.miniui.rendering.RenderPassInfo;
import net.bati.miniui.widget.Button;
import net.bati.miniui.widget.Panel;
import net.bati.miniui.widget.Separator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static com.astradia.screen.customization.CosmeticCardWidget.previewRenderer;

/**
 * The square region where the cosmetic is rendered.
 * Extends Widget directly so we can override renderContent.
 */
public class CosmeticPreviewPanel extends Panel {
    private final Button selectButton;

    private final CosmeticCardWidget cosmeticCard;
    public CosmeticPreviewPanel(CosmeticCardWidget parent) {
        super("preview");
        cosmeticCard = parent;
        setBackground(Background.color(0xFF2B2B2B));
        setFlex(FlexConstraints.DEFAULT.withFlexGrow(1));

        selectButton = new Button("preview-select", Component.literal(""));
        selectButton.setConstraints(LayoutConstraints.DEFAULT.withWidth(SizeConstraint.fillParent()).withHeight(SizeConstraint.fillParent()));
        selectButton.useVanillaStyle(false);
        selectButton.setColors(0,0);
        add(selectButton);
        add(new Separator("psep", true, 2).setColor(0xFF2D2D50).setConstraints(LayoutConstraints.DEFAULT.withAlignment(Alignment.BOTTOM_LEFT).withWidth(SizeConstraint.fillParent())));

    }

    public Button getSelectButton() {
        return selectButton;
    }

    @Override
    protected void renderContent(RenderPassInfo rp) {
        if (computedLayout == null) return;
        if (Minecraft.getInstance().level == null) return;
        if (cosmeticCard.cosmeticDefinition == null) return;
        if (cosmeticCard.fakePlayer == null) return;

        var strategy = CosmeticPreviewRenderer.getStrategyForCategory(
                cosmeticCard.cosmeticDefinition.getCategoryId());
        previewRenderer.setStrategy(strategy);
        previewRenderer.setPlayer(cosmeticCard.fakePlayer);
        previewRenderer.render(rp, computedLayout.getContentBounds());
    }
}