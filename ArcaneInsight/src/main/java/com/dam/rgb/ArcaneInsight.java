package com.dam.rgb;

import com.dam.rgb.db.BulkDefaultCardsImporter;
import com.dam.rgb.menu.MenuManager;
import com.dam.rgb.visual.Style;

public class ArcaneInsight {

    public static void main(String[] args) {

        System.out.println(Style.LOGO);
        BulkDefaultCardsImporter.checkUpdate();

        MenuManager.mainMenu();
    }
}
