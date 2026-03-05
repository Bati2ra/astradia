package com.astradia.screen.customization;

import net.bati.miniui.layout.EdgeInsets;
import net.bati.miniui.layout.LayoutConstraints;
import net.bati.miniui.layout.SizeConstraint;
import net.bati.miniui.layout.flex.FlexLayout;
import net.bati.miniui.rendering.Background;
import net.bati.miniui.widget.Button;
import net.bati.miniui.widget.FlexContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class CustomizationSidebar extends FlexContainer {

    public CustomizationSidebar(CustomizationViewModel viewModel) {
        super("sidebar");

        direction(FlexLayout.FlexDirection.COLUMN);
        alignItems(FlexLayout.AlignItems.STRETCH);
        justifyContent(FlexLayout.JustifyContent.FLEX_START);
        gap(4);
        setBackground(Background.color(0xFF111827));
        setPadding(EdgeInsets.all(6));
        setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fixed(40)).withHeight(SizeConstraint.fillParent()));

        var categories = viewModel.getCategories();

        for (Identifier categoryId : categories) {
            Button btn = new Button("category-" + categoryId,
                    Component.literal(categoryId.toString()))
                    .setColors(0xFF2A2A4A, 0xFF3A3A6A)
                    .useVanillaStyle(false);
            btn.setConstraints(LayoutConstraints.DEFAULT
                            .withWidth(SizeConstraint.fillParent())
                            .withAspectRatio(1)
                           );
            btn.onClick(() -> viewModel.setSelectedCategory(categoryId));
            add(btn);
        }
    }
}