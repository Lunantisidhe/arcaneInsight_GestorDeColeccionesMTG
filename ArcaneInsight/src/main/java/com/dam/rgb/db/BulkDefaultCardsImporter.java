package com.dam.rgb.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class BulkDefaultCardsImporter {
    public static void importAllDefaultCards(){

        JSONObject allCardsJsonObj = JSONManager.requestAllCardsData();
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
