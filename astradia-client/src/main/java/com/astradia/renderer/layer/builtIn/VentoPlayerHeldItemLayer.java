package com.astradia.renderer.layer.builtIn;

import com.astradia.renderer.gecko.ConfigurablePerBoneRender;
import com.astradia.renderer.layer.VentoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.*;
import software.bernie.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;

import java.util.EnumMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class VentoPlayerHeldItemLayer<T extends LivingEntity & GeoAnimatable, O, R extends ArmedEntityRenderState> extends BlockAndItemGeoLayer<T, O, R> implements VentoRenderLayer<T, O, R> {
    protected final String rightHandBone;
    protected final String leftHandBone;

    public VentoPlayerHeldItemLayer(GeoRenderer<T, O, R> renderer) {
        this(renderer, "RightHandItem", "LeftHandItem");
    }

    public VentoPlayerHeldItemLayer(GeoRenderer<T, O, R> renderer, String rightHandBoneName, String leftHandBoneName) {
        super(renderer);

        this.rightHandBone = rightHandBoneName;
        this.leftHandBone = leftHandBoneName;
    }

    /**
     * Return a list of the bone names that this layer will render for.
     * <p>
     * Ideally, you would cache this list in a class-field if you don't need any data from the input renderState or model
     */
    @Override
    protected List<RenderData<R>> getRelevantBones(R renderState, BakedGeoModel model) {
        boolean isLeftHanded = renderState.getOrDefaultGeckolibData(DataTickets.IS_LEFT_HANDED, false);

        return List.of(
                renderDataForHand(this.rightHandBone, HumanoidArm.RIGHT, isLeftHanded, renderState),
                renderDataForHand(this.leftHandBone, HumanoidArm.LEFT, isLeftHanded, renderState));
    }

    /**
     * Helper method for creating {@link RenderData} for a given hand
     */
    protected static <R extends GeoRenderState> RenderData<R> renderDataForHand(String boneName, R renderState) {
        return renderDataForHand(boneName, HumanoidArm.RIGHT, false, renderState);
    }

    /**
     * Helper method for creating {@link RenderData} for a given hand
     */
    protected static <R extends GeoRenderState> RenderData<R> renderDataForHand(String boneName, HumanoidArm arm, boolean isLeftHanded, R renderState) {
        HumanoidArm mainHandArm = isLeftHanded ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        EquipmentSlot slot = arm == mainHandArm ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

        ItemDisplayContext context = switch (slot) {
            case MAINHAND -> mainHandArm == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            case OFFHAND -> mainHandArm == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            default -> ItemDisplayContext.NONE;
        };

        return new RenderData<>(boneName, context, (bone, renderState2) -> Either.left((ItemStack)renderState2.getGeckolibData(DataTickets.EQUIPMENT_BY_SLOT).get(slot)));
    }

    /**
     * Override to add any custom {@link DataTicket}s you need to capture for rendering.
     * <p>
     * The animatable is discarded from the rendering context after this, so any data needed
     * for rendering should be captured in the renderState provided
     *
     * @param animatable The animatable instance being rendered
     * @param relatedObject An object related to the render pass or null if not applicable.
     *                         (E.G., ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
     * @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
     * @param partialTick The fraction of a tick that has elapsed as of the current render pass
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addRenderData(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {
        EnumMap<EquipmentSlot, ItemStack> equipment = renderState.getOrDefaultGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, (Supplier<EnumMap>)() -> new EnumMap<>(EquipmentSlot.class));

        //noinspection DataFlowIssue
        equipment.put(EquipmentSlot.MAINHAND, animatable.getMainHandItem());
        equipment.put(EquipmentSlot.OFFHAND, animatable.getOffhandItem());

        renderState.addGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, equipment);
        renderState.addGeckolibData(DataTickets.IS_LEFT_HANDED, animatable.getMainArm() == HumanoidArm.LEFT);
    }

    @Override
    public void addPerBoneRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {}

    @Override
    public void addPerBoneCustomRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, ConfigurablePerBoneRender<R>> consumer) {
        if (!renderPassInfo.willRender())
            return;

        final R renderState = renderPassInfo.renderState();
        final BakedGeoModel model = renderPassInfo.model();

        for (RenderData<R> renderData : getRelevantBones(renderState, model)) {
            model.getBone(renderData.boneName())
                    .ifPresentOrElse(bone -> createPerBoneRender(bone, renderData, consumer, renderState),
                            () -> GeckoLibConstants.LOGGER.error("Unable to find bone for ItemArmorGeoLayer: {}, skipping", renderData.boneName()));
        }

    }

    private void createPerBoneRender(GeoBone bone, BlockAndItemGeoLayer.RenderData<R> renderData, BiConsumer<GeoBone, ConfigurablePerBoneRender<R>> consumer, R renderState) {
        Either<ItemStack, BlockState> renderObject = renderData.retrievalFunction().apply(bone, renderState);

        renderObject.ifLeft(stack -> {
            if (!stack.isEmpty()) {
                consumer.accept(bone, new ConfigurablePerBoneRender<>((renderPassInfo, bone1, renderTasks) -> {
                    //RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);
                    var poseStack = renderPassInfo.poseStack();
                    HumanoidArm arm = renderData.displayContext() == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
                    poseStack.pushPose();
                    var parent = bone1.parent().frameSnapshot;
                    float scaleY = parent != null ? parent.getScaleY() : 1.0f;
                    float scaleX = parent != null ? parent.getScaleX() : 1.0F;
                    float baseY = 13 * 0.0625f;

// La longitud del brazo desde el pivote hasta donde queremos el item
// asumiendo que la longitud original del brazo es "armLength"
// Necesitas conocer la longitud del brazo en unidades de modelo

// Por ejemplo, si el brazo mide 12 unidades desde el hombro hasta la mano:
                    float armLength = 12 * 0.0625f; // Ajusta esto según tu modelo
                    float offsetX = 4 + 2 * scaleX;
// Offset ajustado = baseY + (armLength escalado - armLength normal)
                    float scaledOffset = baseY - armLength * (scaleY - 1.0f);
                    poseStack.translate((arm == HumanoidArm.RIGHT ? offsetX : -offsetX) * 0.0625f,  scaledOffset,0 );
                    submitItemStackRender(renderPassInfo.poseStack(), bone, stack, renderData.displayContext(), renderPassInfo.renderState(), renderTasks,
                            renderPassInfo.cameraState(), renderPassInfo.packedLight(), renderPassInfo.packedOverlay(), renderPassInfo.renderColor());
                    poseStack.popPose();
                }, ConfigurablePerBoneRender.BoneTransformMode.NO_SCALE));
            }
        }).ifRight(blockState -> {});
    }


    /**
     * Render the given {@link ItemStack} for the provided {@link GeoBone}.
     * TODO: Spyglass.
     */
    @Override
    protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStack stack, ItemDisplayContext displayContext, R renderState, SubmitNodeCollector renderTasks,
                                         CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
        HumanoidArm arm = displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND ? HumanoidArm.RIGHT : HumanoidArm.LEFT;

        poseStack.pushPose();

        if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poseStack.mulPose(Axis.XN.rotationDegrees(90f));
            poseStack.translate(0, 0.125f, -0.0625f);

            if (stack.getItem() instanceof ShieldItem)
                poseStack.translate(0, 0.125, -0.25);
        }
        else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
            poseStack.translate(0, 0.125f, -0.0625f);

            if (stack.getItem() instanceof ShieldItem) {
                poseStack.translate(0, 0.125, -0.25);
                //poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            }
        }

        if (renderState.attackTime > 0.0F && renderState.mainArm == arm && renderState.swingAnimationType == SwingAnimationType.STAB) {
            SpearAnimations.thirdPersonAttackItem(renderState, poseStack);
        }

        float f = renderState.ticksUsingItem(arm);
        if (f != 0.0F) {
            (arm == HumanoidArm.RIGHT ? renderState.rightArmPose : renderState.leftArmPose).animateUseItem(renderState, poseStack, f, arm, stack);
        }
        submitItemStackRender2(arm, poseStack, renderState, renderTasks, packedLight);
        poseStack.popPose();
    }

    protected void submitItemStackRender2(HumanoidArm arm, PoseStack poseStack, R renderState, SubmitNodeCollector renderTasks, int packedLight) {
        final ItemStackRenderState stackRenderState = arm == HumanoidArm.RIGHT ? renderState.rightHandItemState : renderState.leftHandItemState;

        // Not sure if "seed" is required for Gecko Animated Items to work, should check.
        // Replaced this with vanilla-way because heldContext was required.
        //mc.getItemModelManager().clearAndUpdate(stackRenderState, stack, displayContext, mc.world, null, (int)(long)renderState.getOrDefaultGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, 0L) + displayContext.ordinal());

        stackRenderState.submit(poseStack, renderTasks, packedLight, OverlayTexture.NO_OVERLAY, 0);
    }

}