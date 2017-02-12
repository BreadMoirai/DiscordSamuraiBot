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

    public OsuGuild() {
        this(new HashMap<>());
    }

    public HashMap<String, LinkedList<Score>> getScoreMap() {
        return scoreMap;
    }


    public int mergeScoreMap(HashMap<String, LinkedList<Score>> source) {
        int scoresMerged = 0;
        for (String hash : source.keySet()) {
            if (scoreMap.containsKey(hash)) {
                List<Score> destinationScores = scoreMap.get(hash);
                for (Score sourceScore : source.get(hash))
                    if (!destinationScores.contains(sourceScore)) {
                        destinationScores.add(sourceScore);
                        scoresMerged++;
                    }
            } else {
                scoreMap.put(hash, source.get(hash));
                scoresMerged += source.get(hash).size();
            }
        }
        return scoresMerged;
    }

    public boolean isActive() {
        return active;
    }

    public void setInactive() {
        this.active = false;
    }

    public int getScoreCount() {
        int scoreCount = 0;
        for (LinkedList<Score> scoreList : scoreMap.values()) scoreCount += scoreList.size();
        return scoreCount;
    }
}
