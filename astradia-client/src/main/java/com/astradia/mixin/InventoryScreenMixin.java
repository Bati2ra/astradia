package com.astradia.mixin;

import com.astradia.renderer.entity.player.PlayerRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin {

    @WrapOperation(
            method = "renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;extractRenderState(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;", remap = false)
    )
    private static EntityRenderState wrapRenderState(LivingEntity entity, Operation<EntityRenderState> original) {
        if(entity instanceof AbstractClientPlayer player) {
            PlayerRenderer playerRenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            var state = playerRenderer.createRenderState(entity, 1);
            if(state instanceof GeoRenderState geoRenderState) {
                geoRenderState.addGeckolibData(DataTickets.PACKED_LIGHT, 15728880);
            }
            return state;
        } else {
            return original.call(entity);
        }
    }
}
