package com.dam.rgb.db;

import com.google.gson.Gson;
import com.mongodb.client.*;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class DBManager {

    // añade una carta a la base de datos
    public static void createCard(JSONObject cardJsonObj, String collectionName) {

        // conexion base de datos mongodb
        MongoClient client = MongoClients.create();
        MongoDatabase database = client.getDatabase("arcaneInsightDB");
        MongoCollection<Document> collection = database.getCollection(collectionName);

        // pasamos la carta de objeto json a json
        if (cardJsonObj == null || cardJsonObj.isEmpty()) {
            System.err.println("Error: no se pudo añadir la carta.");
            return;
        }
        String cardJson = cardJsonObj.toString();

        // convierte el json a un objeto java
        Gson cardGson = new Gson();
        Object cardObj = cardGson.fromJson(cardJson, Object.class);

        // convierte el objeto java a un documento bson
        Document cardDoc = Document.parse(cardGson.toJson(cardObj));

        // añade el documento a la coleccion de mongo
        collection.insertOne(cardDoc);

        // cierra el objeto conexion
        client.close();
    }

    // añade una lista de cartas a la base de datos
    public static void createSeveralCards(JSONArray cardJsonArray, String collectionName) {

        // conexion base de datos mongodb
        MongoClient client = MongoClients.create();
        MongoDatabase database = client.getDatabase("arcaneInsightDB");
        MongoCollection<Document> collection = database.getCollection(collectionName);

        if (cardJsonArray == null || cardJsonArray.isEmpty()) {
            System.err.println("Error: no se pudieron añadir las cartas.");
            return;
        }

        // leemos el array de cartas
        ArrayList<Document> cardDocs = new ArrayList<>();
        for (int i = 0; i < cardJsonArray.length(); i++) {

            // pasamos la carta a json
            JSONObject cardJsonObj = cardJsonArray.getJSONObject(i);
            if (cardJsonObj == null || cardJsonObj.isEmpty()) {
                System.err.println("Error: no se pudo añadir la carta.");
                continue;
            }
            String cardJson = cardJsonObj.toString();

            // convierte el json a un objeto java
            Gson cardGson = new Gson();
            Object cardObj = cardGson.fromJson(cardJson, Object.class);

            // convierte el objeto java a un documento bson
            Document cardDoc = Document.parse(cardGson.toJson(cardObj));
            cardDocs.add(cardDoc);
        }

        // añade los documentos a la coleccion de mongo
        collection.insertMany(cardDocs);

        // cierra el objeto conexion
        client.close();
    }

    // recupera las cartas coincidentes de la base de datos
    public static ArrayList<Document> searchFuzzyCards
        (String fuzzyCardName, String searchField, String collectionName, boolean firstCardOnly) {

        // conexion base de datos mongodb
        MongoClient client = MongoClients.create();
        MongoDatabase database = client.getDatabase("arcaneInsightDB");
        MongoCollection<Document> collection = database.getCollection(collectionName);

        ArrayList<Document> searchResults = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();

        while (cursor.hasNext()) {

            Document cardDoc = cursor.next();
            String fieldValue = cardDoc.getString(searchField);

            // si la similitud entre el campo a buscar y el recuperado es mayor al 80%, añadimos los resultados
            if (FuzzySearch.extractOne(fuzzyCardName, Collections.singleton(fieldValue)).getScore() >= 80) {
                searchResults.add(cardDoc);
                if (firstCardOnly)
                    break;
            }
        }

        // cierra los objetos
        cursor.close();
        client.close();

        return searchResults;
    }
}
