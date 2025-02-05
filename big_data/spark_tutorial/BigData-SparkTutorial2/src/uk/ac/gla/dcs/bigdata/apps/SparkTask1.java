package uk.ac.gla.dcs.bigdata.apps;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import uk.ac.gla.dcs.bigdata.functions.flatmap.PCMinReqsFilterFlatMap;
import uk.ac.gla.dcs.bigdata.functions.map.SteamGamesToListMap;
import uk.ac.gla.dcs.bigdata.functions.map.SteamStatsFormatter;
import uk.ac.gla.dcs.bigdata.functions.reducer.SameTitleReducer;
import uk.ac.gla.dcs.bigdata.structures.SteamGameList;
import uk.ac.gla.dcs.bigdata.structures.SteamGameStats;

import java.io.File;
import java.util.Collections;

public class SparkTask1 {

    public static void main(String[] args) {
        File hadoopDIR = new File("resources/hadoop/");
        System.setProperty("hadoop.home.dir", hadoopDIR.getAbsolutePath());
        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("SparkTask1");
        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();
        Dataset<Row> steamGamesAsRowTable = spark
                .read()
                .option("header", "true")
                .csv("data/Steam/games_features.sample.csv");
        // Encoder for SteamGameStats
        Encoder<SteamGameStats> steamGameStatsEncoder = Encoders.bean(SteamGameStats.class);

        // Map each Row to SteamGameStats
        Dataset<SteamGameStats> steamGames = steamGamesAsRowTable.map(new SteamStatsFormatter(), steamGameStatsEncoder);

        Dataset<SteamGameStats> xpGames = steamGames.flatMap(new PCMinReqsFilterFlatMap(), steamGameStatsEncoder);

        Encoder<SteamGameList> steamGameStatsListEncoder = Encoders.bean(SteamGameList.class);
        Dataset<SteamGameList> xpGamesAsLists =  xpGames.map(new SteamGamesToListMap(), steamGameStatsListEncoder);
        SteamGameList uniqueXPGamesAsLists = xpGamesAsLists.reduce(new SameTitleReducer());

        // Print out the count
        System.out.println(uniqueXPGamesAsLists.getGameList().size());

        // Sort by recommendation count because I want to
        Collections.sort(uniqueXPGamesAsLists.getGameList());
        Collections.reverse(uniqueXPGamesAsLists.getGameList());

        // Print the titles
        for (SteamGameStats game : uniqueXPGamesAsLists.getGameList()) {
            System.out.println(game.getTitle());
        }
    }
}
