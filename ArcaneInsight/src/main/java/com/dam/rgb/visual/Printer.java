package com.dam.rgb.visual;

import com.dam.rgb.visual.enums.BorderType;
import com.dam.rgb.visual.enums.Colors;
import com.dam.rgb.visual.enums.Position;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.dam.rgb.visual.Style.*;
import static com.dam.rgb.visual.enums.Position.*;

public class Printer {

    public static final int CARD_WIDTH = 60;

    public static void printCard(JSONObject json) {
        try {
            // elige los colores segun la identidad de color
            Colors[] colorIdentity = Colorizer.chooseColor(json.getJSONArray("color_identity"));

            // imprime cartas de doble cara
            if (!json.optString("card_faces").isEmpty()) {
                JSONArray cardFaces = json.getJSONArray("card_faces");

                for (int i = 0; i < 2; i++) {
                    JSONObject card = cardFaces.getJSONObject(i);
                    // printCardArt(json, card);
                    printCardFace(json, card, colorIdentity, (i == 0 ? BorderType.TOP_HALF : BorderType.BOTTOM_HALF));
                }

            // imprime cartas de una sola cara
            } else {
                // printCardArt(json, json);
                printCardFace(json, json, colorIdentity, BorderType.COMPLETE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // imprime el arte de una carta
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

    // imprime una cara de una carta
    private static void printCardFace(JSONObject json, JSONObject card, Colors[] colorIdentity, BorderType borderType) {

        if (borderType != BorderType.BOTTOM_HALF)
            printBorder(TOP, colorIdentity);

        // informacion basica
        printJustified(card.optString("name"), card.optString("mana_cost"), colorIdentity, false);
        printJustified(card.optString("type_line"), "", colorIdentity, false);

        printBorder(CENTER, colorIdentity);

        // textos
        squishText(card, card.optString("oracle_text"), colorIdentity, false);
        if (!card.optString("oracle_text").isEmpty() && !card.optString("flavor_text").isEmpty())
            printJustified("", "", colorIdentity, false);
        squishText(card, card.optString("flavor_text"), colorIdentity, true);

        // extras
        String power = card.optString("power");
        String toughness = card.optString("toughness");
        String defense = card.optString("defense");
        String loyalty = card.optString("loyalty");

        if (!power.isEmpty() && !toughness.isEmpty())
            if (!card.optString("oracle_text").contains("Level up"))
                printJustified("", power + " / " + toughness, colorIdentity, false);
        else if (!defense.isEmpty())
            printJustified("", defense, colorIdentity, false);
        else if (!loyalty.isEmpty())
            printJustified("", loyalty, colorIdentity, false);

        String attractionLights = card.optString("attraction_lights");
        String handModifier = card.optString("hand_modifier");
        String lifeModifier = card.optString("life_modifier");

        if (!attractionLights.isEmpty())
            printJustified("", "Attraction lights "
                    + attractionLights.replaceAll("[\\[\\]]", "").replace(",", ", "),
                    colorIdentity, false);

        if (!handModifier.isEmpty() && !lifeModifier.isEmpty()) {
            printJustified("", "", colorIdentity, false);
            printJustified("Hand size " + handModifier, "Starting life " + lifeModifier, colorIdentity, false);
        }

        printBorder(CENTER, colorIdentity);

        // informacion pie
        if (borderType != BorderType.TOP_HALF) {
            String rarity = json.optString("rarity");
            String collectorNumber = json.optString("collector_number").replace("★", "*");
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

    // imprime los bordes de la carta en los colores elegidos
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

    // imprime textos justificados a la izquierda/derecha
    public static void printJustified(String left, String right, Colors[] colors, boolean italic) {

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

    // imprime un texto restringiendolo a un numero de caracteres
    public static void squishText(JSONObject card, String textBlock, Colors[] colors, boolean italic) {

        String cardType = card.optString("type_line");
        String[] splitTextArr = textBlock.split("\n");
        ArrayList<String> splitText = new ArrayList<>();
        ArrayList<String> levelUpStats = new ArrayList<>();
        int levelUpCount = 0;

        // añadido espaciados
        for (int i = 0; i < splitTextArr.length; i++) {
            String line = splitTextArr[i];

            // espaciado fases sagas, habilidades lealtad planeswalkers, niveles clases y habitaciones dungeons
            if (
                    (cardType.contains("Saga") && line.startsWith("I")) ||
                    (cardType.contains("Planeswalker") && i != 0) ||
                    (cardType.contains("Class") && line.startsWith("{")) ||
                    (cardType.contains("Dungeon") && i != 0) ||
                    (cardType.contains("Creature") && line.startsWith("LEVEL")))
                splitText.add(" ");

            // fuerza y resistencia criaturas con level up
            if (cardType.contains("Creature") && splitTextArr[0].startsWith("Level up") && i != 0
                    && splitTextArr[i - 1].startsWith("LEVEL"))
                levelUpStats.add(line.replace("/", " / "));
            else
                splitText.add(line);

            // espaciado niveles clases y criaturas con prototype
            if (
                    (cardType.contains("Class") && line.endsWith(")")) ||
                    (cardType.contains("Creature") && line.startsWith("Prototype")))
                splitText.add(" ");
        }


        for (int i = 0; i < splitText.size(); i++) {

            String text = splitText.get(i);
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


                // impresion
                String trim = text.substring(start, end).trim();

                // fuerza y resistencia iniciales en cartas con level up
                if (i == 0 && end >= text.length() && splitText.get(i + 2).startsWith("LEVEL"))
                    printJustified(trim, card.optString("power") + " / " + card.optString("toughness"),
                            colors, italic);

                // fuerza y resistencia segun niveles en cartas con level up
                else if (trim.startsWith("LEVEL")) {
                    printJustified(trim, levelUpStats.get(levelUpCount), colors, italic);
                    levelUpCount++;

                // impresion regular
                } else
                    printJustified(trim, "", colors, italic);

                start = end;
            }
        }
    }
}
