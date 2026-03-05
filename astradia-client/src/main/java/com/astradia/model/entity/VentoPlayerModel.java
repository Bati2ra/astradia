package com.astradia.model.entity;

import com.astradia.VentoClient;
import com.astradia.renderer.gecko.VentoDataTickets;
import com.astradia.screen.util.FakePlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class VentoPlayerModel<T extends GeoAnimatable> extends GeoModel<T> {

    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return Identifier.fromNamespaceAndPath(VentoClient.MOD_ID, "pepe");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        if(geoRenderState instanceof AvatarRenderState playerEntityRenderState) {
            FakePlayer.FakeProfile fakeProfile = geoRenderState.getGeckolibData(VentoDataTickets.FAKE_PLAYER_PROFILE);
            if(fakeProfile != null && fakeProfile.body() != null) {
                return fakeProfile.body();
            }
            return playerEntityRenderState.skin.body().texturePath();
        }
        return Identifier.fromNamespaceAndPath(VentoClient.MOD_ID, "textures/texture.png");
    }

    @Override
    public Identifier getAnimationResource(GeoAnimatable geoAnimatable) {
        return Identifier.fromNamespaceAndPath(VentoClient.MOD_ID, "si");
    }

}
