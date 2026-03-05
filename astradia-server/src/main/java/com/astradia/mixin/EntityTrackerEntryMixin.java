package com.astradia.mixin;

import com.astradia.VentoServer;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public abstract class EntityTrackerEntryMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(method = "addPairing", at = @At("RETURN"))
	private void onStartedTracking(ServerPlayer player, CallbackInfo ci) {
		if(entity instanceof ServerPlayer trackedPlayer) {
			VentoServer.getPlayerManager().onPlayerTracking(player, trackedPlayer);
		}
	}
}