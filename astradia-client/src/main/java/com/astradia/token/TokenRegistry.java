package com.astradia.token;

import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TokenRegistry {
    private static final Map<String, TokenResolver<?>> RESOLVERS = new HashMap<>();

    public static void register(String tokenKey, TokenResolver<?> resolver) {
        RESOLVERS.put(tokenKey, resolver);
    }

    public static Optional<Object> resolve(AbstractClientPlayerEntity player, String token) {
        TokenResolver<?> resolver = RESOLVERS.get(token);
        if (resolver != null) {
            return Optional.ofNullable(resolver.resolve(player, token));
        }
        return Optional.empty();
    }

    public static void initialize() {
        register("player_skin", new PlayerSkinResolver());
    }
}