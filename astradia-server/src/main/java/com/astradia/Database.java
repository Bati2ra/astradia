package com.astradia;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Database {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "astradia";

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    private static final CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
    private static final CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);


    public static void connect() {
        mongoClient = MongoClients.create(CONNECTION_STRING);
        database = mongoClient.getDatabase(DATABASE_NAME);
        System.out.println("Conectado a MongoDB correctamente");
    }

    public static <T> MongoCollection<T> getCollection(String collectionName, Class<T> entityClass) {
        return database.getCollection(collectionName, entityClass).withCodecRegistry(codecRegistry);
    }

    public static void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
