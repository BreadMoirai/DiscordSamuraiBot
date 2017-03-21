package samurai.osu.tracker;

import samurai.osu.entities.Score;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author TonTL
 * @version 5.x - 3/19/2017
 */
public class ScoreCache {

    private HashSet<Score> scores;

    public ScoreCache() {
        this.scores = new HashSet<>();
    }

    public List<Score> addScores(List<Score> userRecent) {
        ArrayList<Score> newScores = new ArrayList<>();
        for (Score s : userRecent) {
            if (scores.add(s)) {
                newScores.add(s);
            }
        }
        return newScores;
    }
}
