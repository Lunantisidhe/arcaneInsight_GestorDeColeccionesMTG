package com.dam.rgb.crud;

import com.dam.rgb.db.DBManager;
import com.dam.rgb.db.utilities.CardViewEnum;
import com.dam.rgb.db.utilities.Connection;
import com.dam.rgb.visual.Printer;
import org.bson.Document;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class CardCRUDManager {

    private static final Scanner SC = new Scanner(System.in);

    // busca una carta en la base de datos general y comprueba si se quiere añadir
    public static void addCard() {

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
                    do {
                        cardIdString = textReturn("Introduce el número de la carta a añadir");

                        if (cardIdString != null) {
                            try {
                                cardId = Integer.parseInt(cardIdString);

                                // añade la carta a la coleccion
                                DBManager.createCard(new JSONObject(search.get(cardId - 1).toJson()),
                                        "collection", 1d);
                                System.out.println("Se ha añadido tu carta.");

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
    public static void addCardsFromFile() {

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
                DBManager.createSeveralCards(cards, "collection");
                System.out.println("Se han añadido " + cards.size() + " cartas a tu colección.");

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
                    Printer.printCard(new JSONObject(card.toJson()), false);
                }

                else if (cardViewEnum.equals(CardViewEnum.CARD_W_IMG)) { //TODO revisar cartas doble cara
                    Printer.printCard(new JSONObject(card.toJson()), true);

                } else
                    System.out.println(card.getString("name"));
            }
        }
    }

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

                System.out.print("\n" + (i + 1));
                System.out.println("\n(x" + Math.round(card.getDouble("quantity")) + ") ");
                Printer.printCard(new JSONObject(card.toJson()), false);
            }

            String cardIdString;
            int cardId = 0;
            do {
                cardIdString = textReturn("Introduce el número de la carta a añadir");

                if (cardIdString != null) {
                    try {
                        cardId = Integer.parseInt(cardIdString);

                        // elimina la carta de la coleccion
                        DBManager.deleteCard(new JSONObject(allCards.get(cardId - 1).toJson()), "collection");
                        System.out.println("Se ha eliminado tu carta.");

                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.err.println("Error: el número introducido no es válido");
                        cardId = 0;
                    }
                }
            } while (cardIdString != null && cardId == 0);
        }
    }

    // comprueba si se desea o no volver en una insercion de texto
    private static String textReturn(String text) {

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

        return null;
    }
}
