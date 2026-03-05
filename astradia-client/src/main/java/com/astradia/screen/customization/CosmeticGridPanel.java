package com.astradia.screen.customization;

import com.astradia.network.payloads.PlayerCosmeticEquipPayload;
import com.astradia.pojo.ClientCosmeticDefinition;
import net.bati.miniui.layout.EdgeInsets;
import net.bati.miniui.layout.LayoutConstraints;
import net.bati.miniui.layout.SizeConstraint;
import net.bati.miniui.rendering.Background;
import net.bati.miniui.widget.VirtualScrollGrid;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.function.Supplier;

public class CosmeticGridPanel extends VirtualScrollGrid<CosmeticCardWidget, ClientCosmeticDefinition> {
    private final CustomizationViewModel viewModel;

    private CosmeticGridPanel(CustomizationViewModel viewModel,
                              String id, int columns,
                              Supplier<CosmeticCardWidget> factory,
                              ItemBinder<CosmeticCardWidget, ClientCosmeticDefinition> binder) {
        super(id, columns, factory, binder);
        this.viewModel = viewModel;
    }

    public static CosmeticGridPanel build(CustomizationViewModel viewModel) {
        CosmeticGridPanel grid = new CosmeticGridPanel(
                viewModel,
                "content-grid",
                3,
                CosmeticGridPanel::cardFactory,
                (card, item, index) -> {
                    card.getPreviewPanel().getSelectButton().clearClickHandlers();
                    card.updateCard(item);
                    card.getPreviewPanel().getSelectButton().onClick(() -> onCosmeticClicked(item));
                }
        );
        grid.setBackground(Background.color(0xFF0F0F1A));
        grid.columnGap(6)
                .rowGap(6)
                .cellAspectRatio(0.8f)
                .bufferRows(2)
                .setPadding(EdgeInsets.all(8));

        grid.setFlex(grid.getFlexConstraints().withFlexGrow(1f));
        return grid;
    }

    private static CosmeticCardWidget cardFactory() {
        CosmeticCardWidget card = new CosmeticCardWidget("_cell");
        // Width fills the grid column; height is measured naturally (square preview + label)
        card.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fillParent()));
        return card;
    }

    private static void onCosmeticClicked(ClientCosmeticDefinition item) {
        System.out.println("Selected: " + item.getName() + " [" + item.getCategoryId() + "]");
        ClientPlayNetworking.send(new PlayerCosmeticEquipPayload(item.getId(), null));
    }

}