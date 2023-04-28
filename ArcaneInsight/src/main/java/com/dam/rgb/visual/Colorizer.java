package com.dam.rgb.visual;

import com.dam.rgb.visual.enums.Colors;
import org.json.JSONArray;

import java.util.Map;

import static com.dam.rgb.visual.enums.Colors.*;

public class Colorizer {
    public static int[] white = {241, 235, 221}, blue = {37, 150, 190}, black = {85,61,125},
                        red = {227, 60, 34}, green = {9, 106, 64};
    public static int[] golden = {217, 173, 61}, silver = {205, 210, 217}, grey = {187, 187, 187};

    public static Map<String, Colors> colorCorrespondency = Map.of(
            "W", WHITE,
            "U", BLUE,
            "B", BLACK,
            "R", RED,
            "G", GREEN
    );

    public static void printColorized(String text, Colors color) {

        int[] colorValues = new int[3];

        if (color.equals(WHITE))
            colorValues = white;
        else if (color.equals(BLUE))
            colorValues = blue;
        else if (color.equals(BLACK))
            colorValues = black;
        else if (color.equals(RED))
            colorValues = red;
        else if (color.equals(GREEN))
            colorValues = green;
        else if (color.equals(GOLDEN))
            colorValues = golden;
        else if (color.equals(SILVER))
            colorValues = silver;
        else if (color.equals(GREY))
            colorValues = grey;

        //imprime el texto en el color correspondiente y resetea el color del texto de la consola
        System.out.print("\033[38;2;" + colorValues[0] + ";" + colorValues[1] + ";" + colorValues[2] + "m" + text + "\033[0m");
    }

    public static Colors[] chooseColor(JSONArray colorIdentity) {

        //carta tricolor
        if (colorIdentity.toString().length() >= 13)
            return new Colors[]{GOLDEN};

        //carta bicolor
        else if (colorIdentity.toString().length() == 9)
            return new Colors[]{
                    colorCorrespondency.getOrDefault(String.valueOf(colorIdentity.toString().charAt(2)), SILVER),
                    colorCorrespondency.getOrDefault(String.valueOf(colorIdentity.toString().charAt(6)), SILVER)};

        //carta incolora
        else if (colorIdentity.toString().equals("[]"))
            return new Colors[]{SILVER};

        //carta monocolor
        else
            return new Colors[]{colorCorrespondency.getOrDefault(String.valueOf(colorIdentity.toString().charAt(2)), SILVER)};
    }

    public static void printTextWithColoredManaSymbols(String text) {

        int i = 0;
        while (i < text.length()) {
            int startIndex = text.indexOf('{', i);
            if (startIndex == -1) {
                //si no quedan mas simbolos de mana, termina de imprimir el texto
                printColorized(text.substring(i), GREY);
                break;
            }

            //imprime el texto entre la posicion actual y el simbolo de mana
            if (startIndex > i)
                printColorized(text.substring(i, startIndex), GREY);

            int endIndex = text.indexOf('}', startIndex + 1);
            if (endIndex == -1) {
                //si no quedan mas simbolos de mana, termina de imprimir el texto
                printColorized(text.substring(i), GREY);
                break;
            }

            //aisla el simbolo de mana
            String manaSymbol = text.substring(startIndex, endIndex + 1);

            //mana partido
            if (manaSymbol.contains("/")) {
                //separa los simbolos para colorearlos por separado
                String[] splitSymbols = manaSymbol.substring(1, manaSymbol.length() - 1).split("/");
                printColorized("{", GOLDEN);

                for (String splitSymbol : splitSymbols) {
                    printColorized(splitSymbol, colorCorrespondency.getOrDefault(splitSymbol, SILVER));

                    if (!splitSymbol.equals(splitSymbols[splitSymbols.length - 1]))
                        printColorized("/", GOLDEN);
                }

                printColorized("}", GOLDEN);
            }

            //lo colorea de forma correspondiente
            else
                printColorized(manaSymbol, colorCorrespondency.getOrDefault(String.valueOf(manaSymbol.charAt(1)), SILVER));

            i = endIndex + 1;
        }
    }
}
