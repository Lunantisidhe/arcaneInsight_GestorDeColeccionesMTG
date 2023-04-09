package com.dam.rgb;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PixelArt {

    private static final char ASCII_PIXEL = '█';
    private static final int scale = 2;
    private static final int BLOCK_WIDTH = 5;
    private static final int BLOCK_HEIGHT = 15;

    public static void main(String[] args) {
        try {

            //carga la imagen
            BufferedImage sourceImg = ImageIO.read(new File("testImg.jpg"));

            //aumenta el tamaño de la imagen
            int width = sourceImg.getWidth() * scale;
            int height = sourceImg.getHeight() * scale;

            BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImg.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(sourceImg, 0, 0, width, height, null);
            graphics.dispose();

            //convierte la imagen a pixeles ascii coloreados
            for (int y = 0; y < height; y += BLOCK_HEIGHT) {
                for (int x = 0; x < width; x += BLOCK_WIDTH) {
                    //calcula el color del bloque y lo imprime
                    Color color = new Color(resizedImg.getRGB(x, y));
                    System.out.print("\033[38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m" + ASCII_PIXEL);
                }
                System.out.print("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
