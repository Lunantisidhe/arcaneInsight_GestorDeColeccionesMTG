/*  Estructura
    1 - menu principal
        1.1 - tu coleccion
            1.1.1 - ver coleccion
            1.1.2 - añadir cartas
            1.1.3 - eliminar cartas
        1.2 - tus mazos
            1.2.1 - ver tus mazos
                1.2.1.1 - ver mazo
                1.2.1.2 - añadir cartas
                1.2.1.3 - eliminar cartas
            1.2.2 - crear nuevo mazo
            1.2.3 - eliminar mazo
        1.3 - tus wants
            1.3.1 - ver wants
            1.3.2 - añadir cartas
            1.3.3 - eliminar cartas
*/

package com.dam.rgb.menu;

import com.dam.rgb.crud.CardCRUDManager;
import com.dam.rgb.db.utilities.CardViewEnum;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuManager {

    private static final Scanner SC = new Scanner(System.in);


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

        } while (running);
    }

    // 1 - menu principal
    public static void mainMenu() {

        String[] options = {"Tu colección", "Tus mazos", "Tus wants", "Cerrar sesión"};

        Runnable[] actions = {
                () -> collection("collection"),
                MenuManager::decks,
                () -> collection("wants"),
                () -> System.out.println("Ejecución finalizada.")
        };

        showMenu("Menú principal", options, actions);

        // cerramos el scanner
        SC.close();
    }

    // 1.1 - tu coleccion
    // 1.3 - tus wants
    public static void collection(String collectionName) {//TODO

        StringBuilder sb = new StringBuilder("tu");
        if (collectionName.endsWith("s"))
            sb.append("s");
        sb.append(" ").append(collectionName);

        String[] options = {"Ver " + sb, "Añadir cartas", "Eliminar cartas", "Volver"};

        Runnable[] actions = {
                () -> viewCollection(collectionName),
                () -> addCards(collectionName),

                // 1.1.3 - eliminar cartas
                // 1.3.3 - eliminar cartas
                () -> CardCRUDManager.deleteCard(collectionName),

                () -> {}
        };

        showMenu(sb.substring(0, 1).toUpperCase() + sb.substring(1), options, actions);
    }

    // 1.1.1 - ver coleccion
    // 1.3.1 - ver wants
    private static void viewCollection(String collectionName) {//TODO

        String[] options = {"Solo nombres", "Formato carta", "Formato carta con imágenes", "Volver"};

        Runnable[] actions = {
                () -> CardCRUDManager.viewCards(collectionName, CardViewEnum.TEXT_ONLY),
                () -> CardCRUDManager.viewCards(collectionName, CardViewEnum.CARD),
                () -> CardCRUDManager.viewCards(collectionName, CardViewEnum.CARD_W_IMG),
                () -> {}
        };

        StringBuilder sb = new StringBuilder("Ver tu");
        if (collectionName.endsWith("s"))
            sb.append("s");
        sb.append(" ").append(collectionName);

        showMenu(sb.toString(), options, actions);
    }

    // 1.1.2 - añadir cartas
    // 1.3.2 - añadir cartas
    private static void addCards(String collectionName) {//TODO

        String[] options = {"A mano", "Desde un archivo de texto", "Volver"};

        Runnable[] actions = {
                () -> CardCRUDManager.addCard(collectionName),
                CardCRUDManager::addCardsFromFile,
                () -> {}
        };

        showMenu("Añadir cartas", options, actions);
    }

    // 1.2 - tus mazos
    private static void decks() {

        String[] options = {"Ver tus mazos", "Crear nuevo mazo", "Eliminar mazo", "Volver"};

        Runnable[] actions = {

                // 1.2.1 - ver tus mazos
                CardCRUDManager::viewDecks,

                // 1.2.2 - crear nuevo mazo
                CardCRUDManager::createDeck,

                // 1.2.3 - eliminar mazo
                CardCRUDManager::deleteDeck,
                () -> {}
        };

        showMenu("Tus mazos", options, actions);
    }

    // gestion cartas mazos
    public static void deckManagement(String deckName) {

        String croppedDeckName = deckName.substring(0, deckName.length() - 5);

        String[] options = {"Ver mazo", "Añadir cartas", "Eliminar cartas", "Volver"};

        Runnable[] actions = {

                // 1.2.1.1 - ver mazo
                () -> CardCRUDManager.viewDeck(deckName),

                // 1.2.1.1 - añadir cartas
                () -> addCards(deckName),

                // 1.2.1.3 - eliminar cartas
                () -> CardCRUDManager.deleteCard(deckName),

                () -> {}
        };

        showMenu("Gestión " + croppedDeckName, options, actions);
    }
}
