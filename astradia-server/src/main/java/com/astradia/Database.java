package com.astradia;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Database {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017"; // Cambia según sea necesario
    private static final String DATABASE_NAME = "astradia";

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void connect() {
        mongoClient = MongoClients.create(CONNECTION_STRING);
        database = mongoClient.getDatabase(DATABASE_NAME);
        System.out.println("Conectado a MongoDB correctamente");
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public static void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
