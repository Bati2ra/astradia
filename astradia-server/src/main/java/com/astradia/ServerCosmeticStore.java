package com.astradia;

import com.astradia.network.payloads.CosmeticsDataPayload;
import com.astradia.pojo.Cosmetic;
import com.astradia.utils.JsonUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.Map;

public class ServerCosmeticStore extends CosmeticStore<Cosmetic> {
    public static final ServerCosmeticStore INSTANCE = new ServerCosmeticStore();
    private NbtCompound cachedCosmeticsData;
    public void initialize() {

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of(AstradiaServer.MOD_ID, "cosmetics");
            }

            @Override
            public void reload(ResourceManager manager) {
                cosmetics.clear();
                for(Map.Entry<Identifier, Resource> resourceEntry : manager.findResources("cosmetics", path -> path.toString().endsWith(".json")).entrySet()) {
                    try(InputStream stream = resourceEntry.getValue().getInputStream()) {
                        register(JsonUtils.fromJson(stream, Cosmetic.class));
                    } catch(Exception e) {
                        AstradiaServer.LOGGER.error("Error occurred while loading resource json{}", resourceEntry.getKey().toString(), e);
                    }
                }
                cachedCosmeticsData = toNbt();
            }
        });
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((serverPlayer, joined) -> {
            sendToPlayer(serverPlayer);
        });
    }

    private void register(Cosmetic cosmetic) {
        cosmetics.put(cosmetic.getId(), cosmetic);
    }

    public void sendToPlayer(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new CosmeticsDataPayload(cachedCosmeticsData));
        AstradiaServer.LOGGER.info("[Cosmetics] Enviando información de cosméticos a {}.", player.getDisplayName().getString());
    }

    private NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        NbtList list = new NbtList();
        var iterator = cosmetics.entrySet().stream().iterator();
        while(iterator.hasNext()) {
            var entry = iterator.next();
            list.add(entry.getValue().toNbt());
        }
        tag.put("cosmetics", list);
        return tag;
    }
}
