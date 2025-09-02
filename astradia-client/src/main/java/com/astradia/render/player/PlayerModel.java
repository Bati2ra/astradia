package com.astradia.render.player;

import com.astradia.AstradiaClient;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class PlayerModel<T extends GeoAnimatable> extends GeoModel<T> {

    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return Identifier.of(AstradiaClient.MOD_ID, "pepe");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        if(geoRenderState instanceof PlayerEntityRenderState playerEntityRenderState) {
            return playerEntityRenderState.skinTextures.texture();
        }
        return Identifier.of(AstradiaClient.MOD_ID, "textures/texture.png");
    }

    @Override
    public Identifier getAnimationResource(GeoAnimatable geoAnimatable) {
        return Identifier.of(AstradiaClient.MOD_ID, "player");
    }

}
