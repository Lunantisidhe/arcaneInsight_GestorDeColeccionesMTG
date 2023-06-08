package com.dam.rgb.visual;

import com.dam.rgb.utilities.ColorEnum;
import org.json.JSONArray;

import java.util.Map;

public class Colorizer {

    public static final Map<String, ColorEnum> colorCorrespondency = Map.of(
            "W", ColorEnum.WHITE,
            "U", ColorEnum.BLUE,
            "B", ColorEnum.BLACK,
            "R", ColorEnum.RED,
            "G", ColorEnum.GREEN
    );
    // valores rgb de los colores empleados
    private static final int[] WHITE = {241, 235, 221};
    private static final int[] BLUE = {37, 150, 190};
    private static final int[] BLACK = {85, 61, 125};
    private static final int[] RED = {227, 60, 34};
    private static final int[] GREEN = {9, 106, 64};
    private static final int[] GOLDEN = {217, 173, 61};
    private static final int[] SILVER = {205, 210, 217};
    private static final int[] GREY = {187, 187, 187};

    // imprime texto en el color seleccionado
    public static void printColorized(String text, ColorEnum color) {

        int[] colorValues = new int[3];

        if (color.equals(ColorEnum.WHITE))
            colorValues = WHITE;
        else if (color.equals(ColorEnum.BLUE))
            colorValues = BLUE;
        else if (color.equals(ColorEnum.BLACK))
            colorValues = BLACK;
        else if (color.equals(ColorEnum.RED))
            colorValues = RED;
        else if (color.equals(ColorEnum.GREEN))
            colorValues = GREEN;
        else if (color.equals(ColorEnum.GOLDEN))
            colorValues = GOLDEN;
        else if (color.equals(ColorEnum.SILVER))
            colorValues = SILVER;
        else if (color.equals(ColorEnum.GREY))
            colorValues = GREY;

        // imprime el texto en el color correspondiente y resetea el color del texto de la consola
        System.out.print("\033[38;2;" + colorValues[0] + ";" + colorValues[1] + ";" + colorValues[2] + "m" + text + "\033[0m");
    }

    // a partir de la identidad de color de una carta, devuelve sus colores correspondientes
    public static ColorEnum[] chooseColor(JSONArray colorIdentity) {

        // carta incolora
        if (colorIdentity.toString().equals("[]"))
            return new ColorEnum[]{
                    ColorEnum.SILVER
            };

        // carta tricolor+
        else if (colorIdentity.toString().length() >= 13)
            return new ColorEnum[]{
                    ColorEnum.GOLDEN
            };

        // carta bicolor
        else if (colorIdentity.toString().length() == 9)
            return new ColorEnum[]{
                    colorCorrespondency.getOrDefault(String.valueOf(colorIdentity.toString().charAt(2)), ColorEnum.SILVER),
                    colorCorrespondency.getOrDefault(String.valueOf(colorIdentity.toString().charAt(6)), ColorEnum.SILVER)
            };

        // carta monocolor
        else
            return new ColorEnum[]{
                    colorCorrespondency.getOrDefault(String.valueOf(colorIdentity.toString().charAt(2)), ColorEnum.SILVER)
            };
    }

    // imprime simbolos de mana coloreados
    public static void printTextWithColoredManaSymbols(String text) {

        int i = 0;
        while (i < text.length()) {
            int startIndex = text.indexOf('{', i);
            if (startIndex == -1) {
                // si no quedan mas simbolos de mana, termina de imprimir el texto
                printColorized(text.substring(i), ColorEnum.GREY);
                break;
            }

            // imprime el texto entre la posicion actual y el simbolo de mana
            if (startIndex > i)
                printColorized(text.substring(i, startIndex), ColorEnum.GREY);

            int endIndex = text.indexOf('}', startIndex + 1);
            if (endIndex == -1) {
                // si no quedan mas simbolos de mana, termina de imprimir el texto
                printColorized(text.substring(i), ColorEnum.GREY);
                break;
            }

            // aisla el simbolo de mana
            String manaSymbol = text.substring(startIndex, endIndex + 1);

            // mana partido
            if (manaSymbol.contains("/")) {
                // separa los simbolos para colorearlos por separado
                String[] splitSymbols = manaSymbol.substring(1, manaSymbol.length() - 1).split("/");
                printColorized("{", ColorEnum.GOLDEN);

                for (String splitSymbol : splitSymbols) {
                    printColorized(splitSymbol, colorCorrespondency.getOrDefault(splitSymbol, ColorEnum.SILVER));

                    if (!splitSymbol.equals(splitSymbols[splitSymbols.length - 1]))
                        printColorized("/", ColorEnum.GOLDEN);
                }

                printColorized("}", ColorEnum.GOLDEN);
            }

            // lo colorea de forma correspondiente
            else
                printColorized(manaSymbol, colorCorrespondency.getOrDefault(String.valueOf(manaSymbol.charAt(1)), ColorEnum.SILVER));

            i = endIndex + 1;
        }
    }
}
