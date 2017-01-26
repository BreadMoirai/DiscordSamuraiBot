package dreadmoirais.samurais.osu.parse;

import dreadmoirais.samurais.osu.Beatmap;
import dreadmoirais.samurais.osu.enums.Grade;
import dreadmoirais.samurais.osu.enums.GameMode;
import dreadmoirais.samurais.osu.enums.RankedStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TonTL on 1/23/2017.
 * Parse beatmap from byte[]
 */
public class BeatmapParser extends Parser {

    private Beatmap beatmap;

    BeatmapParser(byte[] b) {
        super(new ByteArrayInputStream(b));
        beatmap = new Beatmap();
    }

    public BeatmapParser parse() throws IOException {
        beatmap.setArtist(nextString());
        nextString(); //unicode Artist
        beatmap.setSong(nextString());
        nextString(); //unicode song
        beatmap.setMapper(nextString())
                .setDifficulty(nextString())
                .setAudiofile(nextString())
                .setHash(nextString())
                .setFilename(nextString())
                .setRankedStatus(RankedStatus.get(nextByte()));
        skip(14); //# of circles, sliders, and spinners & last modified time
        beatmap.setAr(nextSingle())
                .setCs(nextSingle())
                .setHp(nextSingle())
                .setOd(nextSingle());
        skip(8); //slider velocity
        List<Map<Integer, Double>> starRating = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            starRating.add(nextIntDoublePairs());
        }
        beatmap.setStarRating(starRating)
                .setDrainTime(nextInt())
                .setTotalTime(nextInt());
        skip(4); //audioPreview time
        skip(nextInt()*17); //timing points

        beatmap.setMapID(nextInt())
                .setSetID(nextInt());
        skip(4);
        beatmap.setGrade(Grade.get(nextByte()));
        skip(9);
        beatmap.setGameMode(GameMode.get(nextByte()))
                .setSource(nextString())
                .setTags(nextString());
        skip(2);
        nextString();
        skip(10);
        beatmap.setFoldername(nextString());
        skip(18);

        beatmap.setDifficultyRating(starRating.get(beatmap.getGameMode().value()).get(0));
        return this;
    }

    Beatmap getBeatmap() {return beatmap;}

    //debugging
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
