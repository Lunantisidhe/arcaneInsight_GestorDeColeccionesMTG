package com.dam.rgb;

import com.dam.rgb.db.DBManager;
import com.dam.rgb.visual.Style;
import org.bson.Document;

import java.util.List;

public class ArcaneInsight {

    public static void main(String[] args) {

        System.out.println(Style.LOGO);

        try {
            String[] urls = {
                    /* types */
                    //artifact
                    "aeon+engine",
                    //battle
                    "invasion+ikoria",
                    //conspiracy
                    "power+play",
                    //creature
                    "aboroth",
                    //emblem
                    "chandra+torch+defiance+emblem",
                    //enchantment
                    "alpine+moon",
                    //hero
                    "the+destined",
                    //instant
                    "absorb",
                    //land
                    "access+tunel",
                    //phenomenon
                    "interplanar+tunnel",
                    //plane
                    "lethe+lake",
                    //planeswalker
                    "garruk+primal",
                    "ajani+sleeper+agent",
                    "daretti+scrap",
                    "elspeth+nemesis",
                    "elminster",
                    "comet+pup",
                    //scheme
                    "choose+demise",
                    //vanguard
                    "maraxus",

                    /* supertypes */
                    //basic
                    "island",
                    //legendary
                    "adamaro+first+desire",
                    //token
                    "bat",
                    "ape",

                    /* subtypes */
                    //attraction
                    "bumper+cars",
                    //contraption
                    "accesories+murder",
                    //equipment
                    "ancestral+katana",
                    //gold
                    "gold",
                    //class
                    "bard+class",
                    //saga
                    "elspeth+nightmare",
                    //adventure
                    "crystal+dragon",

                    /* unique */
                    //two faced
                    "befriending+moths",
                    //nightbound
                    "child+pack",
                    //meld
                    "gisela+broken+blade",
                    "brisela+voice+nightmares",
                    //leveler
                    "echo+mage",
                    "lighthouse+chrono",
                    //split
                    "odds+ends",
                    //split with fuse
                    "breaking+entering",
                    //split with aftermath
                    "rags+riches",
                    //flip
                    "akki+lavarunner",
                    //prototype
                    "fallaji+dragon+engine",
                    //host
                    "labro+bot",
                    //augment
                    "half+squirrel",
                    //dungeon
                    "dungeon+mad+mage",

                    /* conflictive */
                    "asmora",
                    "okina+temple",
                    "amphin+cutt",
                    "dance+dead"
            };

            /* * * */

            List<Document> query = DBManager.searchFuzzyCards("asmora", "name", "allCards", true);
            for (Document result : query) {
                System.out.println(result.toJson());
            }

            /* * * */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
