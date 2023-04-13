package com.dam.rgb.visual;

import com.dam.rgb.visual.enums.Colors;
import org.json.JSONArray;

import java.util.Map;

import static com.dam.rgb.visual.enums.Colors.*;

public class Colorize {
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

    public static void printColorized(String text, Enum<Colors> color) {

        int[] colors = new int[3];

        if (color.equals(WHITE))
            colors = white;
        else if (color.equals(BLUE))
            colors = blue;
        else if (color.equals(BLACK))
            colors = black;
        else if (color.equals(RED))
            colors = red;
        else if (color.equals(GREEN))
            colors = green;
        else if (color.equals(GOLDEN))
            colors = golden;
        else if (color.equals(SILVER))
            colors = silver;
        else if (color.equals(GREY))
            colors = grey;

        //imprime el texto en el color correspondiente
        System.out.print("\033[38;2;" + colors[0] + ";" + colors[1] + ";" + colors[2] + "m" + text);

        //resetea el color del texto de la consola
        System.out.print("\033[38;2;" + 187 + ";" + 187 + ";" + 187 + "m");
    }

    public static Colors chooseColor(JSONArray colorIdentity) { //TODO optz

        if (colorIdentity.toString().length() > 5)
            return GOLDEN;

        else if (colorIdentity.toString().equals("[]"))
            return SILVER;

        else
            return colorCorrespondency.getOrDefault(String.valueOf(colorIdentity.toString().charAt(2)), GREY);
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
                printColorized(manaSymbol, GOLDEN);

            //lo colorea de forma correspondiente
            else
                printColorized(manaSymbol, colorCorrespondency.getOrDefault(String.valueOf(manaSymbol.charAt(1)), SILVER));

            i = endIndex + 1;
        }
    }
}
