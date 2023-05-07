package com.dam.rgb.db;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JSONManager {

    // hace una httprequest para recuperar el json de una carta
    public static JSONObject requestCardJson(String fuzzyCardName) {

        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL).build();

            HttpRequest request = HttpRequest.newBuilder().GET()
                    .uri(URI.create("https://api.scryfall.com/cards/named?fuzzy=" + fuzzyCardName))
                    .headers("Accept", "application/json").setHeader("User-Agent", "Mozilla/5.0").build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            return new JSONObject(responseBody);

        } catch (JSONException | IOException | InterruptedException e) {
            System.err.println("Error: no se pudieron recibir los datos de la carta.");
        }

        System.err.println("Error: no se encontró la carta introducida.");
        return null;
    }

    // hace una httprequest para recuperar el json con los datos de todas las cartas
    public static JSONObject requestAllCardsData() {

        try {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL).build();

            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://api.scryfall.com/bulk-data"))
                    .headers("Accept", "application/json").setHeader("User-Agent", "Mozilla/5.0").build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            return new JSONObject(responseBody);

        } catch (JSONException | IOException | InterruptedException e) {
            System.err.println("Error: no se pudieron recibir los datos de las cartas.");
        }

        System.err.println("Error: no se encontraron los datos de las cartas.");
        return null;
    }
}
