package com.dam.rgb.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BulkDefaultCardsImporter {

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

    public static void importAllDefaultCards(){

        JSONObject allCardsJsonObj = requestAllCardsData();
        if (allCardsJsonObj == null || allCardsJsonObj.isEmpty())
            return;
        JSONArray jsonDataArray = allCardsJsonObj.getJSONArray("data");

        String urlName = "";
        for (int i = 0; i < jsonDataArray.length(); i++) {

            JSONObject jsonObj = (JSONObject) jsonDataArray.get(i);
            if (jsonObj.get("type").toString().equals("default_cards")) {
                urlName = jsonObj.get("download_uri").toString();
                break;
            }
        }

        try {
            URL url = new URL(urlName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                response.append(line);
            reader.close();

            String json = response.toString();
            JSONArray jsonArray = new JSONArray(json);

            DBManager.createSeveralCards(jsonArray, "allCards");

        } catch (IOException | JSONException e) {
            System.err.println("Error: No se pudieron importar las cartas.");
        }
    }
}
