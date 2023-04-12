package com.dam.rgb;

import org.json.JSONException;
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

        //System.out.println(LOGO);

        try {

            String[] urls = {
                    /* types */
                    //artifact
                    "https://api.scryfall.com/cards/named?fuzzy=aeon+engine",
                    //battle
                    "https://api.scryfall.com/cards/named?fuzzy=invasion+ikoria",
                    //conspiracy
                    "https://api.scryfall.com/cards/named?fuzzy=power+play",
                    //creature
                    "https://api.scryfall.com/cards/named?fuzzy=aboroth",
                    //emblem
                    "https://api.scryfall.com/cards/named?fuzzy=chandra+torch+defiance+emblem",
                    //enchantment
                    "https://api.scryfall.com/cards/named?fuzzy=alpine+moon",
                    //hero
                    "https://api.scryfall.com/cards/named?fuzzy=the+destined",
                    //instant
                    "https://api.scryfall.com/cards/named?fuzzy=absorb",
                    //land
                    "https://api.scryfall.com/cards/named?fuzzy=access+tunel",
                    //phenomenon
                    "https://api.scryfall.com/cards/named?fuzzy=interplanar+tunnel",
                    //plane
                    "https://api.scryfall.com/cards/named?fuzzy=lethe+lake",
                    //planeswalker
                    "https://api.scryfall.com/cards/named?fuzzy=ajani+sleeper+agent",
                    //scheme
                    "https://api.scryfall.com/cards/named?fuzzy=choose+demise",
                    //vanguard
                    "https://api.scryfall.com/cards/named?fuzzy=maraxus",

                    /* supertypes */
                    //basic
                    "https://api.scryfall.com/cards/named?fuzzy=island",
                    //legendary
                    "https://api.scryfall.com/cards/named?fuzzy=adamaro+first+desire",
                    //token
                    "https://api.scryfall.com/cards/named?fuzzy=bat",

                    /* subtypes */
                    //attraction
                    "https://api.scryfall.com/cards/named?fuzzy=bumper+cars",
                    //contraption
                    "https://api.scryfall.com/cards/named?fuzzy=accesories+murder",
                    //equipment
                    "https://api.scryfall.com/cards/named?fuzzy=ancestral+katana",
                    //gold
                    "https://api.scryfall.com/cards/named?fuzzy=gold",
                    //class
                    "https://api.scryfall.com/cards/named?fuzzy=bard+class",
                    //saga
                    "https://api.scryfall.com/cards/named?fuzzy=elspeth+nightmare",
                    //adventure
                    "https://api.scryfall.com/cards/named?fuzzy=crystal+dragon",

                    /* unique */
                    //two faced
                    "https://api.scryfall.com/cards/named?fuzzy=befriending+moths",
                    //nightbound
                    "https://api.scryfall.com/cards/named?fuzzy=child+pack",
                    //meld
                    "https://api.scryfall.com/cards/named?fuzzy=gisela+broken+blade",
                    "https://api.scryfall.com/cards/named?fuzzy=brisela+voice+nightmares",
                    //leveler
                    "https://api.scryfall.com/cards/named?fuzzy=echo+mage",
                    //split
                    "https://api.scryfall.com/cards/named?fuzzy=odds+ends",
                    //split with fuse
                    "https://api.scryfall.com/cards/named?fuzzy=breaking+entering",
                    //split with aftermath
                    "https://api.scryfall.com/cards/named?fuzzy=rags+riches",
                    //flip
                    "https://api.scryfall.com/cards/named?fuzzy=akki+lavarunner",
                    //prototype
                    "https://api.scryfall.com/cards/named?fuzzy=fallaji+dragon+engine",
                    //host
                    "https://api.scryfall.com/cards/named?fuzzy=labro+bot",
                    //augment
                    "https://api.scryfall.com/cards/named?fuzzy=half+squirrel",
                    //dungeon
                    "https://api.scryfall.com/cards/named?fuzzy=dungeon+mad+mage",

                    /* un */
                    "https://api.scryfall.com/cards/named?fuzzy=market+elemental",
                    "https://api.scryfall.com/cards/named?fuzzy=bureaucracy",

                    /* conflictive */
                    "https://api.scryfall.com/cards/named?fuzzy=asmora",
                    "https://api.scryfall.com/cards/named?fuzzy=okina+temple",
                    "https://api.scryfall.com/cards/named?fuzzy=amphin+cutt",
                    "https://api.scryfall.com/cards/named?fuzzy=dance+dead",

                    "https://api.scryfall.com/cards/named?fuzzy=befriending+moths"
            };

            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL).build();

            for (String url : urls) {

                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
                        .headers("Accept", "application/json").setHeader("User-Agent", "Mozilla/5.0").build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();
                JSONObject json = new JSONObject(responseBody);

                /* *** prueba impresion *** */
                printDetails(json);
            }

            /*System.out.println(response.statusCode());
            System.out.println(json.toString(4));
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

            PixelArt.printPixel("testImg", 3);
            PixelArt.printPixel("https://cards.scryfall.io/art_crop/front/b/c/bce78225-9dbf-46c1-b63d-083c1858eb98.jpg?1680759056", 3);
            PixelArt.printPixel("https://cards.scryfall.io/art_crop/front/d/9/d99a9a7d-d9ca-4c11-80ab-e39d5943a315.jpg?1632831210", 3);
            */



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printDetails(JSONObject json) throws JSONException {

        JSONObject card = json;

        for (int i = 0; i < 2; i++) {
            if (json.has("card_faces"))
                card = json.getJSONArray("card_faces").getJSONObject(i);

            System.out.println("\n_____");

            //basic info
            if (card.has("name"))
                System.out.print(card.getString("name"));
            if (card.has("mana_cost"))
                System.out.println("\t" + card.getString("mana_cost"));
            if (card.has("type_line"))
                System.out.println(card.getString("type_line") + "\n");

            //text
            if (card.has("oracle_text"))
                System.out.println(card.getString("oracle_text"));
            if (card.has("flavor_text"))
                System.out.println("\n" + card.getString("flavor_text"));

            //extras
            if (card.has("power") && card.has("toughness"))
                System.out.println("\n" + card.getString("power") + " / " + card.getString("toughness"));

            if (card.has("defense"))
                System.out.println("\n" + card.getString("defense"));

            if (card.has("loyalty"))
                System.out.println("\n" + card.getString("loyalty"));

            if (card.has("attraction_lights"))
                System.out.println("\n" + card.getJSONArray("attraction_lights"));

            if (card.has("hand_modifier") && card.has("life_modifier")) {
                System.out.println("\n" + card.getString("hand_modifier") + " / " + card.getString("life_modifier"));
            }

            //bottom info
            if (card.has("rarity") && card.has("collector_number") && card.has("released_at"))
                System.out.println("\n" + card.getString("rarity") + " " + card.getString("collector_number") + "\t" + card.getString("released_at"));

            if (card.has("set") && card.has("foil") && card.has("lang") && card.has("artist"))
                System.out.println(card.getString("set") + " " + card.getString("foil") + " " + card.getString("lang") + " " + card.getString("artist"));


            if (!json.has("card_faces"))
                break;
        }
    }
}
