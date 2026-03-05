package com.astradia.renderer.gecko;

import com.astradia.api.player.PlayerCosmeticData;
import com.astradia.screen.util.FakePlayer;
import com.google.common.reflect.TypeToken;
import net.minecraft.client.entity.ClientAvatarEntity;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public class VentoDataTickets {
   // public static final DataTicket<PlayerCosmeticData> PLAYER_COSMETIC_DATA_TICKET = DataTicket.create("player_cosmetic_data", PlayerCosmeticData.class);
    public static final DataTicket<PlayerCosmeticData> PLAYER_COSMETIC_DATA_TICKET = DataTicket.create("player_cosmetic_data", PlayerCosmeticData.class, new TypeToken<>() {});
    public static final DataTicket<ClientAvatarEntity> PLAYER_REFERENCE_TICKET = DataTicket.create("player_reference", ClientAvatarEntity.class);
    public static final DataTicket<Boolean> IS_FAKE_PLAYER = DataTicket.create("fake_player", Boolean.class);
    public static final DataTicket<FakePlayer.FakeProfile> FAKE_PLAYER_PROFILE = DataTicket.create("fake_player_profile", FakePlayer.FakeProfile.class);
}
