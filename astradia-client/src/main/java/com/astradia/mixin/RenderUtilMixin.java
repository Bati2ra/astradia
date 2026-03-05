package com.astradia.mixin;

import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import software.bernie.geckolib.util.ClientUtil;

@Mixin(ClientUtil.class)
public class RenderUtilMixin {


    @Overwrite
    public static double getCurrentTick() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level != null ? ((double)mc.level.getGameTime() + mc.getDeltaTracker().getGameTimeDeltaPartialTick(false)) : Blaze3D.getTime() * 20.0;
    }
}
