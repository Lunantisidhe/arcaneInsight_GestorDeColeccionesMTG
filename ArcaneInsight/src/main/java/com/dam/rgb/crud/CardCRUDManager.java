package com.dam.rgb.crud;

import com.dam.rgb.db.DBManager;
import com.dam.rgb.utilities.CardViewEnum;
import com.dam.rgb.menu.MenuManager;
import com.dam.rgb.visual.Printer;
import org.bson.Document;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

public class CardCRUDManager {

    public static Scanner SC = new Scanner(System.in, StandardCharsets.UTF_8);


    /* METODOS CREACION */
    // busca una carta en la base de datos general y comprueba si se quiere añadir
    public static void addCards(String collectionName) {

        String cardName;

        do {
            cardName = textReturn("Introduce el nombre de la carta a añadir");

            if (cardName != null) {

                // busca todas las cartas con nombres parecidos
                ArrayList<Document> search = DBManager.searchFuzzyCards(cardName, "name",
                        "allCards", false);

                if (search.isEmpty())
                    System.out.println("No se encontró ninguna carta con este nombre");

                else {
                    // imprime las cartas encontradas
                    for (int i = 0; i < search.size(); i++) {

                        Document doc = search.get(i);
                        System.out.println("\n" + (i + 1));
                        Printer.printCard(new JSONObject(doc.toJson()), false);
                    }

                    String cardIdString;
                    int cardId = 0;
                    String quantityString;
                    int quantity = 0;
                    do {
                        cardIdString = textReturn("Introduce el número de la carta a añadir");

                        if (cardIdString != null) {
                            try {
                                cardId = Integer.parseInt(cardIdString);

                                // introduccion cantidad de cartas
                                do {
                                    quantityString = textReturn("Introduce la cantidad de cartas a añadir");

                                    if (quantityString != null) {

                                        try {
                                            quantity = Integer.parseInt(quantityString);

                                            if (quantity < 1 || quantity > 999999)
                                                throw new NumberFormatException();

                                            // añade la carta a la coleccion
                                            DBManager.createCard(new JSONObject(search.get(cardId - 1).toJson()),
                                                    collectionName, quantity);
                                            System.out.println("Se ha añadido tu carta.");

                                        } catch (NumberFormatException e) {
                                            System.err.println("Error: el número introducido no es válido");
                                            quantity = 0;
                                        }
                                    }
                                } while (quantityString != null && quantity == 0);

                            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                System.err.println("Error: el número introducido no es válido");
                                cardId = 0;
                            }
                        }
                    } while (cardIdString != null && cardId == 0);
                }
            }

        } while (cardName != null);
    }

    // añade cartas a la base de datos desde un fichero txt
    public static void addCardsFromFile(String collectionName) {

        System.out.print("El formato debe ser [cantidad] [nombre carta]");
        String pathStr = textReturn("Introduce el nombre o ruta del fichero");

        if (pathStr != null) {

            // si no tiene extension, se la añadimos
            if (!pathStr.contains("."))
                pathStr += ".txt";

            ArrayList<Document> cards = new ArrayList<>();

            String line;
            String[] parts = new String[2];

            try {
                Path path = Paths.get(pathStr);
                BufferedReader br = Files.newBufferedReader(path);

                while ((line = br.readLine()) != null) {

                    if (line.isBlank())
                        continue;

                    parts = line.split(" ", 2);

                    Document card = DBManager.searchFuzzyCards(parts[1], "name", "allCards",
                            true).get(0);

                    double quantity = Double.parseDouble(parts[0]);
                    if (quantity < 0)
                        throw new NumberFormatException();
                    card.append("quantity", quantity);

                    // quitamos el id del registro de la carta
                    card.remove("_id");

                    cards.add(card);
                }
                br.close();

                // añade los documentos a la coleccion de mongo
                DBManager.createSeveralCards(cards, collectionName);
                System.out.println("Se han añadido " + cards.size() + " cartas.");

            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("\nError: parámetro no válido: " + parts[0]);
            } catch (NumberFormatException e) {
                System.err.println("\nError: número no válido: " + parts[0]);
            } catch (IndexOutOfBoundsException e) {
                System.err.println("\nError: no se ha encontrado la carta: " + parts[1]);
            } catch (IOException e) {
                System.err.println("\nError: error al leer el fichero.");
            }
        }
    }

    // crea un mazo en la base de datos
    public static void createDeck() {

        String deckName = textReturn("Introduce el nombre del mazo a añadir");

        if (deckName != null)
            DBManager.createDeck(deckName);
    }


