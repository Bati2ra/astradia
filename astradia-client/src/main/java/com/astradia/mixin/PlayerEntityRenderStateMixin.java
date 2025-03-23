package com.astradia.mixin;

import com.astradia.utils.AstradiaPlayerEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements AstradiaPlayerEntityRenderState {
	public UUID uuid;
	public float partialTick;

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public float getPartialTick() {
		return partialTick;
	}

	@Override
	public void setPartialTick(float tick) {
		this.partialTick = tick;
	}
}