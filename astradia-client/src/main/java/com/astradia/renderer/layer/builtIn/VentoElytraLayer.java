package com.astradia.renderer.layer.builtIn;

import com.astradia.renderer.gecko.ConfigurablePerBoneRender;
import com.astradia.renderer.layer.VentoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.BiConsumer;

public class VentoElytraLayer<T extends LivingEntity & GeoAnimatable, O, R extends AvatarRenderState> extends GeoRenderLayer<T, O, R> implements VentoRenderLayer<T, O, R> {
    private final ElytraModel model;
    private final ElytraModel babyModel;
    private final EquipmentLayerRenderer equipmentRenderer;

    public VentoElytraLayer(GeoRenderer<T, O, R> renderer, EntityModelSet loader, EquipmentLayerRenderer equipmentRenderer) {
        super(renderer);
        this.model = new ElytraModel(loader.bakeLayer(ModelLayers.ELYTRA));
        this.babyModel = new ElytraModel(loader.bakeLayer(ModelLayers.ELYTRA_BABY));
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void addPerBoneCustomRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, ConfigurablePerBoneRender<R>> consumer) {
        renderPassInfo.model().getBone("Body").ifPresent(bone -> render(bone, consumer));
    }

    private void render(GeoBone bone, BiConsumer<GeoBone, ConfigurablePerBoneRender<R>> consumer) {
        consumer.accept(bone, new ConfigurablePerBoneRender<>((renderPassInfo, bone1, renderTasks) -> {
            final R bipedEntityRenderState = renderPassInfo.renderState();
            final PoseStack matrixStack = renderPassInfo.poseStack();

            ItemStack itemStack = bipedEntityRenderState.chestEquipment;
            Equippable equippableComponent = itemStack.get(DataComponents.EQUIPPABLE);
            if (equippableComponent != null && equippableComponent.assetId().isPresent()) {
                Identifier identifier = getTexture(bipedEntityRenderState);
                ElytraModel elytraEntityModel = bipedEntityRenderState.isBaby ? this.babyModel : this.model;
                matrixStack.pushPose();
                matrixStack.scale(-1.0F, -1.0F, 1.0F);

                matrixStack.translate(0.0F, -1.501F, 0.0F);
                if(bipedEntityRenderState.isCrouching) {
                    matrixStack.translate(0.0F, -(3F) * 0.0625F, 0.0F);
                }
                this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.WINGS, equippableComponent.assetId().get(), elytraEntityModel, bipedEntityRenderState, itemStack, matrixStack, renderTasks, renderPassInfo.packedLight(), identifier, bipedEntityRenderState.outlineColor, 0);
                matrixStack.popPose();
            }
        }, ConfigurablePerBoneRender.BoneTransformMode.FIXED));
    }

    private static @Nullable Identifier getTexture(HumanoidRenderState state) {
        if (state instanceof AvatarRenderState playerEntityRenderState) {
            PlayerSkin skinTextures = playerEntityRenderState.skin;
            if (skinTextures.elytra() != null) {
                return skinTextures.elytra().texturePath();
            }

            if (skinTextures.cape() != null && playerEntityRenderState.showCape) {
                return skinTextures.cape().texturePath();
            }
        }

        return null;
    }
}
