package com.dam.rgb.utilities;

import com.dam.rgb.db.DBManager;
import com.dam.rgb.visual.Printer;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

public class CardManager {

    public static Scanner scText = new Scanner(System.in);

    public static void addCard() {

        String cardName;

        do {
            cardName = textReturn("Introduce el nombre de la carta a añadir");

            if (cardName != null) {

                ArrayList<Document> search = DBManager.searchFuzzyCards(cardName, "name", "allCards", false);

                if (search.isEmpty())
                    System.out.println("No se encontró ninguna carta con este nombre");

                else {
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

                                DBManager.createCard(new JSONObject(search.get(cardId - 1).toJson()), "collection");
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

    public static String textReturn(String text) {

        String input;

        do {
            System.out.println("\n" + text + " o X para volver");
            input = scText.nextLine();

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