    /* METODOS LECTURA */
    // muestra todas las cartas de una coleccion
    public static void viewCards(String collectionName, CardViewEnum cardViewEnum) {

        // recuperamos las cartas de la coleccion
        ArrayList<Document> allCards = DBManager.recoverAllCards(collectionName);

        System.out.println("\nTu colección");

        if (allCards.isEmpty())
            System.out.println("No existe ninguna carta en tu colección");

        else {
            for (Document card : allCards) {

                System.out.print("\n(x" + Math.round(card.getDouble("quantity")) + ") ");

                // segun la opcion, muestra las imagenes o solo los nombres de las cartas
                if (cardViewEnum.equals(CardViewEnum.CARD)) {
                    System.out.println();
                    Printer.printCard(new JSONObject(card.toJson()), false);
                }

                else if (cardViewEnum.equals(CardViewEnum.CARD_W_IMG)) {
                    System.out.println();
                    Printer.printCard(new JSONObject(card.toJson()), true);

                } else
                    System.out.println(card.getString("name"));
            }
        }
    }

    // muestra todos los mazos existentes en la base de datos
    public static void viewDecks() {

        ArrayList<String> deckNames = DBManager.recoverDecks();

        if (deckNames.isEmpty())
            System.out.println("No existe ningún mazo");

        else {
            for (int i = 0; i < deckNames.size(); i++) {
                String deckName = deckNames.get(i);
                String croppedDeckName = deckName.substring(0, deckName.length() - 5);

                System.out.println((i + 1) + " - " + croppedDeckName);
            }

            String deckIdString;
            int deckId = 0;
            do {
                deckIdString = textReturn("Introduce el número del deck a visualizar");

                if (deckIdString != null) {
                    try {
                        deckId = Integer.parseInt(deckIdString);
                        String deckName = deckNames.get(deckId - 1);

                        // visualiza el mazo
                        viewDeck(deckName);

                        // gestion mazo
                        MenuManager.deckManagement(deckName);

                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.err.println("Error: el número introducido no es válido");
                        deckId = 0;
                    }
                }
            } while (deckIdString != null && deckId == 0);
        }
    }

    // muestra las cartas de un mazo
    public static void viewDeck(String deckName) {

        String croppedDeckName = deckName.substring(0, deckName.length() - 5);

        ArrayList<Document> deckCards = DBManager.recoverAllCards(deckName);

        if (deckCards.isEmpty())
            System.out.println("No existe ninguna carta en el deck " + croppedDeckName);

        else {

            // cartas segun color, tipo y tipo de tierra
            int whiteCards = 0, blueCards = 0, blackCards = 0, redCards = 0, greenCards = 0, colorlessCards = 0;
            int landCards = 0, creatureCards = 0, instantCards = 0, sorceryCards = 0, artifactCards = 0,
                    enchantmentCards = 0, planeswalkerCards = 0, otherCardTypes = 0;
            int plainsCards = 0, islandCards = 0, swampCards = 0, mountainCards = 0, forestCards = 0, otherLandCards = 0;


            for (Document card : deckCards) {

                double quantity = card.getDouble("quantity");

                // suma cartas segun color
                ArrayList<String> colores = (ArrayList<String>) card.get("color_identity");

                if (colores.isEmpty())
                    colorlessCards += quantity;

                else
                    for (String color : colores)
                        switch (color) {
                            case "W" -> whiteCards += quantity;
                            case "U" -> blueCards += quantity;
                            case "B" -> blackCards += quantity;
                            case "R" -> redCards += quantity;
                            case "G" -> greenCards += quantity;
                        }

                // suma cartas segun tipo
                String type = card.getString("type_line");

                if (type.contains("Land")) {

                    landCards += quantity;

                    if (type.contains("Basic")) {
                        if (type.contains("Plains"))
                            plainsCards+= quantity;
                        else if (type.contains("Island"))
                            islandCards+= quantity;
                        else if (type.contains("Swamp"))
                            swampCards+= quantity;
                        else if (type.contains("Mountain"))
                            mountainCards+= quantity;
                        else if (type.contains("Forest"))
                            forestCards+= quantity;
                        else
                            otherLandCards+= quantity;

                    } else
                        otherLandCards+= quantity;

                } else if (type.contains("Creature"))
                    creatureCards+= quantity;
                else if (type.contains("Instant"))
                    instantCards+= quantity;
                else if (type.contains("Sorcery"))
                    sorceryCards+= quantity;
                else if (type.contains("Artifact"))
                    artifactCards+= quantity;
                else if (type.contains("Enchantment"))
                    enchantmentCards+= quantity;
                else if (type.contains("Planeswalker"))
                    planeswalkerCards+= quantity;
                else
                    otherCardTypes+= quantity;


                // impresion carta
                double quantityInCollection = DBManager.searchCardInCollection(
                        card.getString("name"), "name", "collection");

                StringBuilder sb = new StringBuilder("\n(x")
                        .append(Math.round(card.getDouble("quantity"))).append(") (");

                if (quantityInCollection > 0)
                    sb.append(Math.round(quantityInCollection));
                else
                    sb.append("ninguna");

                sb.append(" en la colección)");
                System.out.println(sb);

                Printer.printCard(new JSONObject(card.toJson()), false);
            }

            // impresion estadisticas
            System.out.println("\nEstadísticas\n");

            Printer.printFormatted("Cartas por color", "Cartas por tipo", "Tierras por tipo");

            Printer.printFormatted("Blancas: " + whiteCards,
                    "Tierras: " + landCards,
                    "Llanuras: " + plainsCards);
            Printer.printFormatted("Azules: " + blueCards,
                    "Criaturas: " + creatureCards,
                    "Islas: " + islandCards);
            Printer.printFormatted("Negras: " + blackCards,
                    "Instantáneos: " + instantCards,
                    "Pantanos: " + swampCards);
            Printer.printFormatted("Rojas: " + redCards,
                    "Conjuros: " + sorceryCards,
                    "Montañas: " + mountainCards);
            Printer.printFormatted("Verdes: " + greenCards,
                    "Artefactos: " + artifactCards,
                    "Bosques: " + forestCards);
            Printer.printFormatted("Incoloras: " + colorlessCards,
                    "Encantamientos: " + enchantmentCards,
                    "Otras: " + otherLandCards);
            Printer.printFormatted("", "Planeswalkers: " + planeswalkerCards, "");
            Printer.printFormatted("", "Otras: " + otherCardTypes, "");
        }
    }

