package com.dam.rgb.visual;

import com.dam.rgb.visual.enums.Colors;

import static com.dam.rgb.visual.enums.Colors.*;

public class Colorize {
    public static int[] white = {255, 255, 255}, blue = {0, 0, 255}, black = {0, 0, 0}, red = {255, 0, 0}, green = {0, 255, 0};
    public static int[] golden = {218, 165, 32}, silver = {187, 187, 187};

    public static void printColorized(String text, Enum<Colors> color) {

        int[] colors = new int[3];

        if (WHITE.equals(color))
            colors = white;
        else if (BLUE.equals(color))
            colors = blue;
        else if (BLACK.equals(color))
            colors = black;
        else if (RED.equals(color))
            colors = red;
        else if (GREEN.equals(color))
            colors = green;
        else if (GOLDEN.equals(color))
            colors = golden;
        else if (SILVER.equals(color))
            colors = silver;

        //imprime el texto en el color correspondiente
        System.out.print("\033[38;2;" + colors[0] + ";" + colors[1] + ";" + colors[2] + "m" + text);

        //resetea el color del texto de la consola
        System.out.print("\033[38;2;" + 187 + ";" + 187 + ";" + 187 + "m");
    }
}
