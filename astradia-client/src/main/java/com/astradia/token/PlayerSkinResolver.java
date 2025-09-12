package com.astradia.token;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerSkinResolver implements TokenResolver<Identifier> {
    @Override
    public Identifier resolve(AbstractClientPlayerEntity player, String token) {
        if(player.getSkinTextures() == null) {
            return Identifier.of("");
        }
        return player.getSkinTextures().texture();
    }
}
