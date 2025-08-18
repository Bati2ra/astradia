package com.astradia.store;

import com.astradia.Database;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MongoStore<T, ID> implements Store<T, ID> {
    protected final MongoCollection<T> collection;

    public MongoStore(String collectionName, Class<T> clazz) {
        this.collection = Database.getCollection(collectionName, clazz);
    }

    @Override
    public List<T> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void delete(ID id) {
        try {
            collection.deleteOne(Filters.eq("_id", id));
        } catch (MongoException me) {
            System.err.println("Unable to delete due to an error: " + me);
        }
    }

    @Override
    public void saveAll(Map<ID, T> map) {
        try {
            List<WriteModel<T>> updates = new ArrayList<>();
            for (Map.Entry<ID, T> entry : map.entrySet()) {
                Document update = new Document("$set", entry.getValue());
                updates.add(new UpdateOneModel<>(Filters.eq("_id", entry.getKey()), update, new UpdateOptions().upsert(true)));
            }
            if (!updates.isEmpty()) {
                collection.bulkWrite(updates);
            }
        } catch (MongoException me) {
            System.err.println("Unable to bulk save due to an error: " + me);
        }
    }
}