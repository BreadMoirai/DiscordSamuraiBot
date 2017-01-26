
package dreadmoirais.samurais.osu.parse;

import dreadmoirais.samurais.osu.Beatmap;
import dreadmoirais.samurais.osu.enums.GameMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by TonTL on 1/23/2017.
 * Parses an osu.db file
 */
public class OsuParser extends Parser {

    private int versionNumber, folderCount, totalBeatmaps, proccessedTotal;
    private String playerName;
    private HashMap<String, Beatmap> beatmaps;

    public OsuParser(String filepath) throws FileNotFoundException {
        super(new FileInputStream(filepath));
        proccessedTotal = 0;
    }

    public OsuParser parse() throws IOException {
        versionNumber = nextInt();
        folderCount = nextInt();
        skip(9);
        playerName = nextString();
        totalBeatmaps = nextInt();

        System.out.println("Version: " + versionNumber +
                "\nFolderCount: " + folderCount +
                "\nPlayerName: " + playerName +
                "\nTotalBeatmaps: " + totalBeatmaps);

        beatmaps = new HashMap<>(totalBeatmaps);

        for (int i = 0; i < totalBeatmaps; i++) {
            int beatmapSize = nextInt();
            byte[] b = new byte[beatmapSize];

            in.read(b);

            Beatmap beatmap = new BeatmapParser(b).parse().getBeatmap();
            if (beatmap.getGameMode() == GameMode.OSU) {
                beatmaps.put(beatmap.getHash(), beatmap);
            }
            proccessedTotal++;
        }

        int unknown = nextInt();
        if (unknown != 4) {
            System.out.println("Beatmaps Parsed: " + proccessedTotal);
        }
        if (in.read() == -1) {
            System.out.println("Parsing Complete.");
        }
        in.close();
        return this;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public int getFolderCount() {
        return folderCount;
    }

    public int getTotalBeatmaps() {
        return totalBeatmaps;
    }

    public int getProccessedTotal() {
        return proccessedTotal;
    }

    public String getPlayerName() {
        return playerName;
    }

    public HashMap<String, Beatmap> getBeatmaps() {
        return beatmaps;
    }
}

