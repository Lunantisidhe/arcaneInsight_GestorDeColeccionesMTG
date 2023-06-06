package com.dam.rgb.visual;

// logo y caracteres unicode empleados en el programa
public class Style {

    public static final String LOGO = """
             
             /\\___/\\   /\\___/\\\s
            ( •   • ) ( o   o )\s
              /   \\     /   \\\s
             /     \\   /     \\\s
              arcane • insight
              
            """;

    public static final String[] LOADING_ANIM = {
            "( •   • ) ",
            "( ◡   ◡ ) ",
            "( o   o ) ",
            "( ◠   ◠ ) "
    };

    public static final char ASCII_PIXEL = '█';
    public static final String HORIZONTAL_BORDER = "═", VERTICAL_BORDER = "║", LIGHT_HORIZONTAL_BORDER = "─";
    public static final String CENTER_LEFT_CONNECTOR = "╟", CENTER_RIGHT_CONNECTOR = "╢";
    public static final String TOP_LEFT_CORNER = "╔", TOP_RIGHT_CORNER = "╗";
    public static final String BOTTOM_LEFT_CORNER = "╚", BOTTOM_RIGHT_CORNER = "╝";
}
