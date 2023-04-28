package com.dam.rgb.visual;

import com.dam.rgb.visual.enums.BorderType;
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
            // pick color identity
            Colors[] colorIdentity = Colorizer.chooseColor(json.getJSONArray("color_identity"));

            // print two faced cards
            if (!json.optString("card_faces").isEmpty()) {
                JSONArray cardFaces = json.getJSONArray("card_faces");

                for (int i = 0; i < 2; i++) {
                    JSONObject card = cardFaces.getJSONObject(i);
                    // printCardArt(json, card);
                    printCardFace(json, card, colorIdentity, (i == 0 ? BorderType.TOP_HALF : BorderType.BOTTOM_HALF));
                }

            // print mono faced cards
            } else {
                // printCardArt(json, json);
                printCardFace(json, json, colorIdentity, BorderType.COMPLETE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void printCardArt(JSONObject json, JSONObject card) {
        try {
            String artCrop = !card.optString("image_uris").isEmpty()
                    ? card.getJSONObject("image_uris").optString("art_crop")
                    : (!json.optString("image_uris").isEmpty()
                    ? json.getJSONObject("image_uris").optString("art_crop") : null);
            if (artCrop != null)
                PixelArt.printPixel(artCrop, 3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void printCardFace(JSONObject json, JSONObject card, Colors[] colorIdentity, BorderType borderType) {

        if (borderType != BorderType.BOTTOM_HALF)
            printBorder(TOP, colorIdentity);

        // basic info
        printJustified(card.optString("name"), card.optString("mana_cost"), colorIdentity, false);
        printJustified(card.optString("type_line"), "", colorIdentity, false);

        printBorder(CENTER, colorIdentity);

        // text
        squishText(card.optString("oracle_text"), colorIdentity, false);
        if (!card.optString("oracle_text").isEmpty() && !card.optString("flavor_text").isEmpty())
            printJustified("", "", colorIdentity, false);
        squishText(card.optString("flavor_text"), colorIdentity, true);

        // extras
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

        // bottom info
        if (borderType != BorderType.TOP_HALF) {
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

            if (!set.isEmpty() && !foil.isEmpty() && !lang.isEmpty()) {
                String setInfo = set.toUpperCase() + " " + (Boolean.parseBoolean(foil) ? "*" : "•") + " " + lang.toUpperCase();
                printJustified(setInfo + (!artist.isEmpty() ? " — " + artist : ""), "", colorIdentity, false);
            }

            printBorder(BOTTOM, colorIdentity);
        }
    }

    public static void printBorder(Position position, Colors[] colors) {

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

        Colorizer.printColorized(left, colors[0]);

        for (int i = 0; i < (CARD_WIDTH - 2); i++)
            Colorizer.printColorized(center, colors.length > 1 && i >= (CARD_WIDTH - 2) / 2 ? colors[1] : colors[0]);

        Colorizer.printColorized(right + "\n", colors.length > 1 ? colors[1] : colors[0]);

    }

    public static void printJustified (String left, String right, Colors[] colors, boolean italic) {

        int spaces = (CARD_WIDTH - 4) - left.length() - right.length();
        String spacing = " ".repeat(spaces);

        // impresion izquierda
        Colorizer.printColorized(VERTICAL_BORDER, colors[0]);
        System.out.print(italic ? " \033[3m" : " ");
        Colorizer.printTextWithColoredManaSymbols(left + "\033[0m" + spacing);

        // impresion derecha
        Colorizer.printTextWithColoredManaSymbols(right);
        Colorizer.printColorized(" " + VERTICAL_BORDER + "\n", colors.length > 1 ? colors[1] : colors[0]);
    }

    public static void squishText(String textBlock, Colors[] colors, boolean italic) {

        String[] splitText = textBlock.split("\n");

        for (String text : splitText) {
            int start = 0, end = 0;

            // calcula mientras no se termine el texto
            while (end < text.length()) {

                // calcula el segmento que entra en los caracteres establecidos
                end = Math.min(start + (CARD_WIDTH - 4), text.length());

                // ajusta el final si se va a cortar a la mitad una palabra
                if (end < text.length() && !Character.isWhitespace(text.charAt(end))) {
                    int lastSpace = text.substring(start, end).lastIndexOf(' ');
                    if (lastSpace != -1)
                        end = start + lastSpace;
                }

                printJustified(text.substring(start, end).trim(), "", colors, italic);
                start = end;
            }
        }
    }
}
