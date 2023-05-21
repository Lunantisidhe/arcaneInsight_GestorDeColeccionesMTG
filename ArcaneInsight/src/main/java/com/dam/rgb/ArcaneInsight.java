package com.dam.rgb;

import com.dam.rgb.db.BulkCardsImporter;
import com.dam.rgb.menu.MenuManager;
import com.dam.rgb.visual.Style;

// lanzador aplicacion
public class ArcaneInsight {

    public static void main(String[] args) {

        // impresion logo
        System.out.println(Style.LOGO);

        // comprueba que la base de datos local esta actualizada respecto al api de scryfall
        BulkCardsImporter.checkUpdate();

        // menu principal
        MenuManager.mainMenu();
    }
}
