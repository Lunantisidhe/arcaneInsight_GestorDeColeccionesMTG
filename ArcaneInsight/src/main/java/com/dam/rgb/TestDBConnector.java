package com.dam.rgb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class TestDBConnector {
    public static void main(String[] args) {

        MongoClient client = MongoClients.create();
        MongoDatabase db = client.getDatabase("arcaneInsightDB");
        MongoCollection<Document> collection = db.getCollection("collection");

        Document newCard = new Document();
        newCard.put("name", "Aeon Engine");
        newCard.put("mana_cost", "{5}");
        newCard.put("type_line", "Artifact");
        collection.insertOne(newCard);

        List<Document> data = collection.find().into(new ArrayList<>());
        for (Document doc: data) {
            System.out.println(doc.toString());
        }
    }
}
