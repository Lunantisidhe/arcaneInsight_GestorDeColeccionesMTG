package com.dam.rgb;

import com.dam.rgb.db.BulkDefaultCardsImporter;
import com.dam.rgb.visual.Style;

public class ArcaneInsight {

    public static void main(String[] args) {

        System.out.println(Style.LOGO);
        BulkDefaultCardsImporter.checkUpdate();

//        System.out.println("1 - Ver tu colección");
//        DBManager.seeAllCards("collection");
    }
}
