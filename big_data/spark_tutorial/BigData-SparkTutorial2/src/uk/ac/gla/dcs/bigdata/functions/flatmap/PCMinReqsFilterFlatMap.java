package uk.ac.gla.dcs.bigdata.functions.flatmap;

import org.apache.spark.api.java.function.FlatMapFunction;
import uk.ac.gla.dcs.bigdata.structures.SteamGameStats;

import java.util.*;

public class PCMinReqsFilterFlatMap implements FlatMapFunction<SteamGameStats, SteamGameStats> {
    @Override
    public Iterator<SteamGameStats> call(SteamGameStats steamGameStats) throws Exception {
        String text = steamGameStats.getPcminreqstext();

        if (Objects.nonNull(text) && text.toLowerCase().contains("windows xp")) {
            List<SteamGameStats> gameList = new ArrayList<SteamGameStats>(1); // create an empty array of size 1
            gameList.add(steamGameStats); // add the game
            return gameList.iterator(); // return the iterator for the list
        }

        List<SteamGameStats> gameList = new ArrayList<SteamGameStats>(0); // create an empty array of size 1
        return gameList.iterator(); // return the iterator for the list
    }
}
