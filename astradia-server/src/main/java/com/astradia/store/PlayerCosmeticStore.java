package com.astradia.store;

import com.astradia.Database;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

/*public class PlayerCosmeticStore implements Store<CosmeticData, UUID> {
    private final MongoCollection<CosmeticData> collection;

    public PlayerCosmeticStore() {
        this.collection = Database.getCollection("player_cosmetics", CosmeticData.class);
    }
    @Override
    public void save(UUID id, CosmeticData entity) {
        UpdateOptions options = new UpdateOptions().upsert(true);
        Document query = new Document().append("data",  entity);
        Bson updates = Updates.combine(
                Updates.currentTimestamp("lastUpdated"));

        try {
            UpdateResult result = collection.updateOne(query, updates, options);
            System.out.println("Modified document count: " + result.getModifiedCount());
            System.out.println("Upserted id: " + result.getUpsertedId());

        } catch (MongoException me) {
            System.err.println("Unable to update due to an error: " + me);
        }
    }

    @Override
    public Optional<CosmeticData> findById(UUID uuid) {
        return Optional.ofNullable(collection.find(Filters.eq("_id", uuid)).first());
    }

    @Override
    public List<CosmeticData> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void delete(UUID uuid) {
        try {
            collection.deleteOne(Filters.eq("_id", uuid));
        } catch (MongoException me) {
            System.err.println("Unable to delete due to an error: " + me);
        }
    }

    @Override
    public void saveAll(Map<UUID, CosmeticData> map) {
        try {
            List<WriteModel<CosmeticData>> updates = new ArrayList<>();

            for (Map.Entry<UUID, CosmeticData> entry : map.entrySet()) {
                UUID id = entry.getKey();
                Document update = new Document("$set", entry.getValue());

                updates.add(new UpdateOneModel<>(Filters.eq("_id", id), update, new UpdateOptions().upsert(true)));
            }
            if (!updates.isEmpty()) {
                collection.bulkWrite(updates);
            }
        } catch (MongoException me) {
            System.err.println("Unable to update all due to an error: " + me);
        }
    }
}
*/