    // busca cartas en una coleccion empleando filtros y las muestra
    public static void searchByParams(String collectionName) {

        // filtro busqueda
        String searchField;

        do {
            searchField = textReturn("Introduce el filtro de búsqueda");

            if (searchField == null)
                return;

            switch (searchField.toLowerCase()) {
                case "name", "nombre" -> searchField = "name";
                case "rarity", "rareza" -> searchField = "rarity";
                case "type line", "type", "linea de tipo", "línea de tipo", "tipo" -> searchField = "type_line";
                case "artist", "artista" -> searchField = "artist";
                default -> searchField = null;
            }

            if (searchField == null) {
                System.err.println("Error: filtro de búsqueda no válido.");
                System.err.println("Los filtros válidos son:");
                System.err.println("Name, Rarity, Type line y Artist");
            }

        } while (searchField == null);


        // parametro busqueda
        String fuzzyCardParam = textReturn("Introduce el parámetro a buscar");

        if (fuzzyCardParam != null) {

            ArrayList<Document> cards = DBManager.searchFuzzyCards(fuzzyCardParam, searchField,
                    collectionName, false);

            if (cards.isEmpty())
                System.out.println("No se ha encontrado ninguna carta");

            else
                for (Document card : cards)
                    Printer.printCard(new JSONObject(card.toJson()), false);
        }
    }


    /* METODOS BORRADO */
    // muestra las cartas de la coleccion y comprueba si se quiere eliminar alguna
    public static void deleteCard(String collectionName) {

        // recuperamos las cartas de la coleccion
        ArrayList<Document> allCards = DBManager.recoverAllCards(collectionName);

        System.out.println("\nCartas que puedes eliminar");

        if (allCards.isEmpty())
            System.out.println("No existe ninguna carta en tu colección");

        else {
            for (int i = 0; i < allCards.size(); i++) {
                Document card = allCards.get(i);

                System.out.println("\n" + (i + 1) + " - (x" + Math.round(card.getDouble("quantity")) + ") ");
                Printer.printCard(new JSONObject(card.toJson()), false);
            }

            String cardIdString;
            int cardId = 0;
            do {
                cardIdString = textReturn("Introduce el número de la carta a eliminar");

                if (cardIdString != null) {
                    try {
                        cardId = Integer.parseInt(cardIdString);

                        // elimina la carta de la coleccion
                        DBManager.deleteCard(new JSONObject(allCards.get(cardId - 1).toJson()), collectionName);
                        System.out.println("Se ha eliminado tu carta.");

                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.err.println("Error: el número introducido no es válido");
                        cardId = 0;
                    }
                }
            } while (cardIdString != null && cardId == 0);
        }
    }

