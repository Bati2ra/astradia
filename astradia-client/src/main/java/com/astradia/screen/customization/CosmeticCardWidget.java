package com.astradia.screen.customization;

import com.astradia.VentoClient;
import com.astradia.player.ClientCosmeticSlot;
import com.astradia.player.PlayerCosmetics;
import com.astradia.player.PlayerData;
import com.astradia.pojo.ClientCosmeticDefinition;
import com.astradia.screen.customization.preview.CosmeticPreviewRenderer;
import com.astradia.screen.util.Constants;
import com.astradia.screen.util.FakePlayer;
import net.bati.miniui.layout.EdgeInsets;
import net.bati.miniui.layout.LayoutConstraints;
import net.bati.miniui.layout.SizeConstraint;
import net.bati.miniui.layout.flex.FlexLayout;
import net.bati.miniui.widget.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;

import java.util.UUID;

public class CosmeticCardWidget extends FlexContainer {
    public final FakePlayer fakePlayer;

    protected static CosmeticPreviewRenderer previewRenderer;
    protected ClientCosmeticDefinition cosmeticDefinition;

    private final CosmeticPreviewPanel previewPanel;
    private final Label nameLabel;

    public CosmeticCardWidget(String id) {
        super(id);
        direction(FlexLayout.FlexDirection.COLUMN);
        alignItems(FlexLayout.AlignItems.STRETCH);
        gap(3);

        previewPanel = new CosmeticPreviewPanel(this);
        add(previewPanel);

        nameLabel = new Label("name", Component.literal(""))
                .setTruncate(true)
                .setColor(0xFFDDDDDD);
        nameLabel.setPadding(EdgeInsets.vertical(2));
        nameLabel.setConstraints(LayoutConstraints.DEFAULT
                .withWidth(SizeConstraint.fillParent()));
        add(nameLabel);

        fakePlayer = new FakePlayer(Minecraft.getInstance().level, UUID.randomUUID());
        fakePlayer.setProfile(new FakePlayer.FakeProfile(
                Constants.DUMMY_SKIN_PATH, PlayerModelType.SLIM));
        previewRenderer = new CosmeticPreviewRenderer(fakePlayer);
    }

    public CosmeticPreviewPanel getPreviewPanel() {
        return previewPanel;
    }

    public void updateCard(ClientCosmeticDefinition cosmeticDefinition) {
        this.cosmeticDefinition = cosmeticDefinition;
        nameLabel.setText(Component.literal(cosmeticDefinition.getName()));

        updatePlayerPreview(cosmeticDefinition);
    }

    private void updatePlayerPreview(ClientCosmeticDefinition cosmeticDefinition) {
        PlayerData data = VentoClient.getPlayerManager().getFromFakeUuid(fakePlayer.getUUID());
        PlayerCosmetics cosmetics = data.getCosmetics();
        cosmetics.clearAll();
        cosmetics.equip(cosmeticDefinition, null);
    }
}