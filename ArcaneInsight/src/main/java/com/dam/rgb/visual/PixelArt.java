package com.dam.rgb.visual;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PixelArt {

    private static final char ASCII_PIXEL = '█';

    // imprime imagenes en consola empleando caracteres unicode
    public static void printPixel(String fileName, int reductionRatio) {
        try {

            BufferedImage sourceImg;
            if (fileName.startsWith("http"))
                sourceImg = ImageIO.read(new URL(fileName + ".jpg"));
            else
                sourceImg = ImageIO.read(new File(fileName + ".jpg"));

            // reduce el tamaño de la imagen
            int width = sourceImg.getWidth() / reductionRatio;
            int height = sourceImg.getHeight() / (reductionRatio * 3);
            BufferedImage resizedImg = resizeImage(sourceImg, width, height);

            // imprime los pixeles ascii coloreados
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    Color color = new Color(resizedImg.getRGB(x, y));
                    printColoredPixel(color);
                }
                System.out.println();
            }

        } catch (IOException e) {
            System.err.println("Error: error de impresión.");
        }
    }

    // redimensiona imagenes
    private static BufferedImage resizeImage(BufferedImage sourceImg, int width, int height) {

        // crea una nueva imagen
        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImg.createGraphics();

        // configura la interpolacion
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // redimensiona la imagen
        graphics.drawImage(sourceImg, 0, 0, width, height, null);
        graphics.dispose();

        return resizedImg;
    }

    // imprime pixeles coloreados
    private static void printColoredPixel(Color color) {

        // imprime los pixeles coloreados y resetea el color de la consola
        System.out.print("\033[38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue()
                + "m" + ASCII_PIXEL + "\033[0m");
    }
}
