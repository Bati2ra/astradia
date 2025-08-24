package com.astradia.render.layer;

import com.astradia.AstradiaClient;
import com.astradia.impl.ModelType;
import com.astradia.player.ClientCosmeticSlot;
import com.astradia.pojo.ClientCosmeticInfo;
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
        if(!AstradiaClient.isEverythingReady()) return;

        UUID uuid = ((AstradiaPlayerEntityRenderState) state).getUuid();
        float partialTick = ((AstradiaPlayerEntityRenderState) state).getPartialTick();
        var cosmetics = AstradiaClient.getPlayerManager().getFromUuid(uuid).getCosmetics();
        var equipment = cosmetics.getEquippedInventory();
        for (ClientCosmeticSlot slot : equipment.values()) {
            var cosmeticData = slot.getCosmeticData();
            if(cosmeticData == null) continue;
            cosmeticData.getCosmetic().getProperty(ModelType.class).ifPresent(modelType -> renderCosmetic(slot, modelType, matrices, vertexConsumers, light, state, limbAngle, limbDistance, partialTick));
        }
    }

    private void renderCosmetic(ClientCosmeticSlot slot, ModelType modelType, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance, float partialTick) {
        var cosmeticData = slot.getCosmeticData();
        var cosmetic = (ClientCosmeticInfo) cosmeticData.getCosmetic();
        var renderer = cosmetic.getRenderer();
        try {
            var model = renderer.getGeoModel().getBakedModel(renderer.getGeoModel().getModelResource(renderer.getAnimatable(), renderer));
            int color = Colors.WHITE;
            /*if(cosmetic.isColorable()) {
                color = slot.getStoredData().getInt("color");
                if(color == 0) color = -1;
            }*/
            renderer.actuallyRenderCosmetic(this.renderer, matrices, renderer.getAnimatable(), model, RenderLayer.getEntityTranslucent(modelType.getTexturePath()), vertexConsumers, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(modelType.getTexturePath())), false, partialTick, light, OverlayTexture.DEFAULT_UV, color);

        } catch (Exception e) {
            if(e instanceof RuntimeException runtimeException) {
                AstradiaClient.LOGGER.error(runtimeException.getMessage());
            }
        }
    }
}
