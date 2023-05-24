package com.dam.rgb.db;

import com.dam.rgb.db.utilities.Connection;
import com.google.gson.Gson;
import com.mongodb.client.MongoCursor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Updates.set;

public class DBManager {

    private static final int SEARCH_LIMIT = 500;

    /* METODOS CREACION */
    // añade una carta a la base de datos
    public static void createCard(JSONObject cardJsonObj, String collectionName, double quantity) {

        // conexion base de datos mongodb
        Connection connection = new Connection(collectionName);

        if (cardJsonObj == null || cardJsonObj.isEmpty()) {
            System.err.println("Error: no se pudo añadir la carta.");
            return;
        }

        // si la carta ya existe, añadimos a su cantidad
        for (Document doc : connection.getCollection().find()) {
            if (cardJsonObj.getString("name").equals(doc.getString("name"))) {
                connection.getCollection().updateOne(doc, set("quantity", doc.getDouble("quantity") + quantity));
                return;
            }
        }

        // quitamos el id del registro de la carta
        cardJsonObj.remove("_id");

        // pasamos la carta de objeto json a json
        String cardJson = cardJsonObj.toString();

        // convierte el json a un objeto java
        Gson cardGson = new Gson();
        Object cardObj = cardGson.fromJson(cardJson, Object.class);

        // convierte el objeto java a un documento bson
        Document cardDoc = Document.parse(cardGson.toJson(cardObj)).append("quantity", quantity);

        // añade el documento a la coleccion de mongo
        connection.getCollection().insertOne(cardDoc);

        // cierra el objeto conexion
        connection.close();
    }

    // añade una lista de cartas a la base de datos
    public static ArrayList<Document> JSONArrayToDocArray(JSONArray cardJsonArray) {

        if (cardJsonArray == null || cardJsonArray.isEmpty()) {
            System.err.println("Error: no se pudieron añadir las cartas.");
            return null;
        }

        // leemos el array de cartas
        ArrayList<Document> cardDocs = new ArrayList<>();
        for (int i = 0; i < cardJsonArray.length(); i++) {

            JSONObject cardJsonObj = cardJsonArray.getJSONObject(i);
            if (cardJsonObj == null || cardJsonObj.isEmpty()) {
                System.err.println("Error: no se pudo añadir la carta.");
                continue;
            }

            // pasamos la carta a json
            String cardJson = cardJsonObj.toString();

            // convierte el json a un objeto java
            Gson cardGson = new Gson();
            Object cardObj = cardGson.fromJson(cardJson, Object.class);

            // convierte el objeto java a un documento bson
            Document cardDoc = Document.parse(cardGson.toJson(cardObj));
            cardDocs.add(cardDoc);
        }

        return cardDocs;
    }

    // añade una lista de cartas a la base de datos
    public static void createSeveralCards(ArrayList<Document> cardDocs, String collectionName) {

        // conexion base de datos mongodb
        Connection connection = new Connection(collectionName);

        // importacion todas las cartas
        if (collectionName.equals("allCards")) {

            connection.getCollection().insertMany(cardDocs);
            connection.close();
        }

        else {

            ArrayList<Document> cardsToAdd = new ArrayList<>();

            if (cardDocs == null || cardDocs.isEmpty()) {
                System.err.println("Error: no se pudieron añadir las cartas.");
                return;
            }

            // leemos el array de cartas
            for (Document cardDoc : cardDocs) {

                // añadimos la cantidad si no la tiene ya
                if (cardDoc.getDouble("quantity") == null)
                    cardDoc.append("quantity", 1d);

                // quitamos el id del registro de todas las cartas
                cardDoc.remove("_id");

                cardsToAdd.add(cardDoc);

                // si la carta ya existe, añadimos a su cantidad
                for (Document doc : connection.getCollection().find()) {
                    if (cardDoc.getString("name").equals(doc.getString("name"))) {
                        connection.getCollection().updateOne(doc, set("quantity",
                                doc.getDouble("quantity") + cardDoc.getDouble("quantity")));
                        cardsToAdd.remove(cardDoc);
                    }
                }
            }

            // añade los documentos a la coleccion de mongo
            if (!cardsToAdd.isEmpty())
                connection.getCollection().insertMany(cardsToAdd);

            // cierra el objeto conexion
            connection.close();
        }
    }

    // crea un mazo en la base de datos
    public static void createDeck(String deckName) {

        // conexion base de datos mongodb
        Connection connection = new Connection();

        // el mazo ya existe
        if (connection.getDatabase().listCollectionNames().into(new ArrayList<>()).contains(deckName + "_deck"))
            System.out.println("El mazo " + deckName + " ya existe");

        else {
            connection.setCollection(deckName + "_deck");

            // crea un objeto con datos del mazo
            Document data = new Document().append("data_type", "info").append("creation_date", LocalDateTime.now());

            // añade el documento a la coleccion de mongo
            connection.getCollection().insertOne(data);

            System.out.println("Se ha creado el mazo " + deckName);
        }

        // cierra el objeto conexion
        connection.close();
    }


