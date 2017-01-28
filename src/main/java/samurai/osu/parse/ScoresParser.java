package samurai.osu.parse;

import samurai.osu.Score;
import samurai.osu.enums.GameMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TonTL on 1/23/2017.
 * parses score.db
 */
public class ScoresParser extends Parser {

    private int version, count;

    private Map<String, List<Score>> beatmapScores;


    public ScoresParser(String filepath) throws FileNotFoundException {
        super(new FileInputStream(filepath));
    }


    @Override
    public ScoresParser parse() throws IOException {
        version = nextInt();
        count = nextInt();
        beatmapScores = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            String hash = nextString();
            int scoreCount = nextInt();
            List<Score> scoreList = new ArrayList<>(scoreCount);
            for (int j = 0; j < scoreCount; j++) {
                scoreList.add(nextScore());
            }
            beatmapScores.put(hash, scoreList);
        }
        return this;
    }

    private Score nextScore() {
        Score score = null;
        try {
            score = new Score()
                    .setMode(GameMode.get(nextByte()))
                    .setVersion(nextInt())
                    .setBeatmapHash(nextString())
                    .setPlayer(nextString())
                    .setReplayHash(nextString())
                    .setCount300(nextShort())
                    .setCount100(nextShort())
                    .setCount50(nextShort())
                    .setGeki(nextShort())
                    .setKatu(nextShort())
                    .setCount0(nextShort())
                    .setScore(nextInt())
                    .setMaxCombo(nextShort())
                    .setPerfectCombo(nextByte() != 0x00)
                    .setModCombo(nextInt());
            nextString();
            score.setTimestamp(nextLong());
            skip(4);
            score.setOnlineScoreID(nextLong());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return score;
    }

    public int getVersion() {
        return version;
    }

    public int getCount() {
        return count;
    }

    public Map<String, List<Score>> getBeatmapScores() {
        return beatmapScores;
    }
}
