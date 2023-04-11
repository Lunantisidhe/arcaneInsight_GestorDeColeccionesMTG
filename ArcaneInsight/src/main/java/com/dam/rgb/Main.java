package com.dam.rgb;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    static final String LOGO = """
             
             /\\___/\\   /\\___/\\\s
            ( •   • ) ( o   o )\s
              /   \\     /   \\\s
             /     \\   /     \\ \s
              arcane • insight
              
              
            """;

    public static void main(String[] args) {

        System.out.println(LOGO);

        try {

            String url = "https://api.scryfall.com/cards/named?fuzzy=aust+com";

            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL).build();

            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
                    .headers("Accept", "application/json").setHeader("User-Agent", "Mozilla/5.0").build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject json = new JSONObject(responseBody);

            //System.out.println(response.statusCode());
            //System.out.println(json.toString(4));

            /* *** prueba impresion *** */
            String imgUrl = json.getJSONObject("image_uris").getString("art_crop");
            PixelArt.printPixel(imgUrl, 3);
            System.out.println("""
                    
                    ╔═════════════════════════════════════════════════════════╗
                    ║ Austere Command                               {4}{W}{W} ║
                    ║ Sorcery                                                 ║
                    ╟─────────────────────────────────────────────────────────╢
                    ║ Choose two —                                            ║
                    ║ • Destroy all artifacts.                                ║
                    ║ • Destroy all enchantments.                             ║
                    ║ • Destroy all creatures with mana value 3 or less.      ║
                    ║ • Destroy all creatures with mana value 4 or greater.   ║
                    ╟─────────────────────────────────────────────────────────╢
                    ║ R 0172                  ™ & © 2023 Wizards of the Coast ║
                    ║ MOC • EN — Anna Steinbauer                              ║
                    ╚═════════════════════════════════════════════════════════╝
                    
                    """);

            System.out.println(json.getString("name"));
            System.out.println(json.getString("mana_cost"));
            System.out.println(json.getString("type_line"));
            System.out.println(json.getString("oracle_text"));
            System.out.println(json.getString("rarity"));
            System.out.println(json.getString("collector_number"));
            System.out.println(json.getString("released_at"));
            System.out.println(json.getString("set"));
            System.out.println(json.getString("foil"));
            System.out.println(json.getString("lang"));
            System.out.println(json.getString("artist"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //PixelArt.printPixel("testImg", 3);
        //PixelArt.printPixel("https://cards.scryfall.io/art_crop/front/b/c/bce78225-9dbf-46c1-b63d-083c1858eb98.jpg?1680759056", 3);
        //PixelArt.printPixel("https://cards.scryfall.io/art_crop/front/d/9/d99a9a7d-d9ca-4c11-80ab-e39d5943a315.jpg?1632831210", 3);
    }
}
