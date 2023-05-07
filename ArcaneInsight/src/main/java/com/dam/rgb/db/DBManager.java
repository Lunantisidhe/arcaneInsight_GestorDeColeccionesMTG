package com.dam.rgb.db;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

public class DBManager {

    // añade una carta a la base de datos
    public static void createCard(JSONObject cardJson) {

        // conexion base de datos mongodb
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("arcaneInsightDB");
        MongoCollection<Document> collection = database.getCollection("collection");

        // pasamos la carta a json
        if (cardJson == null) {
            System.err.println("Error: no se pudo añadir la carta.");
            return;
        }
        String jsonString = cardJson.toString();

        // convierte el json a un objeto java
        Gson gson = new Gson();
        Object obj = gson.fromJson(jsonString, Object.class);

        // convierte el objeto java a un documento bson
        Document document = Document.parse(gson.toJson(obj));

        // añade el documento a la coleccion de mongo
        collection.insertOne(document);

        // cierra el objeto conexion
        mongoClient.close();
    }
}
