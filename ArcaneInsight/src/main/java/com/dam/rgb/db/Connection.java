package com.dam.rgb.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Connection {

    MongoClient client;
    MongoDatabase database;
    MongoCollection<Document> collection;

    // representa una conexion con la base de datos
    public Connection(String collectionName) {

        client = MongoClients.create();
        database = client.getDatabase("arcaneInsightDB");
        collection = database.getCollection(collectionName);
    }

    public void close() {

        client.close();
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }
}
