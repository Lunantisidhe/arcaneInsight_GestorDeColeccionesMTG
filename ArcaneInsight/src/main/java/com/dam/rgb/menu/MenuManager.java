/*  Estructura
    1 - menu principal / main menu
        1.1 - ver coleccion / view collection
*/

package com.dam.rgb.menu;

import com.dam.rgb.db.DBManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuManager {

    public static Scanner scText = new Scanner(System.in);
    public static Scanner scNums = new Scanner(System.in);

    public static boolean returns = false;

    //menu generico
    public static void showMenu(String title, String[] options, Runnable[] actions) {

        boolean running = true;
        int selection;

        do {
            //impresion menu
            System.out.println("\n" + title);

            for (int i = 0; i < options.length; i++)
                System.out.println((i + 1) + " - " + options[i]);

            //introduccion opcion
            try {
                selection = scNums.nextInt();
            } catch (InputMismatchException e) {
                selection = 0;
                scNums.next();
            }


            //salida menu
            if (selection == options.length)
                running = false;

            //ejecucion metodos
            if (selection >= 1 && selection <= options.length)
                actions[selection - 1].run();

            //caso parametro incorrecto
            else
                System.err.println("Introduzca un parámetro válido.");


            //caso regresar al menu principal
            if (title.equals("Menú principal"))
                returns = false;

            if (returns)
                running = false;

        } while (running);

        //cerramos los scanner
        if (title.equals("Menú principal")) {
            scText.close();
            scNums.close();
        }
    }

    //1 - menu principal
    public static void mainMenu() {

        String[] options = {"Ver tu colección", "Añadir cartas", "Eliminar cartas", "Cerrar sesión"};
        Runnable[] actions = {

                MenuManager::viewCollection,
                () -> {}, // TODO añadir cartas
                () -> {}, // TODO eliminar cartas
                () -> System.out.println("Ejecución finalizada.")
        };

        showMenu("Menú principal", options, actions);
    }

    //1.1 - ver coleccion
    public static void viewCollection() {

        returns = true;

        String[] options = {"Solo nombres", "Con imágenes", "Volver"};
        Runnable[] actions = {

                () -> {}, // TODO ver titulos cartas
                () -> DBManager.seeAllCards("collection"),
                () -> {}
        };

        showMenu("Ver tu colección", options, actions);
    }
}
