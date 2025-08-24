package com.astradia.pojo;

import com.astradia.api.CosmeticInfo;
import com.astradia.render.CosmeticRenderer;
import com.google.gson.JsonObject;

public class ClientCosmeticInfo extends CosmeticInfo {
    private final CosmeticRenderer renderer;

    public ClientCosmeticInfo(JsonObject json) throws Exception {
        super(json);
        renderer = new CosmeticRenderer(this, new CosmeticAnimatable(this));
    }

    public CosmeticRenderer getRenderer() {
        return renderer;
    }

}
