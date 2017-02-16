package samurai.data;

import samurai.osu.Score;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * manages guild specific osu data!
 * Created by TonTL on 2/3/2017.
 */
public class SamuraiGuild {

    private HashMap<String, LinkedList<Score>> scoreMap;
    private LinkedList<SamuraiUser> userList;
    private boolean active;

    public SamuraiGuild(HashMap<String, LinkedList<Score>> scoreMap) {
        userList = new LinkedList<>();
        this.scoreMap = scoreMap;
        active = false;
    }

    public SamuraiGuild() {
        this(new HashMap<>());
    }

    public int getUserCount() {
        return userList.size();
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
