package com.astradia.render.layer;

import com.astradia.render.player.PlayerRenderer;
import com.astradia.utils.VentoRenderUtil;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GeckoCapeLayer<T extends LivingEntity & GeoAnimatable, O, R extends PlayerEntityRenderState & GeoRenderState>  extends GeoRenderLayer<T, O, R> {
    private final BipedEntityModel<PlayerEntityRenderState> model;
    private final EquipmentModelLoader equipmentModelLoader;

    public GeckoCapeLayer(GeoRenderer<T, O, R> renderer, LoadedEntityModels modelLoader, EquipmentModelLoader equipmentModelLoader) {
        super(renderer);
        this.model = new PlayerCapeModel<>(modelLoader.getModelPart(EntityModelLayers.PLAYER_CAPE));
        this.equipmentModelLoader = equipmentModelLoader;
    }

    private boolean hasCustomModelForLayer(ItemStack stack, EquipmentModel.LayerType layerType) {
        EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent != null && equippableComponent.assetId().isPresent()) {
            EquipmentModel equipmentModel = this.equipmentModelLoader.get(equippableComponent.assetId().get());
            return !equipmentModel.getLayers(layerType).isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public void render(R renderState, MatrixStack poseStack, BakedGeoModel bakedModel, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
        if (!renderState.invisible && renderState.capeVisible) {
            SkinTextures skinTextures = renderState.skinTextures;
            if (skinTextures.capeTexture() != null) {
                if (!this.hasCustomModelForLayer(renderState.equippedChestStack, EquipmentModel.LayerType.WINGS)) {
                    MatrixStack newMatrixStack = new MatrixStack();
                    GeoBone bone = ((PlayerRenderer) renderer).body;
                    VentoRenderUtil.moveToTrackedVisualMatrix(bone, newMatrixStack, true);
                    newMatrixStack.push();
                    if (this.hasCustomModelForLayer(renderState.equippedChestStack, EquipmentModel.LayerType.HUMANOID)) {
                        newMatrixStack.translate(0.0F, -0.053125F, 0.06875F);
                    }

                    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderLayer.getEntitySolid(skinTextures.capeTexture()));
                    this.model.setAngles(renderState);
                    this.model.render(newMatrixStack, vertexConsumer, packedLight, OverlayTexture.DEFAULT_UV);
                    newMatrixStack.pop();
                }
            }
        }
    }
}
