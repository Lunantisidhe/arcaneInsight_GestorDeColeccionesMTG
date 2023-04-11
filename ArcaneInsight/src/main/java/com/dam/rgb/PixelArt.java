package com.dam.rgb;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PixelArt {

    private static final char ASCII_PIXEL = '█';

    public static void printPixel(String fileName, int reductionRatio) {
        try {

            //carga la imagen
            BufferedImage sourceImg;
            if (fileName.startsWith("http"))
                sourceImg = ImageIO.read(new URL(fileName + ".jpg"));
            else
                sourceImg = ImageIO.read(new File(fileName + ".jpg"));

            //aumenta el tamaño de la imagen
            int width = sourceImg.getWidth() / reductionRatio;
            int height = sourceImg.getHeight() / 3 / reductionRatio;

            BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImg.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(sourceImg, 0, 0, width, height, null);
            graphics.dispose();

            //convierte la imagen a pixeles ascii coloreados
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //calcula el color del bloque y lo imprime
                    Color color = new Color(resizedImg.getRGB(x, y));
                    System.out.print("\033[38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m" + ASCII_PIXEL);
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
