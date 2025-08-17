package com.astradia.render.layer;

import com.astradia.AstradiaClient;
import com.astradia.player.EquipmentSlot;
import com.astradia.utils.AstradiaPlayerEntityRenderState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;

import java.util.UUID;

public class CosmeticLayer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private final PlayerEntityRenderer renderer;

    public CosmeticLayer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
        renderer = (PlayerEntityRenderer) context;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        UUID uuid = ((AstradiaPlayerEntityRenderState) state).getUuid();
        float partialTick = ((AstradiaPlayerEntityRenderState) state).getPartialTick();
        var cosmetics = AstradiaClient.getPlayerManager().getFromUuid(uuid).getCosmetics();
        var equipment = cosmetics.getEquippedInventory();
        for (EquipmentSlot slot : equipment) {
            if(slot.getCosmetic() == null) continue;
            renderCosmetic(slot, matrices, vertexConsumers, light, state, limbAngle, limbDistance, partialTick);

        }
    }

    private void renderCosmetic(EquipmentSlot slot, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance, float partialTick) {
        var renderer = slot.getCosmetic().getRenderer();
        var model = renderer.getGeoModel().getBakedModel(renderer.getGeoModel().getModelResource(renderer.getAnimatable(), renderer));
        int color = Colors.WHITE;
        if(slot.getCosmetic().isColorable()) {
            color = slot.getStoredData().getInt("color");
            if(color == 0) color = -1;
        }
        renderer.actuallyRenderCosmetic(this.renderer, matrices, renderer.getAnimatable(), model, RenderLayer.getEntityTranslucent(slot.getCosmetic().getTexturePath()), vertexConsumers, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(slot.getCosmetic().getTexturePath())), false, partialTick, light, OverlayTexture.DEFAULT_UV, color);
    }
}
