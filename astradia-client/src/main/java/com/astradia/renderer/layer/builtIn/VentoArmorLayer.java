package com.astradia.renderer.layer.builtIn;

import com.astradia.renderer.util.VentoRenderUtil;
import com.astradia.renderer.layer.VentoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.*;

public class VentoArmorLayer<T extends LivingEntity & GeoAnimatable, O, R extends HumanoidRenderState, M extends HumanoidModel<R>, A extends HumanoidModel<R>> extends GeoRenderLayer<T, O, R> implements VentoRenderLayer<T, O, R> {
    private final ArmorModelSet<A> adultModelData;
    private final ArmorModelSet<A> babyModelData;
    private final EquipmentLayerRenderer equipmentRenderer;

    public VentoArmorLayer(GeoRenderer<T, O, R> renderer, ArmorModelSet<A> modelData, EquipmentLayerRenderer equipmentRenderer) {
        this(renderer, modelData, modelData, equipmentRenderer);
    }

    public VentoArmorLayer(GeoRenderer<T, O, R> renderer, ArmorModelSet<A> adultModelData, ArmorModelSet<A> babyModelData, EquipmentLayerRenderer equipmentRenderer) {
        super(renderer);
        this.adultModelData = adultModelData;
        this.babyModelData = babyModelData;
        this.equipmentRenderer = equipmentRenderer;
    }

    private static boolean shouldRender(Equippable equippable, EquipmentSlot equipmentSlot) {
        return equippable.assetId().isPresent() && equippable.slot() == equipmentSlot;
    }

