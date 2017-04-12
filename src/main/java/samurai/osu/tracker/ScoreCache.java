package samurai.osu.tracker;

import net.dv8tion.jda.core.entities.Message;
import samurai.osu.model.Score;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    public Message getSessionStats() {
        return null;
    }
}
