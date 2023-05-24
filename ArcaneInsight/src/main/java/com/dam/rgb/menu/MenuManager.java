/*  Estructura
    1 - menu principal
        1.1 - tu coleccion
            1.1.1 - ver coleccion
            1.1.2 - añadir cartas
            1.1.3 - eliminar cartas
        1.2 - tus mazos
            1.2.1 - ver tus mazos
            1.2.2 - crear nuevo mazo
*/

package com.dam.rgb.menu;

import com.dam.rgb.crud.CardCRUDManager;
import com.dam.rgb.db.utilities.CardViewEnum;

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

        String[] options = {"Tu colección", "Tus mazos", "Cerrar sesión"};

        Runnable[] actions = {
                MenuManager::collection,
                MenuManager::decks,
                () -> System.out.println("Ejecución finalizada.")
        };

        showMenu("Menú principal", options, actions);

        // cerramos el scanner
        SC.close();
    }

    // 1.1 - tu coleccion
    public static void collection() {

        String[] options = {"Ver tu colección", "Añadir cartas", "Eliminar cartas", "Cerrar sesión"};

        Runnable[] actions = {
                MenuManager::viewCollection,
                MenuManager::addCards,
                () -> CardCRUDManager.deleteCard("collection"),
                () -> {}
        };

        showMenu("Tu colección", options, actions);
    }

    // 1.1.1 - ver coleccion
    private static void viewCollection() {

        returns = true;

        String[] options = {"Solo nombres", "Formato carta", "Formato carta con imágenes", "Volver"};

        Runnable[] actions = {
                () -> CardCRUDManager.viewCards("collection", CardViewEnum.TEXT_ONLY),
                () -> CardCRUDManager.viewCards("collection", CardViewEnum.CARD),
                () -> CardCRUDManager.viewCards("collection", CardViewEnum.CARD_W_IMG),
                () -> {}
        };

        showMenu("Ver tu colección", options, actions);
    }

    // 1.1.2 - añadir cartas
    private static void addCards() {

        returns = true;

        String[] options = {"A mano", "Desde un archivo de texto", "Volver"};

        Runnable[] actions = {
                CardCRUDManager::addCard,
                CardCRUDManager::addCardsFromFile,
                () -> {}
        };

        showMenu("Añadir cartas", options, actions);
    }

    // 1.2 - tus mazos
    private static void decks() { //TODO

        String[] options = {"Ver tus mazos", "Crear nuevo mazo", "Volver"};

        Runnable[] actions = {
                CardCRUDManager::viewDecks,
                CardCRUDManager::createDeck,
                () -> {}
        };

        showMenu("Tus mazos", options, actions);
    }
}
