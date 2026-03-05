package com.astradia.screen.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;

import java.util.UUID;

public class FakePlayer extends RemotePlayer {
    private final UUID fakeuuid;
    private FakeProfile profile;

    public FakePlayer(ClientLevel clientLevel) {
        this(clientLevel, UUID.randomUUID());
    }
    public FakePlayer(ClientLevel clientLevel, UUID uuid) {
        super(clientLevel, createProfile(uuid));
        fakeuuid = uuid;
    }

    private static GameProfile createProfile(UUID uuid) {
        return new GameProfile(uuid, uuid.toString());
    }

    public void setProfile(FakeProfile profile) {
        this.profile = profile;
    }

    public FakeProfile getProfile() {
        return profile;
    }

    @Override
    public UUID getUUID() {
        return fakeuuid;
    }

    public void tickForRender() {
        this.tickCount++;
        this.setYRot(180f);
        this.setXRot(0f);
        this.yBodyRot = 180f;
        this.yHeadRot = 180f;
    }

    public record FakeProfile(Identifier body, PlayerModelType model) { }
}