    /* METODOS LECTURA */
    // busca una carta dentro de una coleccion y devuelve la cantidad encontrada
    public static double searchCardInCollection(String cardParam, String searchField, String collectionName) {

        // conexion base de datos mongodb
        Connection connection = new Connection(collectionName);

        ArrayList<Document> searchResults = new ArrayList<>();
        MongoCursor<Document> cursor = connection.getCollection().find().iterator();

        double quantity = 0;

        while (cursor.hasNext()) {

            Document cardDoc = cursor.next();
            String fieldValue = cardDoc.getString(searchField);

            // si encontramos la carta, devolvemos true
            if (cardParam.equals(fieldValue)) {
                quantity = cardDoc.getDouble("quantity");
                break;
            }
        }

        // cierra los objetos
        cursor.close();
        connection.close();

        return quantity;
    }

    // recupera todas las cartas de una coleccion
    public static ArrayList<Document> recoverAllCards(String collectionName) {

        // conexion base de datos mongodb
        Connection connection = new Connection(collectionName);
        MongoCursor<Document> cursor = connection.getCollection().find().iterator();

        ArrayList<Document> allCards = new ArrayList<>();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            if (document.getString("data_type") == null)
                allCards.add(document);
        }

        // cierra los objetos
        cursor.close();
        connection.close();

        return allCards;
    }

    // recupera las cartas coincidentes de la base de datos
    public static ArrayList<Document> searchFuzzyCards
        (String fuzzyCardParam, String searchField, String collectionName, boolean firstCardOnly) {

        // conexion base de datos mongodb
        Connection connection = new Connection(collectionName);

        ArrayList<Document> searchResults = new ArrayList<>();
        MongoCursor<Document> cursor = connection.getCollection().find().iterator();

        while (cursor.hasNext()) {

            Document cardDoc = cursor.next();
            String fieldValue = cardDoc.getString(searchField);

            // si la similitud entre el campo a buscar y el recuperado es mayor al 80%, añadimos los resultados
            if (FuzzySearch.extractOne(fuzzyCardParam, Collections.singleton(fieldValue)).getScore() >= 80) {

                searchResults.add(cardDoc);

                if (firstCardOnly)
                    break;

                // limite resultados
                else if (searchResults.size() >= SEARCH_LIMIT)
                    break;
            }
        }

        // cierra los objetos
        cursor.close();
        connection.close();

        return searchResults;
    }

    // recupera todos los mazos existentes en la base de datos
    public static ArrayList<String> recoverDecks() {

        // conexion base de datos mongodb
        Connection connection = new Connection();

        // recupera los nombres de los mazos
        List<String> deckNames = connection.getDatabase().listCollectionNames().into(new ArrayList<>())
                .stream().filter(name -> name.endsWith("_deck")).collect(Collectors.toList());

        // cierra el objeto conexion
        connection.close();

        return (ArrayList<String>) deckNames;
    }


    /* METODOS BORRADO */
    // elimina una carta de la base de datos
    public static void deleteCard(JSONObject cardJsonObj, String collectionName) {

        // conexion base de datos mongodb
        Connection connection = new Connection(collectionName);

        if (cardJsonObj == null || cardJsonObj.isEmpty()) {
            System.err.println("Error: no se pudo eliminar la carta.");
            return;
        }

        // pasamos la carta de objeto json a json
        String cardJson = cardJsonObj.toString();

        // convierte el json a un objeto java
        Gson cardGson = new Gson();
        Object cardObj = cardGson.fromJson(cardJson, Object.class);

        // convierte el objeto java a un documento bson
        Document cardDoc = Document.parse(cardGson.toJson(cardObj));

        // si hay varias copias de la carta, le restamos a su cantidad en vez de eliminarla
        if (cardDoc.getDouble("quantity") > 1)
            connection.getCollection().updateOne(cardDoc, set("quantity", cardDoc.getDouble("quantity") - 1));

        // elimina el documento de la coleccion de mongo
        else
            connection.getCollection().deleteOne(cardDoc);

        // cierra el objeto conexion
        connection.close();

    }

    // elimina un mazo de la base de datos
    public static void deleteDeck(String deckName) {

        // conexion base de datos mongodb
        Connection connection = new Connection(deckName);

        // elimina el mazo
        connection.getCollection().drop();

        // cierra el objeto conexion
        connection.close();
    }


    /* METODOS ACTUALIZACION BD */
    // añade los datos de ultima importacion a la base de datos
    public static void addLastUpdatedDate(LocalDateTime updateDate) {

        // conexion base de datos mongodb
        Connection connection = new Connection("data");

        // insertamos los datos
        Document data = new Document("data_type", "info").append("last_update_date", updateDate);
        connection.getCollection().insertOne(data);

        // cierra el objeto conexion
        connection.close();
    }

    // visualiza los datos de ultima importacion de la base de datos
    public static LocalDateTime recoverLastUpdatedDate() {

        // conexion base de datos mongodb
        Connection connection = new Connection("data");

        // recuperamos los datos
        Document data = connection.getCollection().find().first();

        if (data == null)
            return LocalDateTime.MIN;

        Date updateDate = data.getDate("last_update_date");

        // cierra el objeto conexion
        connection.close();

        return LocalDateTime.ofInstant(updateDate.toInstant(), ZoneId.systemDefault());
    }
}
