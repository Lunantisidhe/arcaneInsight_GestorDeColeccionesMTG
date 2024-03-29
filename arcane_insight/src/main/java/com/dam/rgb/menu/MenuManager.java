/*  Estructura
    1 - menu principal
        1.1 - tu coleccion
            1.1.1 - ver coleccion
            1.1.2 - añadir cartas
            1.1.3 - buscar cartas
            1.1.4 - eliminar cartas
            1.1.5 - exportar coleccion
        1.2 - tus mazos
            1.2.1 - ver tus mazos
                1.2.1.1 - ver mazo
                1.2.1.2 - añadir cartas
                1.2.1.3 - buscar cartas
                1.2.1.4 - eliminar cartas
                1.2.1.5 - exportar mazo
            1.2.2 - crear nuevo mazo
            1.2.3 - eliminar mazo
        1.3 - tu lista de deseos
            1.3.1 - ver lista de deseos
            1.3.2 - añadir cartas
            1.3.3 - buscar cartas
            1.3.4 - eliminar cartas
            1.3.5 - exportar lista de deseos
        1.4 - buscar cartas globales
*/

package com.dam.rgb.menu;

import com.dam.rgb.crud.CardCRUDManager;
import com.dam.rgb.utilities.CardViewEnum;
import com.dam.rgb.utilities.CollectionNames;

import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class MenuManager {

    private static final Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);

    private static final Map<String, String> nameCorrespondence = Map.of(
            CollectionNames.MAIN_COLLECTION_NAME, "colección",
            CollectionNames.WANTS_COLLECTION_NAME, "lista de deseos"
    );


    // menu generico
    private static void showMenu(String title, String[] options, Runnable[] actions) {

        boolean running = true;
        String selectionString;
        int selection;

        do {
            // impresion titulo y opciones menu
            System.out.println("\n" + title);

            for (int i = 0; i < options.length - 1; i++)
                System.out.println((i + 1) + " - " + options[i]);
            System.out.println("X - " + options[options.length - 1]);

            // introduccion opcion
            try {
                selectionString = SC.nextLine();

                // modo testing
                if (selectionString.equals("389")) {
                    selection = options.length;
                    CardCRUDManager.addTestCards();

                } else if (selectionString.equalsIgnoreCase("x"))
                    selection = options.length;
                else
                    selection = Integer.parseInt(selectionString);

            } catch (InputMismatchException | NumberFormatException e) {
                selection = 0;
                SC.reset();
            }

            // opcion salida del menu
            if (selection == options.length)
                running = false;

            // ejecucion de metodos
            if (selection >= 1 && selection <= options.length)
                actions[selection - 1].run();

            // caso parametro incorrecto
            else {
                System.err.println("Introduzca un parámetro válido.");
                SC.reset();
            }

        } while (running);
    }

    // 1 - menu principal
    public static void mainMenu() {

        String[] options = {"Tu colección", "Tus mazos", "Tu lista de deseos", "Buscar cartas globales", "Cerrar sesión"};

        Runnable[] actions = {
                () -> collection(CollectionNames.MAIN_COLLECTION_NAME),
                MenuManager::decks,
                () -> collection(CollectionNames.WANTS_COLLECTION_NAME),

                // 1.4 - buscar cartas globales
                () -> CardCRUDManager.searchByParams(CollectionNames.GLOBAL_COLLECTION_NAME),

                () -> System.out.println("Ejecución finalizada.")
        };

        showMenu("Menú principal", options, actions);

        // cerramos el scanner
        SC.close();
    }

    // 1.1 - tu coleccion
    // 1.3 - tu lista de deseos
    public static void collection(String collectionName) {

        String[] options = {"Ver tu " + nameCorrespondence.get(collectionName), "Añadir cartas", "Buscar cartas",
                "Eliminar cartas", "Exportar tu " + nameCorrespondence.get(collectionName), "Volver"};

        Runnable[] actions = {
                () -> viewCollection(collectionName),
                () -> addCards(collectionName),

                //1.1.3 - buscar cartas
                //1.3.3 - buscar cartas
                () -> CardCRUDManager.searchByParams(collectionName),

                // 1.1.4 - eliminar cartas
                // 1.3.4 - eliminar cartas
                () -> CardCRUDManager.deleteCard(collectionName),

                // 1.1.5 - exportar coleccion
                // 1.3.5 - exportar lista de deseos
                () -> CardCRUDManager.exportCardsToFile(collectionName),

                () -> {}
        };

        showMenu("Tu " + nameCorrespondence.get(collectionName), options, actions);
    }

    // 1.1.1 - ver coleccion
    // 1.3.1 - ver lista de deseos
    private static void viewCollection(String collectionName) {

        String[] options = {"Solo nombres", "Formato carta", "Formato carta con imágenes", "Volver"};

        Runnable[] actions = {
                () -> CardCRUDManager.viewCards(collectionName, CardViewEnum.TEXT_ONLY),
                () -> CardCRUDManager.viewCards(collectionName, CardViewEnum.CARD),
                () -> CardCRUDManager.viewCards(collectionName, CardViewEnum.CARD_W_IMG),
                () -> {}
        };

        showMenu("Ver tu " + nameCorrespondence.get(collectionName), options, actions);
    }

    // 1.1.2 - añadir cartas
    // 1.3.2 - añadir cartas
    private static void addCards(String collectionName) {

        String[] options = {"A mano", "Desde un archivo de texto", "Volver"};

        Runnable[] actions = {
                () -> CardCRUDManager.addCards(collectionName),
                () -> CardCRUDManager.addCardsFromFile(collectionName),
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

        String[] options = {"Ver mazo", "Añadir cartas", "Buscar cartas", "Eliminar cartas", "Exportar mazo", "Volver"};

        Runnable[] actions = {

                // 1.2.1.1 - ver mazo
                () -> CardCRUDManager.viewDeck(deckName),

                // 1.2.1.2 - añadir cartas
                () -> addCards(deckName),

                // 1.2.1.3 - buscar cartas
                () -> CardCRUDManager.searchByParams(deckName),

                // 1.2.1.4 - eliminar cartas
                () -> CardCRUDManager.deleteCard(deckName),

                // 1.2.1.5 - exportar mazo
                () -> CardCRUDManager.exportCardsToFile(deckName),

                () -> {}
        };

        showMenu("Gestión " + croppedDeckName, options, actions);
    }
}
