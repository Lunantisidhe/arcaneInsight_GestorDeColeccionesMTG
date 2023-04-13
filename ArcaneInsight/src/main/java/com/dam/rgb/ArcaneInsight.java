package com.dam.rgb;

import com.dam.rgb.visual.Colorize;
import com.dam.rgb.visual.enums.Colors;
import com.dam.rgb.visual.enums.Position;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.InputMismatchException;

import static com.dam.rgb.visual.Colorize.colorCorrespondency;
import static com.dam.rgb.visual.Style.*;
import static com.dam.rgb.visual.enums.Colors.GOLDEN;
import static com.dam.rgb.visual.enums.Colors.SILVER;
import static com.dam.rgb.visual.enums.Position.*;

public class ArcaneInsight {

    public static void main(String[] args) {

        System.out.println(LOGO);

        try {
            String[] urls = {
                    /* types */
                    //artifact
                    "aeon+engine",
                    //battle
                    "invasion+ikoria",
                    //conspiracy
                    "power+play",
                    //creature
                    "aboroth",
                    //emblem
                    "chandra+torch+defiance+emblem",
                    //enchantment
                    "alpine+moon",
                    //hero
                    "the+destined",
                    //instant
                    "absorb",
                    //land
                    "access+tunel",
                    //phenomenon
                    "interplanar+tunnel",
                    //plane
                    "lethe+lake",
                    //planeswalker
                    "ajani+sleeper+agent",
                    //scheme
                    "choose+demise",
                    //vanguard
                    "maraxus",

                    /* supertypes */
                    //basic
                    "island",
                    //legendary
                    "adamaro+first+desire",
                    //token
                    "bat",

                    /* subtypes */
                    //attraction
                    "bumper+cars",
                    //contraption
                    "accesories+murder",
                    //equipment
                    "ancestral+katana",
                    //gold
                    "gold",
                    //class
                    "bard+class",
                    //saga
                    "elspeth+nightmare",
                    //adventure
                    "crystal+dragon",

                    /* unique */
                    //two faced
                    "befriending+moths",
                    //nightbound
                    "child+pack",
                    //meld
                    "gisela+broken+blade",
                    "brisela+voice+nightmares",
                    //leveler
                    "echo+mage",
                    //split
                    "odds+ends",
                    //split with fuse
                    "breaking+entering",
                    //split with aftermath
                    "rags+riches",
                    //flip
                    "akki+lavarunner",
                    //prototype
                    "fallaji+dragon+engine",
                    //host
                    "labro+bot",
                    //augment
                    "half+squirrel",
                    //dungeon
                    "dungeon+mad+mage",

                    /* conflictive */
                    "asmora",
                    "okina+temple",
                    "amphin+cutt",
                    "dance+dead",
            };

            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL).build();

            for (String url : urls) {
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://api.scryfall.com/cards/named?fuzzy=" + url))
                        .headers("Accept", "application/json").setHeader("User-Agent", "Mozilla/5.0").build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();
                JSONObject json = new JSONObject(responseBody);

                printCard(json);
                //System.out.println(json.toString(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printCard(JSONObject json) {
        try {
            Colors colorIdentity = chooseColor(json.getJSONArray("color_identity"));

            if (json.has("card_faces")) {
                JSONArray cardFaces = json.getJSONArray("card_faces");

                for (int i = 0; i < 2; i++) {
                    JSONObject card = cardFaces.getJSONObject(i);
                    printCardFace(json, card, colorIdentity);
                }

            } else
                printCardFace(json, json, colorIdentity);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void printCardFace(JSONObject json, JSONObject card, Colors colorIdentity) {

        //imagen
        /* if (card.has("image_uris")) {
            if (card.getJSONObject("image_uris").has("art_crop"))
                PixelArt.printPixel(card.getJSONObject("image_uris").getString("art_crop"), 3);
        } else if (json.has("image_uris")) {
            if (json.getJSONObject("image_uris").has("art_crop"))
                PixelArt.printPixel(json.getJSONObject("image_uris").getString("art_crop"), 3);
        } */
        
        printBorder(TOP, colorIdentity);

        //basic info
        printJustified(card.optString("name"), card.optString("mana_cost"), colorIdentity);
        printJustified(card.optString("type_line"), "", colorIdentity);

        printBorder(CENTER, colorIdentity);

        //text
        squishText(card.optString("oracle_text"), colorIdentity);
        squishText(card.optString("flavor_text"), colorIdentity);

        //extras
        String power = card.optString("power");
        String toughness = card.optString("toughness");
        String defense = card.optString("defense");
        String loyalty = card.optString("loyalty");

        if (!power.isEmpty() && !toughness.isEmpty())
            printJustified("", power + " / " + toughness, colorIdentity);
        else if (!defense.isEmpty())
            printJustified("", defense, colorIdentity);
        else if (!loyalty.isEmpty())
            printJustified("", loyalty, colorIdentity);

        String attractionLights = card.optString("attraction_lights");
        String handModifier = card.optString("hand_modifier");
        String lifeModifier = card.optString("life_modifier");

        if (!attractionLights.isEmpty())
            printJustified("", attractionLights, colorIdentity);
        if (!handModifier.isEmpty() && !lifeModifier.isEmpty())
            printJustified("", handModifier + " / " + lifeModifier, colorIdentity);

        printBorder(CENTER, colorIdentity);

        //bottom info
        String rarity = json.optString("rarity");
        String collectorNumber = json.optString("collector_number");
        String releasedAt = json.optString("released_at");

        if (!rarity.isEmpty() && !collectorNumber.isEmpty() && !releasedAt.isEmpty()) {
            int year = LocalDate.parse(releasedAt).getYear();
            String collectorInfo = rarity.toUpperCase().charAt(0) + " " + collectorNumber;
            String copyright = "™ & © " + year + " Wizards of the Coast";
            printJustified(collectorInfo, copyright, colorIdentity);
        }

        String set = json.optString("set");
        String foil = json.optString("foil");
        String lang = json.optString("lang");
        String artist = json.optString("artist");

        if (!set.isEmpty() && !foil.isEmpty() && !lang.isEmpty() && !artist.isEmpty()) {
            String setInfo = set.toUpperCase() + " " + (Boolean.parseBoolean(foil) ? "*" : "•") + " " + lang.toUpperCase();
            printJustified(setInfo + " — " + artist, "", colorIdentity);
        }

        printBorder(BOTTOM, colorIdentity);
    }


    public static void printBorder(Enum<Position> position, Colors color) {

        String left = null, center = null, right = null;

        if (position == TOP) {
            left = TOP_LEFT_CORNER;
            center = HORIZONTAL_BORDER;
            right = TOP_RIGHT_CORNER;

        } else if (position == CENTER) {
            left = CENTER_LEFT_CONNECTOR;
            center = LIGHT_HORIZONTAL_BORDER;
            right = CENTER_RIGHT_CONNECTOR;

        } else if (position == BOTTOM) {
            left = BOTTOM_LEFT_CORNER;
            center = HORIZONTAL_BORDER;
            right = BOTTOM_RIGHT_CORNER;
        }

        Colorize.printColorized(left, color);

        for (int i = 0; i < (CARD_WIDTH - 2); i++)
            Colorize.printColorized(center, color);

        Colorize.printColorized(right + "\n", color);

    }

    public static void printJustified (String left, String right, Colors color) {

        int spaces = (CARD_WIDTH - 4) - left.length() - right.length();
        String spacing = " ".repeat(spaces);

        Colorize.printColorized(VERTICAL_BORDER, color);
        System.out.print(" " + left + spacing);

        //impresion simbolos de mana
        if (right.contains("}")) {
            colorManaSymbol(right);
            Colorize.printColorized(" " + VERTICAL_BORDER + "\n", color);

        } else {
            System.out.print(right + " ");
            Colorize.printColorized(VERTICAL_BORDER + "\n", color);
        }
    }

    public static void squishText(String textBlock, Colors color) {

        String[] splitText = textBlock.split("\n");

        for (String text : splitText) {
            int start = 0, end = 0;

            //calcula mientras no se termine el texto
            while (end < text.length()) {

                //calcula el segmento que entra en los caracteres establecidos
                end = Math.min(start + (CARD_WIDTH - 4), text.length());

                //ajusta el final si se va a cortar a la mitad una palabra
                if (end < text.length() && !Character.isWhitespace(text.charAt(end))) {
                    int lastSpace = text.substring(start, end).lastIndexOf(' ');
                    if (lastSpace != -1)
                        end = start + lastSpace;
                }

                printJustified(text.substring(start, end).trim(), "", color);
                start = end;
            }
        }
    }

    public static Colors chooseColor(JSONArray colorIdentity) {

        if (colorIdentity.toString().equals("[]"))
            return SILVER;

        else
            return colorCorrespondency.getOrDefault(String.valueOf(colorIdentity.toString().charAt(2)), GOLDEN);
    }

    public static void colorManaSymbol(String manaCost) {

        int i = 0;
        while (i < manaCost.length()) {
            int startIndex = manaCost.indexOf('{', i);
            if (startIndex == -1)
                break;

            int endIndex = manaCost.indexOf('}', startIndex + 1);
            if (endIndex == -1)
                break;

            //aisla el simbolo de mana
            String manaSymbol = manaCost.substring(startIndex, endIndex + 1);

            //mana partido
            if (manaSymbol.contains("/"))
                Colorize.printColorized(manaSymbol, GOLDEN);

            //lo colorea de forma correspondiente
            else
                Colorize.printColorized(manaSymbol, colorCorrespondency.getOrDefault(String.valueOf(manaSymbol.charAt(1)), SILVER));

            i = endIndex + 1;
        }
    }
}
