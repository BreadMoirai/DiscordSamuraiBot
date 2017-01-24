package dreadmoirais.samurais.osu.parse;

import dreadmoirais.samurais.osu.Beatmap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by TonTL on 1/23/2017.
 * Parses an osu.db file
 */
public class OsuParser extends Parser {

    private int versionNumber, folderCount, totalBeatmaps, proccessedTotal;
    private String playerName;
    private List<Beatmap> beatmaps;

    public OsuParser(String fileIn) {
        proccessedTotal = 0;
        try {
            in = new FileInputStream(fileIn);
        }
        catch(FileNotFoundException e) {
            System.err.println("File not Found\n" + e.getMessage());
            return;
        }
        parse();
    }

    void parse() {
        versionNumber = nextInt();
        folderCount = nextInt();
        skip(9);
        playerName = nextString();
        totalBeatmaps = nextInt();


        System.out.println("Version: " + versionNumber +
                "\nFolderCount: " + folderCount +
                "\nPlayerName: " + playerName +
                "\nTotalBeatmaps: " + totalBeatmaps);


        beatmaps = new ArrayList<>(totalBeatmaps);

        for (int i = 0; i < totalBeatmaps; i++) {
            int beatmapSize = nextInt();
            byte[] b = new byte[beatmapSize];
            try {
                in.read(b);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            BeatmapParser bparser = new BeatmapParser(b);
            beatmaps.put(bparser.getBeatmap().getHash(), bparser.getBeatmap());
            proccessedTotal++;
        }

        int unknown = nextInt();
        if ( unknown != 4) {
            System.out.println("Beatmaps Parsed: " + proccessedTotal);
        }
        try {
            if (in.read() == -1) {
                System.out.println("Parsing Complete.");
            }
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
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

