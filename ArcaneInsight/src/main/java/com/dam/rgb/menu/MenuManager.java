/*  Estructura
    1 - menu principal / main menu
        1.1 - ver coleccion / view collection
        1.2 - añadir cartas / add cards
*/

package com.dam.rgb.menu;

import com.dam.rgb.db.utilities.CardViewEnum;
import com.dam.rgb.db.DBManager;
import com.dam.rgb.crud.CardCRUDManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuManager {

    private static final Scanner SC = new Scanner(System.in);

    private static boolean returns = false;


    // menu generico
    private static void showMenu(String title, String[] options, Runnable[] actions) {

        boolean running = true;
        int selection;

        do {
            // impresion titulo y opciones menu
            System.out.println("\n" + title);

            for (int i = 0; i < options.length; i++)
                System.out.println((i + 1) + " - " + options[i]);

            // introduccion opcion
            try {
                selection = SC.nextInt();
            } catch (InputMismatchException e) {
                selection = 0;
                SC.next();
            }

            // opcion salida del menu
            if (selection == options.length)
                running = false;

            // ejecucion de metodos
            if (selection >= 1 && selection <= options.length)
                actions[selection - 1].run();

            // caso parametro incorrecto
            else
                System.err.println("Introduzca un parámetro válido.");


            // caso regresar al menu principal
            if (title.equals("Menú principal"))
                returns = false;

            if (returns)
                running = false;

        } while (running);
    }

    // 1 - menu principal
    public static void mainMenu() {

        String[] options = {"Ver tu colección", "Añadir cartas", "Eliminar cartas", "Cerrar sesión"};

        Runnable[] actions = {
                MenuManager::viewCollection,
                MenuManager::addCards,
                () -> {}, // TODO eliminar cartas
                () -> System.out.println("Ejecución finalizada.")
        };

        showMenu("Menú principal", options, actions);

        // cerramos el scanner
        SC.close();
    }

    // 1.1 - ver coleccion
    private static void viewCollection() {

        returns = true;

        String[] options = {"Solo nombres", "Formato carta", "Formato carta con imágenes", "Volver"};

        Runnable[] actions = {
                () -> DBManager.seeAllCards("collection", CardViewEnum.TEXT_ONLY),
                () -> DBManager.seeAllCards("collection", CardViewEnum.CARD),
                () -> DBManager.seeAllCards("collection", CardViewEnum.CARD_W_IMG),
                () -> {}
        };

        showMenu("Ver tu colección", options, actions);
    }

    // 1.2 - añadir cartas
    private static void addCards() {

        returns = true;

        String[] options = {"A mano", "Desde un archivo de texto", "Volver"};

        Runnable[] actions = {
                CardCRUDManager::addCard,
                () -> {}, // TODO añadir desde archivo de texto
                () -> {}
        };

        showMenu("Añadir cartas", options, actions);
    }
}
