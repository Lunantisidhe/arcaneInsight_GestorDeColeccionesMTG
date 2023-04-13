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

                    /* conflictive */
                    "https://api.scryfall.com/cards/named?fuzzy=asmora",
                    "https://api.scryfall.com/cards/named?fuzzy=okina+temple",
                    "https://api.scryfall.com/cards/named?fuzzy=amphin+cutt",
                    "https://api.scryfall.com/cards/named?fuzzy=dance+dead",
            };

            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL).build();

            for (String url : urls) {
                HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
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


    public static void printCard(JSONObject json) throws JSONException {

        JSONObject card = json;

        for (int i = 0; i < 2; i++) {
            if (json.has("card_faces"))
                card = json.getJSONArray("card_faces").getJSONObject(i);

            Colors colorIdentity = chooseColor(json.getJSONArray("color_identity"));

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
            if (card.has("name") && card.has("mana_cost"))
                printJustified(card.getString("name"), card.getString("mana_cost"), colorIdentity);
            if (card.has("type_line"))
                printJustified(card.getString("type_line"), "", colorIdentity);

            printBorder(CENTER, colorIdentity);

            //text
            if (card.has("oracle_text"))
                squishText(card.getString("oracle_text"), colorIdentity);
            if (card.has("flavor_text"))
                squishText(card.getString("flavor_text"), colorIdentity);

            //extras
            if (card.has("power") && card.has("toughness"))
                printJustified("", card.getString("power") + " / "
                        + card.getString("toughness"), colorIdentity);
            if (card.has("defense"))
                printJustified("", card.getString("defense"), colorIdentity);
            if (card.has("loyalty"))
                printJustified("", card.getString("loyalty"), colorIdentity);
            if (card.has("attraction_lights"))
                System.out.println(card.getJSONArray("attraction_lights"));
            if (card.has("hand_modifier") && card.has("life_modifier"))
                System.out.println(card.getString("hand_modifier") + " / " + card.getString("life_modifier"));

            printBorder(CENTER, colorIdentity);

            //bottom info
            if (json.has("rarity") && json.has("collector_number") && json.has("released_at"))
                printJustified(json.getString("rarity").toUpperCase().charAt(0) + " "
                        + json.getString("collector_number"), "™ & © "
                        + LocalDate.parse(json.getString("released_at")).getYear()
                        + " Wizards of the Coast", colorIdentity);
            if (json.has("set") && json.has("foil") && json.has("lang") && json.has("artist"))
                printJustified(json.getString("set").toUpperCase() + " "
                        + foilSymbol(Boolean.parseBoolean(json.getString("foil"))) + " "
                        + json.getString("lang").toUpperCase() + " — "
                        + json.getString("artist"), "", colorIdentity);

            printBorder(BOTTOM, colorIdentity);

            if (!json.has("card_faces"))
                break;
        }
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

        Colorize.printColorized(VERTICAL_BORDER, color);
        System.out.print(" " + left);

        for(int i = 0; i < spaces; i++)
            System.out.print(" ");

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

    public static String foilSymbol(boolean foil) {
        return foil ? "*" : "•";
    }

    public static void colorManaSymbol(String manaCost) {

        //busca el inicio del simbolo de mana
        int i = 0;
        while (i < manaCost.length()) {
            if (manaCost.charAt(i) == '{') {
                int end = manaCost.indexOf('}', i + 1);
                String manaSymbol = manaCost.substring(i, end + 1);

                //mana partido
                if (manaSymbol.contains("/"))
                    Colorize.printColorized(manaSymbol, GOLDEN);

                //lo colorea de forma correspondiente
                else
                    Colorize.printColorized(manaSymbol, colorCorrespondency.getOrDefault(String.valueOf(manaSymbol.charAt(1)), SILVER));

                i = end + 1;

            } else
                i++;
        }
    }
}
