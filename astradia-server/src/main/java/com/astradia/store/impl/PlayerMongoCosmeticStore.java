package com.astradia.store.impl;

import com.astradia.store.MongoStore;
import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;

import java.util.Optional;
import java.util.UUID;

public class PlayerMongoCosmeticStore extends MongoStore<PlayerMongoCosmeticStore.CosmeticData, UUID> {

    public PlayerMongoCosmeticStore() {
        super("player_cosmetics", CosmeticData.class);
    }

    @Override
    public void save(UUID id, CosmeticData entity) {
        UpdateOptions options = new UpdateOptions().upsert(true);
        Bson updates = Updates.combine(
                Updates.set("data", entity),
                Updates.currentTimestamp("lastUpdated")
        );

        try {
            UpdateResult result = collection.updateOne(Filters.eq("_id", id), updates, options);
            System.out.println("[CosmeticsStore] Updated: " + result.getModifiedCount() + ", Upserted: " + result.getUpsertedId());
        } catch (MongoException me) {
            System.err.println("Unable to update cosmetics: " + me);
        }
    }

    @Override
    public Optional<CosmeticData> findById(UUID uuid) {
        return Optional.ofNullable(collection.find(Filters.eq("_id", uuid)).first());
    }

    public static class CosmeticData {

    }
}