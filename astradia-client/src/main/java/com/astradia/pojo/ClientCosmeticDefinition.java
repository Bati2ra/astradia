package com.astradia.pojo;

import com.astradia.api.CosmeticDefinition;
import com.astradia.renderer.custom.CosmeticRenderer;
import com.google.gson.JsonObject;

public class ClientCosmeticDefinition extends CosmeticDefinition {
    private final CosmeticRenderer renderer;

    public ClientCosmeticDefinition(JsonObject json) throws Exception {
        super(json);
        renderer = new CosmeticRenderer(this, new CosmeticAnimatable(this));
    }

    public CosmeticRenderer getRenderer() {
        return renderer;
    }

}
