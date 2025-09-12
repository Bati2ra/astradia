package com.astradia.token;

import net.minecraft.client.network.AbstractClientPlayerEntity;

public interface TokenResolver<T> {
    T resolve(AbstractClientPlayerEntity player, String token);
}