package com.dam.rgb.visual;

import com.dam.rgb.visual.enums.Colors;
import com.dam.rgb.visual.enums.Position;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

import static com.dam.rgb.visual.Style.*;
import static com.dam.rgb.visual.enums.Position.*;

public class Printer {

    public static void printCard(JSONObject json) {
        try {
            Colors colorIdentity = Colorize.chooseColor(json.getJSONArray("color_identity"));

            if (!json.optString("card_faces").isEmpty()) {
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
        /*try {
            String artCrop = !card.optString("image_uris").isEmpty()
                    ? card.getJSONObject("image_uris").optString("art_crop")
                    : !json.optString("image_uris").isEmpty()
                    ? json.getJSONObject("image_uris").optString("art_crop") : null;
            if (artCrop != null)
                PixelArt.printPixel(artCrop, 3);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        printBorder(TOP, colorIdentity);

        //basic info
        printJustified(card.optString("name"), card.optString("mana_cost"), colorIdentity, false);
        printJustified(card.optString("type_line"), "", colorIdentity, false);

        printBorder(CENTER, colorIdentity);

        //text
        squishText(card.optString("oracle_text"), colorIdentity, false);
        if (!card.optString("oracle_text").isEmpty() && !card.optString("flavor_text").isEmpty())
            printJustified("", "", colorIdentity, false);
        squishText(card.optString("flavor_text"), colorIdentity, true);

        //extras
        String power = card.optString("power");
        String toughness = card.optString("toughness");
        String defense = card.optString("defense");
        String loyalty = card.optString("loyalty");

        if (!power.isEmpty() && !toughness.isEmpty())
            printJustified("", power + " / " + toughness, colorIdentity, false);
        else if (!defense.isEmpty())
            printJustified("", defense, colorIdentity, false);
        else if (!loyalty.isEmpty())
            printJustified("", loyalty, colorIdentity, false);

        String attractionLights = card.optString("attraction_lights");
        String handModifier = card.optString("hand_modifier");
        String lifeModifier = card.optString("life_modifier");

        if (!attractionLights.isEmpty())
            printJustified("", attractionLights, colorIdentity, false);
        if (!handModifier.isEmpty() && !lifeModifier.isEmpty())
            printJustified("", handModifier + " / " + lifeModifier, colorIdentity, false);

        printBorder(CENTER, colorIdentity);

        //bottom info
        String rarity = json.optString("rarity");
        String collectorNumber = json.optString("collector_number");
        String releasedAt = json.optString("released_at");

        if (!rarity.isEmpty() && !collectorNumber.isEmpty() && !releasedAt.isEmpty()) {
            int year = LocalDate.parse(releasedAt).getYear();
            String collectorInfo = rarity.toUpperCase().charAt(0) + " " + collectorNumber;
            String copyright = "™ & © " + year + " Wizards of the Coast";
            printJustified(collectorInfo, copyright, colorIdentity, false);
        }

        String set = json.optString("set");
        String foil = json.optString("foil");
        String lang = json.optString("lang");
        String artist = json.optString("artist");

        if (!set.isEmpty() && !foil.isEmpty() && !lang.isEmpty() && !artist.isEmpty()) {
            String setInfo = set.toUpperCase() + " " + (Boolean.parseBoolean(foil) ? "*" : "•") + " " + lang.toUpperCase();
            printJustified(setInfo + " — " + artist, "", colorIdentity, false);
        }

        printBorder(BOTTOM, colorIdentity);
    }

    public static void printBorder(Position position, Colors color) {

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

    public static void printJustified (String left, String right, Colors color, boolean italic) {

        int spaces = (CARD_WIDTH - 4) - left.length() - right.length();
        String spacing = " ".repeat(spaces);

        Colorize.printColorized(VERTICAL_BORDER, color);
        System.out.print(italic ? " \033[3m" : " ");
        System.out.print(left + "\033[0m" + spacing);


        //impresion simbolos de mana
        if (right.contains("}")) {
            Colorize.colorManaSymbol(right);
            Colorize.printColorized(" " + VERTICAL_BORDER + "\n", color);

        } else {
            System.out.print(right + " ");
            Colorize.printColorized(VERTICAL_BORDER + "\n", color);
        }
    }

    public static void squishText(String textBlock, Colors color, boolean italic) {

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

                printJustified(text.substring(start, end).trim(), "", color, italic);
                start = end;
            }
        }
    }
}
