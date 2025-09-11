package com.astradia.render.layer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Arm;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GeckoArmorLayer<T extends LivingEntity & GeoAnimatable, O,
        R extends PlayerEntityRenderState & GeoRenderState,
        A extends BipedEntityModel<R>> extends GeoRenderLayer<T, O, R> {

    private final A innerModel;
    private final A outerModel;
    private final A babyInnerModel;
    private final A babyOuterModel;
    private final EquipmentRenderer equipmentRenderer;

    public GeckoArmorLayer(GeoRenderer<T, O, R> renderer, A innerModel, A outerModel, EquipmentRenderer equipmentRenderer) {
        this(renderer, innerModel, outerModel, innerModel, outerModel, equipmentRenderer);
    }

    public GeckoArmorLayer(GeoRenderer<T, O, R> renderer, A innerModel, A outerModel, A babyInnerModel, A babyOuterModel, EquipmentRenderer equipmentRenderer) {
        super(renderer);
        this.innerModel = innerModel;
        this.outerModel = outerModel;
        this.babyInnerModel = babyInnerModel;
        this.babyOuterModel = babyOuterModel;
        this.equipmentRenderer = equipmentRenderer;
    }

    public static boolean hasModel(ItemStack stack, EquipmentSlot slot) {
        EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
        return equippableComponent != null && hasModel(equippableComponent, slot);
    }

    private static boolean hasModel(EquippableComponent component, EquipmentSlot slot) {
        return component.assetId().isPresent() && component.slot() == slot;
    }

    @Override
    public void render(R renderState, MatrixStack poseStack, BakedGeoModel bakedModel, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
        this.renderArmor(poseStack, bufferSource, renderState.equippedChestStack, EquipmentSlot.CHEST, packedLight, this.getModel(renderState, EquipmentSlot.CHEST));
        this.renderArmor(poseStack, bufferSource, renderState.equippedLegsStack, EquipmentSlot.LEGS, packedLight, this.getModel(renderState, EquipmentSlot.LEGS));
        this.renderArmor(poseStack, bufferSource, renderState.equippedFeetStack, EquipmentSlot.FEET, packedLight, this.getModel(renderState, EquipmentSlot.FEET));
        this.renderArmor(poseStack, bufferSource, renderState.equippedHeadStack, EquipmentSlot.HEAD, packedLight, this.getModel(renderState, EquipmentSlot.HEAD));

    }

    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, EquipmentSlot slot, int light, A armorModel) {
        EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent != null && hasModel(equippableComponent, slot)) {
            //((BipedEntityModel)this.getContextModel()).copyTransforms(armorModel);
            this.setVisible(armorModel, slot);
            EquipmentModel.LayerType layerType = this.usesInnerModel(slot) ? EquipmentModel.LayerType.HUMANOID_LEGGINGS : EquipmentModel.LayerType.HUMANOID;
            this.equipmentRenderer.render(layerType, equippableComponent.assetId().orElseThrow(), armorModel, stack, matrices, vertexConsumers, light);
        }
    }

    protected void setVisible(A bipedModel, EquipmentSlot slot) {
        bipedModel.setVisible(false);
        switch (slot) {
            case HEAD:
                bipedModel.head.visible = true;
                bipedModel.hat.visible = true;
                break;
            case CHEST:
                bipedModel.body.visible = true;
                bipedModel.rightArm.visible = true;
                bipedModel.leftArm.visible = true;
                break;
            case LEGS:
                bipedModel.body.visible = true;
                bipedModel.rightLeg.visible = true;
                bipedModel.leftLeg.visible = true;
                break;
            case FEET:
                bipedModel.rightLeg.visible = true;
                bipedModel.leftLeg.visible = true;
        }

    }

    private A getModel(R state, EquipmentSlot slot) {
        if (this.usesInnerModel(slot)) {
            return state.baby ? this.babyInnerModel : this.innerModel;
        } else {
            return state.baby ? this.babyOuterModel : this.outerModel;
        }
    }

    private boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }
}
