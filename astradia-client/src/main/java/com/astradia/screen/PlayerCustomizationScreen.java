package com.astradia.screen;

import com.astradia.VentoClient;
import com.astradia.pojo.ClientCosmeticDefinition;
import com.astradia.screen.customization.CosmeticGridPanel;
import com.astradia.screen.customization.CustomizationSidebar;
import com.astradia.screen.customization.CustomizationViewModel;
import com.astradia.screen.customization.PlayerPreviewPanel;
import net.bati.miniui.layout.EdgeInsets;
import net.bati.miniui.layout.LayoutConstraints;
import net.bati.miniui.layout.SizeConstraint;
import net.bati.miniui.layout.flex.FlexLayout;
import net.bati.miniui.rendering.Background;
import net.bati.miniui.screen.ModernScreen;
import net.bati.miniui.widget.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerCustomizationScreen extends ModernScreen {
    private final CustomizationViewModel viewModel;

    private CosmeticGridPanel grid;
    private Label countLabel;

    public PlayerCustomizationScreen(@Nullable Component title) {
        super(Component.literal(""));
        viewModel = new CustomizationViewModel();
        viewModel.setOnCategoryChanged(this::applyFilter);

    }

    @Override
    protected void init() {
        super.init();
        setRoot(buildRoot());
    }

    private Widget buildRoot() {
        Panel root = new Panel("root");
        root.setConstraints(LayoutConstraints.fillParent());

        FlexContainer mainCol = FlexContainer.column("main_col").gap(0)
                .alignItems(FlexLayout.AlignItems.STRETCH);
        mainCol.setConstraints(LayoutConstraints.fillParent());

        mainCol.add(buildHeader())
                .add(Separator.horizontal("sep").setColor(0xFF2D2D50))
                .add(buildBody());

        root.addChild(mainCol);
        return root;
    }

    private Widget buildBody() {
        FlexContainer body = FlexContainer.row("body").gap(0)
                .alignItems(FlexLayout.AlignItems.STRETCH);

        body.setFlex(body.getFlexConstraints().withFlexGrow(1f));

        grid = CosmeticGridPanel.build(viewModel);
        viewModel.getCategories().stream().findFirst().ifPresent(viewModel::setSelectedCategory);

        body
                .add(new CustomizationSidebar(viewModel))
                .add(Separator.vertical("vsep").setColor(0xFF2D2D50))
                .add(grid)
                .add(new PlayerPreviewPanel(viewModel));
        return body;
    }

    private Widget buildHeader() {
        FlexContainer header = FlexContainer.row("header")
                .alignItems(FlexLayout.AlignItems.CENTER)
                .justifyContent(FlexLayout.JustifyContent.SPACE_BETWEEN)
                .gap(8);
        header.setBackground(Background.color(0xFF16213E));
        header.setPadding(EdgeInsets.symmetric(0, 10));
        header.setConstraints(LayoutConstraints.DEFAULT
                .withHeight(SizeConstraint.fixed(28)));

        Label title = new Label("title", Component.literal("§b§lCosmetics"))
                .setColor(0xFFCCCCCC);

        countLabel = new Label("count", Component.literal(""))
                .setColor(0xFF888888);

        Button close = new Button("close", Component.literal("§c✕"))
                .setColors(0xFF8B0000, 0xFFCC0000)
                .useVanillaStyle(false);
        close.setConstraints(LayoutConstraints.DEFAULT.withWidth(SizeConstraint.fixed(16)));
        close.onClick(() -> Minecraft.getInstance().setScreen(null));

        return header.add(title).add(Spacer.grow("sp")).add(countLabel).add(close);
    }

    private void applyFilter(Identifier category) {
        var items = viewModel.getUnlockedCosmetics();

        List<ClientCosmeticDefinition> filtered = items.stream()
                .filter(i -> i.getCategoryId().equals(category))
                .toList();

        grid.setItems(filtered);
        countLabel.setText(Component.literal("§7" + filtered.size() + " items"));
    }

    @Override
    public void onClose() {
        VentoClient.getPlayerManager().clearFakePlayers();
        super.onClose();
    }
}
