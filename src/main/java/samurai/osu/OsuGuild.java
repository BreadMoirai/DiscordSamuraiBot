package samurai.osu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * manages guild specific osu data!
 * Created by TonTL on 2/3/2017.
 */
public class OsuGuild {

    private HashMap<String, LinkedList<Score>> scoreMap;
    private boolean active;

    public OsuGuild(HashMap<String, LinkedList<Score>> scoreMap) {
        this.scoreMap = scoreMap;
        active = true;
    }

    public HashMap<String, LinkedList<Score>> getScoreMap() {
        return scoreMap;
    }


    public void mergeScoreMap(HashMap<String, LinkedList<Score>> source) {
        for (String hash : source.keySet()) {
            if (scoreMap.containsKey(hash)) {
                List<Score> destinationScores = scoreMap.get(hash);
                for (Score sourceScore : source.get(hash)) {
                    destinationScores.add(sourceScore);
                }
            } else {
                scoreMap.put(hash, (LinkedList<Score>) source.get(hash));
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
