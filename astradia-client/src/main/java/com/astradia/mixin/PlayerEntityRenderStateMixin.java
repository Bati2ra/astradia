package com.astradia.mixin;

import com.astradia.utils.AstradiaPlayerEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;
import java.util.UUID;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements AstradiaPlayerEntityRenderState {
	public UUID vento$uuid;
	public float vento$partialTick;

	@Override
	public UUID getUuid() {
		return vento$uuid;
	}

	@Override
	public void setUuid(UUID uuid) {
		this.vento$uuid = uuid;
	}

	@Override
	public float getPartialTick() {
		return vento$partialTick;
	}

	@Override
	public void setPartialTick(float tick) {
		this.vento$partialTick = tick;
	}
}