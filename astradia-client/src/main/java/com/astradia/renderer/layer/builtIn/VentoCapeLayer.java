package com.astradia.renderer.layer.builtIn;

import com.astradia.renderer.gecko.ConfigurablePerBoneRender;
import com.astradia.renderer.layer.VentoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.BiConsumer;

public class VentoCapeLayer<T extends LivingEntity & GeoAnimatable, O, R extends AvatarRenderState> extends GeoRenderLayer<T, O, R> implements VentoRenderLayer<T, O, R> {
    private final HumanoidModel<AvatarRenderState> model;
    private final EquipmentAssetManager equipmentModelLoader;

    public VentoCapeLayer(GeoRenderer<T, O, R> renderer, EntityModelSet modelLoader, EquipmentAssetManager equipmentModelLoader) {
        super(renderer);
        this.model = new PlayerCapeModel(modelLoader.bakeLayer(ModelLayers.PLAYER_CAPE));
        this.equipmentModelLoader = equipmentModelLoader;
    }

    @Override
    public void addPerBoneCustomRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, ConfigurablePerBoneRender<R>> consumer) {
        renderPassInfo.model().getBone("Body").ifPresent(bone -> render(bone, consumer));
    }

    private boolean hasCustomModelForLayer(ItemStack stack, EquipmentClientInfo.LayerType layerType) {
        Equippable equippableComponent = (Equippable)stack.get(DataComponents.EQUIPPABLE);
        if (equippableComponent != null && equippableComponent.assetId().isPresent()) {
            EquipmentClientInfo equipmentModel = this.equipmentModelLoader.get(equippableComponent.assetId().get());
            return !equipmentModel.getLayers(layerType).isEmpty();
        } else {
            return false;
        }
    }

    private void render(GeoBone bone, BiConsumer<GeoBone, ConfigurablePerBoneRender<R>> consumer) {
        consumer.accept(bone, new ConfigurablePerBoneRender<>((renderPassInfo, bone1, renderTasks) -> {
            final R renderState = renderPassInfo.renderState();
            final PoseStack matrixStack = renderPassInfo.poseStack();
            if (!renderState.isInvisible && renderState.showCape) {
                PlayerSkin skinTextures = renderState.skin;
                if (skinTextures.cape() != null) {
                    if (!this.hasCustomModelForLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
                        matrixStack.pushPose();
                        matrixStack.scale(-1.0F, -1.0F, 1.0F);

                        matrixStack.translate(0.0F, -1.501F, 0.0F);

                        if (this.hasCustomModelForLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
                            matrixStack.translate(0.0F, -0.053125F, 0.06875F);
                        }

                        if(renderState.isCrouching) {
                            matrixStack.translate(0.0F, -(2F) * 0.0625F, 0.0F);
                        }
                        renderTasks.submitModel(this.model, renderState, matrixStack, RenderTypes.entitySolid(skinTextures.cape().texturePath()), renderPassInfo.packedLight(), OverlayTexture.NO_OVERLAY, renderState.outlineColor, null);
                        matrixStack.popPose();
                    }
                }
            }
        }, ConfigurablePerBoneRender.BoneTransformMode.NO_SCALE));
    }
}
