package com.dam.rgb;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.dam.rgb.db.BulkCardsImporter;
import com.dam.rgb.menu.MenuManager;
import com.dam.rgb.visual.Style;
import org.slf4j.LoggerFactory;

// lanzador aplicacion
public class ArcaneInsight {

    public static void main(String[] args) {

        // elimina los logs de informacion de la base de datos en la consola
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("org.mongodb.driver").setLevel(Level.ERROR);

        // impresion logo
        System.out.println(Style.LOGO);

        // comprueba que la base de datos local esta actualizada respecto al api de scryfall
        BulkCardsImporter.checkUpdate();

        // menu principal
        MenuManager.mainMenu();
    }
}
