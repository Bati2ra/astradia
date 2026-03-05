package com.astradia.mixin;

import com.astradia.renderer.entity.state.VentoAvatarRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(AvatarRenderState.class)
public class PlayerEntityRenderStateMixin implements VentoAvatarRenderState {
	public UUID vento$uuid;

	@Override
	public UUID getUuid() {
		return vento$uuid;
	}

	@Override
	public void setUuid(UUID uuid) {
		this.vento$uuid = uuid;
	}
}