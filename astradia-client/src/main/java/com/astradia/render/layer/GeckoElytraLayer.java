package com.astradia.render.layer;

import com.astradia.render.player.PlayerRenderer;
import com.astradia.utils.VentoRenderUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GeckoElytraLayer<T extends LivingEntity & GeoAnimatable, O, R extends PlayerEntityRenderState & GeoRenderState>  extends GeoRenderLayer<T, O, R> {
    private final ElytraEntityModel model;
    private final ElytraEntityModel babyModel;
    private final EquipmentRenderer equipmentRenderer;

    public GeckoElytraLayer(GeoRenderer<T, O, R> renderer, LoadedEntityModels loader, EquipmentRenderer equipmentRenderer) {
        super(renderer);
        this.model = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA));
        this.babyModel = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA_BABY));
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void render(R renderState, MatrixStack poseStack, BakedGeoModel bakedModel, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
        ItemStack itemStack = renderState.equippedChestStack;
        EquippableComponent equippableComponent = (EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent != null && !equippableComponent.assetId().isEmpty()) {
            MatrixStack newMatrixStack = new MatrixStack();
            GeoBone bone = ((PlayerRenderer) renderer).body;
            Identifier identifier = getTexture(renderState);
            ElytraEntityModel elytraEntityModel = renderState.baby ? this.babyModel : this.model;
            newMatrixStack.push();
            VentoRenderUtil.moveToTrackedVisualMatrix(bone, newMatrixStack, true);
            newMatrixStack.translate(0.0F, 0.0F, 0.125F);
            elytraEntityModel.setAngles(renderState);
            this.equipmentRenderer.render(EquipmentModel.LayerType.WINGS, (RegistryKey)equippableComponent.assetId().get(), elytraEntityModel, itemStack, newMatrixStack, bufferSource, packedLight, identifier);
            newMatrixStack.pop();
        }
    }

    @Nullable
    private static Identifier getTexture(BipedEntityRenderState state) {
        if (state instanceof PlayerEntityRenderState playerEntityRenderState) {
            SkinTextures skinTextures = playerEntityRenderState.skinTextures;
            if (skinTextures.elytraTexture() != null) {
                return skinTextures.elytraTexture();
            }

            if (skinTextures.capeTexture() != null && playerEntityRenderState.capeVisible) {
                return skinTextures.capeTexture();
            }
        }

        return null;
    }
}
