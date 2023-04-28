package com.dam.rgb;

import com.dam.rgb.visual.Printer;
import com.dam.rgb.visual.Style;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ArcaneInsight {

    public static void main(String[] args) {

        System.out.println(Style.LOGO);

        try {
            String[] urls = {
//                    /* types */
//                    //artifact
//                    "aeon+engine",
//                    //battle
                    "invasion+ikoria",
//                    //conspiracy
//                    "power+play",
//                    //creature
//                    "aboroth",
//                    //emblem
//                    "chandra+torch+defiance+emblem",
//                    //enchantment
//                    "alpine+moon",
//                    //hero
//                    "the+destined",
//                    //instant
//                    "absorb",
//                    //land
//                    "access+tunel",
//                    //phenomenon
//                    "interplanar+tunnel",
//                    //plane
//                    "lethe+lake",
//                    //planeswalker
                    "ajani+sleeper+agent",
//                    //scheme
//                    "choose+demise",
//                    //vanguard
//                    "maraxus",
//
//                    /* supertypes */
//                    //basic
//                    "island",
//                    //legendary
//                    "adamaro+first+desire",
//                    //token
//                    "bat",
//
//                    /* subtypes */
//                    //attraction
//                    "bumper+cars",
//                    //contraption
//                    "accesories+murder",
//                    //equipment
//                    "ancestral+katana",
//                    //gold
//                    "gold",
//                    //class
                    "bard+class",
//                    //saga
                    "elspeth+nightmare",
//                    //adventure
//                    "crystal+dragon",
//
//                    /* unique */
//                    //two faced
                    "befriending+moths",
//                    //nightbound
//                    "child+pack",
//                    //meld
//                    "gisela+broken+blade",
//                    "brisela+voice+nightmares",
//                    //leveler
                    "echo+mage",
//                    //split
//                    "odds+ends",
//                    //split with fuse
//                    "breaking+entering",
//                    //split with aftermath
//                    "rags+riches",
//                    //flip
//                    "akki+lavarunner",
//                    //prototype
                    "fallaji+dragon+engine",
//                    //host
//                    "labro+bot",
//                    //augment
//                    "half+squirrel",
//                    //dungeon
                    "dungeon+mad+mage",
//
//                    /* conflictive */
//                    "asmora",
//                    "okina+temple",
                    "amphin+cutt",
//                    "dance+dead"
            };

            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL).build();

            for (String url : urls) {
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://api.scryfall.com/cards/named?fuzzy=" + url))
                        .headers("Accept", "application/json").setHeader("User-Agent", "Mozilla/5.0").build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();
                JSONObject json = new JSONObject(responseBody);

                Printer.printCard(json);
                // System.out.println(json.toString(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
