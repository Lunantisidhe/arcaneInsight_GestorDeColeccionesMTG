package com.dam.rgb;

import com.dam.rgb.visual.Printer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestBulk {
    public static void main(String[] args) throws IOException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL).build();

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://api.scryfall.com/bulk-data"))
                .headers("Accept", "application/json").setHeader("User-Agent", "Mozilla/5.0").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        JSONObject json = new JSONObject(responseBody);


        JSONArray hola = json.getJSONArray("data");
        String uri = "";
        for (int i = 0; i < hola.length(); i++) {
            JSONObject obj = (JSONObject) hola.get(i);
            if (obj.get("type").toString().equals("default_cards")) {
                uri = obj.get("download_uri").toString();
            }
        }
        System.out.println(uri);
    }
}
