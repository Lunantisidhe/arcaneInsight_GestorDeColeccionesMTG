package com.dam.rgb;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) {
        try {

            String url = "https://api.scryfall.com/cards/named?fuzzy=aust+com";

            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL).build();

            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
                    .headers("Accept", "application/json").setHeader("User-Agent", "Mozilla/5.0").build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject json = new JSONObject(responseBody);

            System.out.println(response.statusCode());
            System.out.println(json.toString(4));

        } catch (Exception e) {
            e.printStackTrace();
        }

        PixelArt.printPixel("testImg", 3);
        PixelArt.printPixel("https://cards.scryfall.io/art_crop/front/b/c/bce78225-9dbf-46c1-b63d-083c1858eb98.jpg?1680759056", 3);
        PixelArt.printPixel("https://cards.scryfall.io/art_crop/front/d/9/d99a9a7d-d9ca-4c11-80ab-e39d5943a315.jpg?1632831210", 3);
    }
}
