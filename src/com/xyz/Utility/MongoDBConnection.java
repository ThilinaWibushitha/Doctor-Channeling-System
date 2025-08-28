package com.xyz.Utility;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.*;

public class MongoDBConnection {

    private static MongoDBConnection instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoDBConnection() {
        try {
            // Set up codec registry for POJOs (Plain Old Java Objects)
            CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
            CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

            // Connect to MongoDB with custom codec registry
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("Doctor_System").withCodecRegistry(codecRegistry);

            System.out.println("✅ MongoDB connection established with POJO codec.");
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not connect to MongoDB: " + e.getMessage());
            System.err.println("⚠️ The application will run in demo mode with limited functionality.");
            throw new RuntimeException("MongoDB connection failed", e);
        }
    }

    public static MongoDBConnection getInstance() {
        if (instance == null) {
            synchronized (MongoDBConnection.class) {
                if (instance == null) {
                    instance = new MongoDBConnection();
                }
            }
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        mongoClient.close();
        System.out.println("🔒 MongoDB connection closed.");
    }
}
