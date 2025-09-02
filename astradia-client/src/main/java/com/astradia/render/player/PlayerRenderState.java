package com.astradia.render.player;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;

public class PlayerRenderState extends PlayerEntityRenderState implements GeoRenderState {
    @Override
    public <D> void addGeckolibData(DataTicket<D> dataTicket, @Nullable D d) {

    }

    @Override
    public boolean hasGeckolibData(DataTicket<?> dataTicket) {
        return false;
    }

    @Override
    public <D> @Nullable D getGeckolibData(DataTicket<D> dataTicket) {
        return null;
    }

    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return Map.of();
    }
}
