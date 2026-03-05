package com.astradia.renderer.layer.builtIn;

import com.astradia.VentoClient;
import com.astradia.api.CosmeticDefinition;
import com.astradia.api.player.PlayerCosmeticData;
import com.astradia.impl.ModelProperty;
import com.astradia.player.ClientCosmeticSlot;
import com.astradia.player.PlayerCosmetics;
import com.astradia.pojo.ClientCosmeticDefinition;
import com.astradia.renderer.custom.CosmeticRenderer;
import com.astradia.renderer.entity.state.VentoAvatarRenderState;
import com.astradia.renderer.gecko.VentoDataTickets;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.UUID;

public class VentoCosmeticLayer<T extends Avatar & ClientAvatarEntity & GeoAnimatable, O, R extends AvatarRenderState> extends GeoRenderLayer<T, O, R> {

    public VentoCosmeticLayer(GeoRenderer<T, O, R> renderer) {
        super(renderer);

    }

    @Override
    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        if(!renderPassInfo.willRender()) return;

        PlayerCosmetics cosmetics = getCosmetics(renderPassInfo.renderState());
        var equipment = cosmetics.getSlots();

        for(ClientCosmeticSlot cosmeticSlot : equipment.values()) {
            if(cosmeticSlot.isEmpty()) continue; // Rehacer esta poronga xd

            submitCosmetic(renderPassInfo, renderTasks, cosmeticSlot.getEquipped().getCosmetic(), cosmeticSlot.getEquipped());


        }
    }

    private void submitCosmetic(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks, CosmeticDefinition cosmeticDefinition, PlayerCosmeticData cosmeticData) {
        cosmeticData.getCosmetic().getProperty(ModelProperty.class).ifPresent(modelProperty -> {
            final CosmeticRenderer cosmeticRenderer = ((ClientCosmeticDefinition) cosmeticData.getCosmetic()).getRenderer();
            final float partialTick = renderPassInfo.renderState().getPartialTick();
            final double tick = renderPassInfo.renderState().getAnimatableAge();

            try {
                cosmeticRenderer.performRenderPass(cosmeticRenderer.getAnimatable(), renderPassInfo.renderState(), cosmeticData, null, renderPassInfo.poseStack(), renderTasks, renderPassInfo.cameraState(), renderPassInfo.packedLight(), partialTick, tick, (renderPassInfo1, snapshots) -> {
                    renderPassInfo.renderPosed(() -> {
                        final Vector3f offset = new Vector3f();
                        var rootOptional = renderPassInfo.model().getBone("Root");
                        if(rootOptional.isPresent()) {
                            var root = rootOptional.get();
                            offset.set(root.frameSnapshot.getTranslateX(), root.frameSnapshot.getTranslateY(), root.frameSnapshot.getTranslateZ());
                        }
                        for (GeoBone geoBone : renderPassInfo1.model().topLevelBones()) {
                            snapshots.ifPresent(geoBone.name(), (boneSnapshot -> {

                                renderPassInfo.model().getBone(geoBone.name().substring(5)).ifPresent(bone -> {
                                    PoseStack tempStack = new PoseStack();
                                    RenderUtil.transformToBone(tempStack, bone);
                                    BoneSnapshot frameSnapshot = bone.frameSnapshot;
                                    if(frameSnapshot == null) return;
                                    boneSnapshot.setTranslation(frameSnapshot.getTranslateX() + offset.x, frameSnapshot.getTranslateY() + offset.y, frameSnapshot.getTranslateZ() + offset.z);
                                    boneSnapshot.setRotation(frameSnapshot.getRotX(), frameSnapshot.getRotY(), boneSnapshot.getRotZ()).setScale(frameSnapshot.getScaleX(), frameSnapshot.getScaleY(), frameSnapshot.getScaleZ());
                                });
                            }));
                        }
                    });
                });
            } catch (Exception ignored) {
                // Renderizar cosmético default si hay algun error con el modelo (no existe)
            }



        });
    }
    private PlayerCosmetics getCosmetics(R renderState) {
        UUID uuid = ((VentoAvatarRenderState) renderState).getUuid();
        if(renderState.hasGeckolibData(VentoDataTickets.IS_FAKE_PLAYER)) {
            return VentoClient.getPlayerManager().getFromFakeUuid(uuid).getCosmetics();
        }
        return VentoClient.getPlayerManager().getFromUuid(uuid).getCosmetics();
    }
}
