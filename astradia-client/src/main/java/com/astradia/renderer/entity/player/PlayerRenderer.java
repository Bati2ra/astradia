package com.astradia.renderer.entity.player;

import com.astradia.VentoClient;
import com.astradia.api.player.BodyProportionValues;
import com.astradia.model.entity.VentoPlayerModel;
import com.astradia.player.PlayerBodyProportions;
import com.astradia.renderer.gecko.ConfigurablePerBoneRender;
import com.astradia.renderer.gecko.RenderPassInfoAccessor;
import com.astradia.renderer.gecko.VentoDataTickets;
import com.astradia.renderer.layer.*;
import com.astradia.renderer.layer.builtIn.*;
import com.astradia.renderer.entity.state.VentoAvatarRenderState;
import com.astradia.screen.util.FakePlayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.lang.Math;
import java.util.*;
import java.util.function.Consumer;

public class PlayerRenderer<T extends AbstractClientPlayer & GeoAnimatable, R extends AvatarRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    public GeoBone head;
    public GeoBone body;
    public GeoBone rightArm;
    public GeoBone leftArm;
    public GeoBone rightLeg;
    public GeoBone leftLeg;

    public GeoBone rightHandItem;
    public GeoBone leftHandItem;

    public GeoBone root;

    public GeoBone rightArmPh, leftArmPh;

    private GeoBone rightArmSlim, leftArmSlim;
    private GeoBone rightArmClassic, leftArmClassic;
    private GeoBone rightArmLayerSlim, leftArmLayerSlim;
    private GeoBone rightArmLayerClassic, leftArmLayerClassic;

    private boolean thin;
    
    private GeoBone hat;
    private GeoBone jacket;
    private GeoBone rightSleeve;
    private GeoBone leftSleeve;
    private GeoBone rightPants;
    private GeoBone leftPants;

    public final AvatarRenderer<T> original;

    public PlayerRenderer(EntityRendererProvider.Context context, VentoPlayerModel<T> model, boolean thinArms,
                          AvatarRenderer<T> original) {
        super(context, model);
        this.original = original;
        thin = thinArms;

        withRenderLayers(context);
    }

    private void withRenderLayers(EntityRendererProvider.Context context) {
        withRenderLayer(new VentoHeadLayer<>(this, context.getModelSet(), context.getPlayerSkinRenderCache()));
        withRenderLayer(new VentoCosmeticLayer<>(this));
        withRenderLayer(new VentoArmorLayer(this, ArmorModelSet.bake(thin ? ModelLayers.PLAYER_SLIM_ARMOR : ModelLayers.PLAYER_ARMOR, context.getModelSet(), (root) -> new net.minecraft.client.model.player.PlayerModel(root, thin)), context.getEquipmentRenderer()));
        withRenderLayer(new VentoElytraLayer<>(this, context.getModelSet(), context.getEquipmentRenderer()));
        withRenderLayer(new VentoCapeLayer<>(this, context.getModelSet(), context.getEquipmentAssets()));
        withRenderLayer(new VentoPlayerHeldItemLayer<>(this));
    }

    @Override
    public Vec3 getRenderOffset(R playerEntityRenderState) {
        Vec3 vec3d = super.getRenderOffset(playerEntityRenderState);
        return playerEntityRenderState.isCrouching ? vec3d.add(0.0, (double)(playerEntityRenderState.scale * -2.0F) / 16.0, 0.0) : vec3d;
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        UUID uuid = ((VentoAvatarRenderState) renderPassInfo.renderState()).getUuid();
        PlayerBodyProportions playerProportions = VentoClient.getPlayerManager().getFromUuid(uuid).getProportions();
        BodyProportionValues config = playerProportions.getValues();

        float head = config.getParameterById("head.scale");
        float bodyWidth = config.getParameterById("torso.width");
        float bodyHeight = config.getParameterById("torso.height");
        float rightArmWidth = config.getParameterById("rightArm.width");
        float rightArmHeight = config.getParameterById("rightArm.length");
        float leftArmWidth = config.getParameterById("leftArm.width");
        float leftArmHeight = config.getParameterById("leftArm.length");
        float rightLegWidth = config.getParameterById("rightLeg.width");
        float rightLegHeight = config.getParameterById("rightLeg.length");
        float leftLegWidth = config.getParameterById("leftLeg.width");
        float leftLegHeight = config.getParameterById("leftLeg.length");
        float width = config.getParameterById("global.width");
        float height = config.getParameterById("global.height");

        var bodyVerticalOffset = (bodyHeight - 1.0f) * 12;
        float legDeltaY  = (Math.max(rightLegHeight, leftLegHeight)  - 1.0f) * 12;
        original.getModel().setupAnim(renderPassInfo.renderState());

        snapshots.ifPresent("Root", boneSnapshot -> {
            boneSnapshot.setTranslateY(bodyVerticalOffset + legDeltaY);
            boneSnapshot.setScale(width, height, width);
        });

        ifModelPartPresent("head", modelPart -> snapshots.ifPresent("Head", boneSnapshot -> {
            boneSnapshot.setScale(head, head, head);

            boneSnapshot.setTranslation(modelPart.x, -modelPart.y, modelPart.z);
            boneSnapshot.setRotation(-modelPart.xRot, -modelPart.yRot, modelPart.zRot);
        }));

        ifModelPartPresent("right_leg", modelPart -> snapshots.ifPresent("RightLeg", boneSnapshot -> {
            boneSnapshot.setScale(rightLegWidth, rightLegHeight, rightLegWidth);

            boneSnapshot.setTranslation(modelPart.x + 2.0F, 12.0F - modelPart.y - bodyVerticalOffset, modelPart.z);
            boneSnapshot.setRotation(-modelPart.xRot, -modelPart.yRot, modelPart.zRot);
        }));

        ifModelPartPresent("left_leg", modelPart -> snapshots.ifPresent("LeftLeg", boneSnapshot -> {
            boneSnapshot.setScale(leftLegWidth, leftLegHeight, leftLegWidth);
            boneSnapshot.setTranslation(modelPart.x - 2.0F, 12.0F - modelPart.y - bodyVerticalOffset, modelPart.z);
            boneSnapshot.setRotation(-modelPart.xRot, -modelPart.yRot, modelPart.zRot);
        }));

        ifModelPartPresent("right_arm", modelPart -> {
            snapshots.ifPresent("RightArmScalePoint", boneSnapshot -> boneSnapshot.setScale(rightArmWidth, rightArmHeight, rightArmWidth));
            snapshots.ifPresent("RightArm", boneSnapshot -> {
                boneSnapshot.setTranslation(modelPart.x + 5.0F + (bodyWidth - 1.0f) * -4, 2.0F - modelPart.y, modelPart.z);
                boneSnapshot.setRotation(-modelPart.xRot, -modelPart.yRot, modelPart.zRot);
            });
        });

        ifModelPartPresent("left_arm", modelPart -> {
            snapshots.ifPresent("LeftArmScalePoint", boneSnapshot -> boneSnapshot.setScale(leftArmWidth, leftArmHeight, leftArmWidth));
            snapshots.ifPresent("LeftArm", boneSnapshot -> {
                boneSnapshot.setTranslation(modelPart.x - 5.0F + (bodyWidth - 1.0f) * 4, 2.0F - modelPart.y, modelPart.z);
                boneSnapshot.setRotation(-modelPart.xRot, -modelPart.yRot, modelPart.zRot);
            });
        });

        ifModelPartPresent("body", modelPart -> snapshots.ifPresent("Body", boneSnapshot -> {
            boneSnapshot.setScale(bodyWidth, bodyHeight, bodyWidth);
            boneSnapshot.setTranslation(modelPart.x, -modelPart.y, modelPart.z);
            boneSnapshot.setRotation(-modelPart.xRot, -modelPart.yRot, modelPart.zRot);
        }));

        setupBoneVisibility(renderPassInfo, snapshots);
    }

    private void setupBoneVisibility(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        GeoRenderState renderState = renderPassInfo.renderState();
        FakePlayer.FakeProfile fakeProfile = renderState.getGeckolibData(VentoDataTickets.FAKE_PLAYER_PROFILE);
        PlayerModelType model = renderPassInfo.renderState().skin.model();

        if(fakeProfile != null) {
            model = fakeProfile.model();
        }
        var thin = model.equals(PlayerModelType.SLIM);
        snapshots.ifPresent("RightArmClassic", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(thin);
            boneSnapshot.skipRender(thin);
        });
        snapshots.ifPresent("RightArmSlim", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(!thin);
            boneSnapshot.skipRender(!thin);
        });
        snapshots.ifPresent("LeftArmClassic", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(thin);
            boneSnapshot.skipRender(thin);
        });
        snapshots.ifPresent("LeftArmSlim", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(!thin);
            boneSnapshot.skipRender(!thin);
        });

        snapshots.ifPresent("RightArmLayerClassic", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(thin);
            boneSnapshot.skipRender(thin);
        });
        snapshots.ifPresent("RightArmLayerSlim", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(!thin);
            boneSnapshot.skipRender(!thin);
        });
        snapshots.ifPresent("LeftArmLayerClassic", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(thin);
            boneSnapshot.skipRender(thin);
        });
        snapshots.ifPresent("LeftArmLayerSlim", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(!thin);
            boneSnapshot.skipRender(!thin);
        });

        snapshots.ifPresent("RightArmLayer", boneSnapshot -> boneSnapshot.skipChildrenRender(!renderPassInfo.renderState().showRightSleeve));
        snapshots.ifPresent("LeftArmLayer", boneSnapshot -> boneSnapshot.skipChildrenRender(!renderPassInfo.renderState().showLeftSleeve));

        snapshots.ifPresent("RightLegLayer", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(!renderPassInfo.renderState().showRightPants);
            boneSnapshot.skipRender(!renderPassInfo.renderState().showRightPants);
        });
        snapshots.ifPresent("LeftLegLayer", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(!renderPassInfo.renderState().showLeftPants);
            boneSnapshot.skipRender(!renderPassInfo.renderState().showLeftPants);
        });
        snapshots.ifPresent("BodyLayer", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(!renderPassInfo.renderState().showJacket);
            boneSnapshot.skipRender(!renderPassInfo.renderState().showJacket);
        });
        snapshots.ifPresent("HeadLayer", boneSnapshot -> {
            boneSnapshot.skipChildrenRender(!renderPassInfo.renderState().showHat);
            boneSnapshot.skipRender(!renderPassInfo.renderState().showHat);
        });

    }

    private void ifModelPartPresent(String name, Consumer<ModelPart> action) {
        getModelPart(name).ifPresent(action);
    }

    private Optional<ModelPart> getModelPart(String name) {
        return Optional.ofNullable(original.getModel().getChildPart(name));
    }

    private void retrievePlayerBones() {
        if(root != null) return;
        root = model.getBakedModel(model.getModelResource(null)).getBone("Root").get();
        VentoClient.LOGGER.info("[PlayerRenderer] Retrieving bones from player model");
    }

    @Override
    public R createRenderState(T animatable, @org.jspecify.annotations.Nullable Void relatedObject) {
        return (R) new AvatarRenderState();
    }

    @Override
    public void extractRenderState(T entity, R entityRenderState, float partialTick) {
        original.extractRenderState(entity, entityRenderState, partialTick);
        if(entity instanceof FakePlayer fakePlayer) {
            entityRenderState.addGeckolibData(VentoDataTickets.IS_FAKE_PLAYER, true);
            entityRenderState.addGeckolibData(VentoDataTickets.FAKE_PLAYER_PROFILE, fakePlayer.getProfile());
        }
        this.fillRenderState(entity, null, entityRenderState, partialTick);
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        super.scaleModelForRender(renderPassInfo, widthScale * 0.9375F, heightScale * 0.9375F);
    }

    @Override
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        retrievePlayerBones();
        super.preRenderPass(renderPassInfo, renderTasks);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        final R renderState = renderPassInfo.renderState();
        final LivingEntityRenderState livingRenderState = renderState instanceof LivingEntityRenderState state ? state : null;
        final PoseStack poseStack = renderPassInfo.poseStack();

        if (livingRenderState != null && renderState.getGeckolibData(DataTickets.ENTITY_POSE) == Pose.SLEEPING) {
            Direction bedDirection = livingRenderState.bedOrientation;

            if (bedDirection != null) {
                float eyePosOffset = livingRenderState.eyeHeight - 0.1F;

                poseStack.translate(-bedDirection.getStepX() * eyePosOffset, 0, -bedDirection.getStepZ() * eyePosOffset);
            }
        }

        applyRotations(renderPassInfo, poseStack, livingRenderState != null ? livingRenderState.scale : 1);
        poseStack.translate(0, 0.01f, 0);
        //original.getModel().setupAnim(renderPassInfo.renderState());
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        final R renderState = renderPassInfo.renderState();
        final PoseStack matrixStack = renderPassInfo.poseStack();

        float h = renderState.swimAmount;
        float i = renderState.xRot;
        float j;
        if (renderState.isFallFlying) {
            super.applyRotations(renderPassInfo, poseStack, nativeScale);
            j = renderState.fallFlyingScale();
            if (!renderState.isAutoSpinAttack) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(j * (-90.0F - i)));
            }

            if (renderState.shouldApplyFlyingYRot) {
                matrixStack.mulPose(Axis.YP.rotation(renderState.flyingYRot));
            }
        } else if (h > 0.0F) {
            super.applyRotations(renderPassInfo, poseStack, nativeScale);
            j = renderState.isInWater ? -90.0F - i : -90.0F;
            float k = Mth.lerp(h, 0.0F, j);
            matrixStack.mulPose(Axis.XP.rotationDegrees(k));
            if (renderState.isVisuallySwimming) {
                matrixStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.applyRotations(renderPassInfo, poseStack, nativeScale);
        }
    }

    @Override
    public boolean firePreRenderEvent(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        return super.firePreRenderEvent(renderPassInfo, renderTasks);
    }

    @Override
    public void fireCompileRenderStateEvent(T animatable, @org.jspecify.annotations.Nullable Void relatedObject, R renderState, float partialTick) {
        super.fireCompileRenderStateEvent(animatable, relatedObject, renderState, partialTick);
    }

    @Override
    public void postRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.postRenderPass(renderPassInfo, renderTasks);


    }

    @Override
    public void performRenderPass(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState, RenderPassInfo.@Nullable BoneUpdater<R> boneUpdater) {
        poseStack.pushPose();

        final RenderType renderType = getRenderType(renderState, getTextureLocation(renderState));
        final RenderPassInfo<R> renderPassInfo = RenderPassInfo.create(this, renderState, poseStack, cameraState, renderType != null);
        boolean spectator = renderState.isSpectator;

        if (boneUpdater != null)
            renderPassInfo.addBoneUpdater(boneUpdater);

        if (firePreRenderEvent(renderPassInfo, renderTasks)) {
            preRenderPass(renderPassInfo, renderTasks);
            scaleModelForRender(renderPassInfo, 1, 1);
            adjustRenderPose(renderPassInfo);
            if(!spectator)
                preApplyRenderLayers(renderPassInfo, renderTasks);
            renderPassInfo.captureModelRenderPose();
            submitRenderTasks(renderPassInfo, renderTasks, renderType);
            submitPerBoneCustomRenderTasks(renderPassInfo, renderTasks);
            submitPerBoneRenderTasks(renderPassInfo, renderTasks);
            if(!spectator) {
                applyRenderLayers(renderPassInfo, renderTasks);
            }
        }

        poseStack.popPose();

        postRenderPass(renderPassInfo, renderTasks);
    }

    @Override
    public void preApplyRenderLayers(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        for (GeoRenderLayer<T, Void, R> renderLayer : getRenderLayers()) {
            renderLayer.preRender(renderPassInfo, renderTasks);
            renderLayer.addPerBoneRender(renderPassInfo, renderPassInfo::addPerBoneRender);
            if(renderLayer instanceof VentoRenderLayer) {
                VentoRenderLayer<T, Void, R> ventoRenderLayer = (VentoRenderLayer<T, Void, R>) renderLayer;
                ventoRenderLayer.addPerBoneCustomRender(renderPassInfo, ((RenderPassInfoAccessor<R>)renderPassInfo)::addPerBoneCustomRender);
            }
        }
    }

    private void submitPerBoneCustomRenderTasks(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        final Map<GeoBone, List<ConfigurablePerBoneRender<R>>> perBoneRenders = ((RenderPassInfoAccessor<R>)renderPassInfo).getBoneCustomRenderTasks();

        if (perBoneRenders.isEmpty())
            return;

        renderPassInfo.renderPosed(() -> {
            final Matrix4f pose = renderPassInfo.getModelRenderMatrixState();
            final PoseStack poseStack = renderPassInfo.poseStack();

            poseStack.pushPose();
            poseStack.last().pose().set(pose);

            for (Map.Entry<GeoBone, List<ConfigurablePerBoneRender<R>>> boneTasks : perBoneRenders.entrySet()) {
                final GeoBone bone = boneTasks.getKey();

                poseStack.pushPose();
                RenderUtil.transformToBone(poseStack, bone);

                // Capturar la transformación completa (currentPose + bone transform)
                Matrix4f boneTransform = new Matrix4f(poseStack.last().pose());

                // Revertir para no afectar el loop
                poseStack.popPose();

                poseStack.pushPose();

                for (ConfigurablePerBoneRender<R> renderOp : boneTasks.getValue()) {
                    poseStack.pushPose();

                    boolean hasCustomTransform = renderOp.applyTransform(poseStack, bone);

                    if (!hasCustomTransform) {
                        // Aplicar la transformación completa pre-calculada
                        poseStack.last().pose().set(boneTransform);
                    }

                    renderOp.submitRenderTask(renderPassInfo, bone, renderTasks);
                    poseStack.popPose();
                }

                poseStack.popPose();
            }

            poseStack.popPose();
        });
    }
}