    @Override
    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        submit(renderPassInfo, renderPassInfo.poseStack(), renderTasks, renderPassInfo.packedLight(),  renderPassInfo.renderState());
    }

    public void submit(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, R humanoidRenderState) {
        this.renderArmorPiece(renderPassInfo, poseStack, submitNodeCollector, humanoidRenderState.chestEquipment, EquipmentSlot.CHEST, i, humanoidRenderState);
        this.renderArmorPiece(renderPassInfo, poseStack, submitNodeCollector, humanoidRenderState.legsEquipment, EquipmentSlot.LEGS, i, humanoidRenderState);
        this.renderArmorPiece(renderPassInfo, poseStack, submitNodeCollector, humanoidRenderState.feetEquipment, EquipmentSlot.FEET, i, humanoidRenderState);
        this.renderArmorPiece(renderPassInfo, poseStack, submitNodeCollector, humanoidRenderState.headEquipment, EquipmentSlot.HEAD, i, humanoidRenderState);
    }

    private void renderArmorPiece(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack itemStack, EquipmentSlot equipmentSlot, int i, R humanoidRenderState) {
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable != null && shouldRender(equippable, equipmentSlot)) {
            A humanoidModel = this.getArmorModel(humanoidRenderState, equipmentSlot);
            EquipmentClientInfo.LayerType layerType = this.usesInnerModel(equipmentSlot) ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
            renderLayers(renderPassInfo, layerType, equippable.assetId().orElseThrow(), humanoidModel, humanoidRenderState, itemStack, poseStack, submitNodeCollector, i, null, humanoidRenderState.outlineColor, 1, equipmentSlot);
        }
    }

    public <S> void renderLayers(RenderPassInfo<R> renderPassInfo, EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> resourceKey, Model<? super S> model, S object, ItemStack itemStack, PoseStack poseStac2k, SubmitNodeCollector submitNodeCollector, int i, @Nullable Identifier identifier, int j, int k, EquipmentSlot slot) {
        List<EquipmentClientInfo.Layer> list = equipmentRenderer.equipmentAssets.get(resourceKey).getLayers(layerType);
        if (!list.isEmpty()) {
            int l = DyedItemColor.getOrDefault(itemStack, 0);
            boolean bl = itemStack.hasFoil();
            int m = k;
            Iterator<EquipmentClientInfo.Layer> var16 = list.iterator();

            while(true) {
                EquipmentClientInfo.Layer layer;
                int n;
                do {
                    if (!var16.hasNext()) {
                        ArmorTrim armorTrim = itemStack.get(DataComponents.TRIM);
                        if (armorTrim != null) {
                            TextureAtlasSprite textureAtlasSprite = equipmentRenderer.trimSpriteLookup.apply(new EquipmentLayerRenderer.TrimSpriteKey(armorTrim, layerType, resourceKey));
                            RenderType renderType = Sheets.armorTrimsSheet(armorTrim.pattern().value().decal());
                            submitNodeCollector.order(m++).submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, vertexConsumer) -> submitGeometry(renderPassInfo, model, -1, pose, vertexConsumer, textureAtlasSprite));
                        }
                        return;
                    }

                    layer = var16.next();
                    n = getColorForLayer(layer, l);
                } while(n == 0);

                Identifier identifier2 = layer.usePlayerTexture() && identifier != null ? identifier : equipmentRenderer.layerTextureLookup.apply(new EquipmentLayerRenderer.LayerTextureKey(layerType, layer));

                int finalN = n;
                submitNodeCollector.order(m++).submitCustomGeometry(renderPassInfo.poseStack(), RenderTypes.armorCutoutNoCull(identifier2), (pose, vertexConsumer) -> submitGeometry(renderPassInfo, model, finalN, pose, vertexConsumer));

                if (bl) {
                    submitNodeCollector.order(m++).submitCustomGeometry(renderPassInfo.poseStack(), RenderTypes.armorEntityGlint(), (pose, vertexConsumer) -> submitGeometry(renderPassInfo, model, finalN, pose, vertexConsumer));
                }

                bl = false;
            }
        }
    }

    private <S> void submitGeometry(RenderPassInfo<R> renderPassInfo, Model<? super S> model, int n, PoseStack.Pose pose, VertexConsumer vertexConsumer) {
        submitGeometry(renderPassInfo, model, n, pose, vertexConsumer, null);
    }

    private <S> void submitGeometry(RenderPassInfo<R> renderPassInfo, Model<? super S> model, int n, PoseStack.Pose pose, VertexConsumer vertexConsumer, @Nullable TextureAtlasSprite textureAtlasSprite) {
        renderPassInfo.renderPosed(() -> {
            PoseStack poseStack = renderPassInfo.poseStack();
            poseStack.pushPose();
            poseStack.last().set(pose);

            VertexConsumer vertexConsumer2 = textureAtlasSprite == null ? vertexConsumer : textureAtlasSprite.wrap(vertexConsumer);
            if(model instanceof HumanoidModel<? super S> humanoidModel)
                render(renderPassInfo, humanoidModel, model.root(), vertexConsumer2, OverlayTexture.NO_OVERLAY, n);

            poseStack.popPose();
        });
    }

    public <S> String getBoneNameForSegment(HumanoidModel<? super S> model, ModelPart modelPart) {
        if (modelPart == model.head)     return "Head";
        if (modelPart == model.body)     return "Body";
        if (modelPart == model.leftArm)  return "LeftArmScalePoint";
        if (modelPart == model.rightArm) return "RightArmScalePoint";
        if (modelPart == model.leftLeg)  return "LeftLeg";
        if (modelPart == model.rightLeg) return "RightLeg";
        return null;
    }

    public <S> void render(RenderPassInfo<R> renderPassInfo, HumanoidModel<? super S> model, ModelPart modelPart, VertexConsumer vertexConsumer, int j, int k) {
        if (modelPart.visible) {
            if (!modelPart.cubes.isEmpty() || !modelPart.children.isEmpty()) {
                final int packedLight = renderPassInfo.packedLight();
                final PoseStack poseStack = renderPassInfo.poseStack();

                poseStack.pushPose();
                renderPassInfo.model().getBone(getBoneNameForSegment(model, modelPart)).ifPresent(geoBone -> VentoRenderUtil.transformToBoneFixed(renderPassInfo.poseStack(), geoBone));
                poseStack.pushPose();
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                poseStack.translate(0.0F, -1.501F, 0.0F);

                modelPart.translateAndRotate(poseStack);

                if (!modelPart.skipDraw) {
                    modelPart.compile(poseStack.last(), vertexConsumer, packedLight, j, k);
                }
                poseStack.popPose();

                modelPart.translateAndRotate(poseStack);

                Iterator<ModelPart> var6 = modelPart.children.values().iterator();

                while (var6.hasNext()) {
                    ModelPart nextModelPart = var6.next();
                    render(renderPassInfo, model, nextModelPart, vertexConsumer, j, k);
                }

                poseStack.popPose();
            }
        }
    }

    private static int getColorForLayer(EquipmentClientInfo.Layer layer, int i) {
        Optional<EquipmentClientInfo.Dyeable> optional = layer.dyeable();
        if (optional.isPresent()) {
            int j = optional.get().colorWhenUndyed().map(ARGB::opaque).orElse(0);
            return i != 0 ? i : j;
        } else {
            return -1;
        }
    }

    private A getArmorModel(R humanoidRenderState, EquipmentSlot equipmentSlot) {
        return (humanoidRenderState.isBaby ? this.babyModelData : this.adultModelData).get(equipmentSlot);
    }

    private boolean usesInnerModel(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS;
    }
}