    // muestra los mazos de la coleccion y comprueba si se quiere eliminar alguno
    public static void deleteDeck() {

        ArrayList<String> deckNames = DBManager.recoverDecks();

        if (deckNames.isEmpty())
            System.out.println("No existe ningún mazo");

        else {
            for (int i = 0; i < deckNames.size(); i++) {
                String deckName = deckNames.get(i);
                String croppedDeckName = deckName.substring(0, deckName.length() - 5);

                System.out.println((i + 1) + " - " + croppedDeckName);
            }

            String deckIdString;
            int deckId = 0;
            do {
                deckIdString = textReturn("Introduce el número del deck a eliminar");

                if (deckIdString != null) {
                    try {
                        deckId = Integer.parseInt(deckIdString);
                        String deckName = deckNames.get(deckId - 1);
                        String croppedDeckName = deckName.substring(0, deckName.length() - 5);

                        // confirmacion
                        String confirmation;
                        do {
                            confirmation = textReturn("Para eliminar el mazo escribe \""
                                    + croppedDeckName.toUpperCase() + "\"");

                            if (confirmation == null)
                                break;

                            if (Objects.requireNonNull(confirmation).equalsIgnoreCase(croppedDeckName)) {
                                DBManager.deleteDeck(deckName);
                                System.out.println("El mazo " + croppedDeckName + " se ha eliminado correctamente.");
                            }

                        } while (!Objects.requireNonNull(confirmation).equalsIgnoreCase(croppedDeckName));

                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.err.println("Error: el número introducido no es válido");
                        deckId = 0;
                    }
                }
            } while (deckIdString != null && deckId == 0);
        }
    }


    /* METODOS AUXILIARES */
    // comprueba si se desea o no volver en una insercion de texto
    public static String textReturn(String text) {

        String input;

        do {
            System.out.println("\n" + text + " o X para volver");
            input = SC.nextLine();

            if (input.equalsIgnoreCase("x"))
                return null;

            else if (input.isBlank())
                System.err.println("Error: introduce un parámetro válido");

            else
                return input;

        } while (input.isBlank());

        return input;
    }

    // exporta cartas de la base de datos a un fichero txt
    public static void exportCardsToFile(String collectionName) {

        String pathStr = textReturn("Introduce el nombre o ruta del fichero");

        if (pathStr != null) {

            // si no tiene extension, se la añadimos
            if (!pathStr.contains("."))
                pathStr += ".txt";

            // recuperamos las cartas de la coleccion
            ArrayList<Document> cards = DBManager.recoverAllCards(collectionName);

            if (cards.isEmpty())
                System.out.println("No existe ninguna carta a exportar");

            else {
                Stream<String> cardsStream = cards.stream().map((Document card) ->
                        Math.round(card.getDouble("quantity")) + " " + card.getString("name"));
                Path destination = Paths.get(pathStr);
                BufferedWriter bw;

                try {
                    bw = Files.newBufferedWriter(destination);

                    // escritura
                    cardsStream.forEach(line -> {
                        try {
                            bw.write(line + "\n");
                        } catch (IOException e) {
                            System.err.println("Error: problema al escribir el fichero.");
                        }
                    });

                    bw.flush();
                    bw.close();

                    System.out.println("Se han exportado tus cartas correctamente.");

                } catch (IOException e) {
                    System.err.println("Error: la ruta no es correcta.");
                }
            }
        }
    }

    // añade a la base de datos las cartas de prueba de coleccion, lista de deseos y dos mazos
    public static void addTestCards() {

        // crea los dos mazos
        DBManager.createDeck("Mono-red aggro");
        DBManager.createDeck("Yorion blink EDH");

        String[][] pathsStr = {
                {"collection", "../test_data/testData_collection.txt"},
                {"wants", "../test_data/testData_wants.txt"},
                {"Mono-red aggro_deck", "../test_data/testData_deck1.txt"},
                {"Yorion blink EDH_deck", "../test_data/testData_deck2.txt"}
        };
        ArrayList<Document> cards = new ArrayList<>();

        String line;
        String[] parts = new String[2];

        for (String[] pathStr : pathsStr) {
            try {
                Path path = Paths.get(pathStr[1]);
                BufferedReader br = Files.newBufferedReader(path);

                while ((line = br.readLine()) != null) {

                    if (line.isBlank())
                        continue;

                    parts = line.split(" ", 2);

                    Document card = DBManager.searchFuzzyCards(parts[1], "name", "allCards",
                            true).get(0);

                    double quantity = Double.parseDouble(parts[0]);
                    if (quantity < 0)
                        throw new NumberFormatException();
                    card.append("quantity", quantity);

                    // quitamos el id del registro de la carta
                    card.remove("_id");

                    cards.add(card);
                }
                br.close();

                // añade los documentos a la coleccion de mongo
                DBManager.createSeveralCards(cards, pathStr[0]);
                System.out.println("Se han añadido " + cards.size() + " cartas.");

            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("\nError: parámetro no válido: " + parts[0]);
            } catch (NumberFormatException e) {
                System.err.println("\nError: número no válido: " + parts[0]);
            } catch (IndexOutOfBoundsException e) {
                System.err.println("\nError: no se ha encontrado la carta: " + parts[1]);
            } catch (IOException e) {
                System.err.println("\nError: error al leer el fichero.");
            }

            cards.clear();
        }

        System.out.println("Añadidas cartas de prueba");
    }
}
