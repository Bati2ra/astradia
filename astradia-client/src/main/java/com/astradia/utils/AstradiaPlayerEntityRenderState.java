package com.astradia.utils;

import java.util.UUID;

public interface AstradiaPlayerEntityRenderState {
    UUID getUuid();
    void setUuid(UUID uuid);
    float getPartialTick();
    void setPartialTick(float tick);
}
