package com.astradia.renderer.layer;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CosmeticLayer<T extends LivingEntity & GeoAnimatable, O, R extends AvatarRenderState & GeoRenderState>  extends GeoRenderLayer<T, O, R> {
    public CosmeticLayer(GeoRenderer<T, O, R> renderer) {
        super(renderer);
    }
   /* private final PlayerEntityRenderer originalRenderer;

    public CosmeticLayer(GeoRenderer<T, O, R> renderer, PlayerEntityRenderer originalRenderer) {
        super(renderer);
        this.originalRenderer = originalRenderer;
    }

    @Override
    public void render(R renderState, MatrixStack poseStack, BakedGeoModel bakedModel, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
        if(!AstradiaClient.isEverythingReady()) return;
        UUID uuid = ((AstradiaPlayerEntityRenderState) renderState).getUuid();
        float partialTick = ((AstradiaPlayerEntityRenderState) renderState).getPartialTick();
        var cosmetics = AstradiaClient.getPlayerManager().getFromUuid(uuid).getCosmetics();
        var equipment = cosmetics.getEquippedInventory();
        for (ClientCosmeticSlot slot : equipment.values()) {
            var cosmeticData = slot.getCosmeticData();
            if(cosmeticData == null) continue;
            cosmeticData.getCosmetic().getProperty(ModelProperty.class).ifPresent(modelType -> renderCosmetic(slot, modelType, poseStack, bufferSource, packedLight, renderState, partialTick));
        }
    }

    private void renderCosmetic(ClientCosmeticSlot slot, ModelProperty modelProperty, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float partialTick) {
        var cosmeticData = slot.getCosmeticData();
        var cosmetic = (ClientCosmeticInfo) cosmeticData.getCosmetic();
        var renderer = cosmetic.getRenderer();

        try {
            var model = renderer.getGeoModel().getBakedModel(renderer.getGeoModel().getModelResource(null));
            int color = Colors.WHITE;

            Identifier texture = cosmetic.getProperty(TextureProperty.class).get().getPath();
            var animatedTextureProperty = cosmetic.getProperty(AnimatedTextureProperty.class);
            if(animatedTextureProperty.isPresent()) {
                var animations = animatedTextureProperty.get().getAnimations();
                if(animations.containsKey(texture)) {

                }
            }
            RenderLayer layer = RenderLayer.getEntityCutout(texture);
          //  GeoRenderState renderState = renderer.fillRenderState(renderer.getAnimatable(), null, new GeoRenderState.Impl(), partialTick);
          //  renderState.addGeckolibData(DataTickets.PACKED_LIGHT, light);
            PlayerRenderer<?,?> playerRenderer = (PlayerRenderer<?, ?>) this.renderer;

            var colorableProperty = cosmetic.getProperty(ColorableProperty.class);
            if(colorableProperty.isPresent()) {
                color = cosmeticData.getTypeData(ColorableProperty.class, ColorableProperty.PlayerData.class).get().getColor();

            }
            renderer.renderReference = playerRenderer;


            renderer.render(matrices, cosmeticData,renderer.getAnimatable(), vertexConsumers, layer, vertexConsumers.getBuffer(layer), light, partialTick);
            //renderer.actuallyRenderCosmetic((GeoRenderState) state, this.originalRenderer, playerRenderer, matrices, renderer.getAnimatable(), model, layer, vertexConsumers, vertexConsumers.getBuffer(layer), false, partialTick, 15728640, OverlayTexture.DEFAULT_UV, color);

        } catch (Exception e) {
            if(e instanceof RuntimeException runtimeException) {
                AstradiaClient.LOGGER.error(runtimeException.getMessage());
            }
        }
    }*/
}
