package dreadmoirais.samurais.osu.parse;

import dreadmoirais.samurais.osu.Beatmap;

import java.io.ByteArrayInputStream;

/**
 * Created by TonTL on 1/23/2017.
 * Parse beatmap from byte[]
 */
public class BeatmapParser extends Parser {

    private Beatmap bmap;

    public BeatmapParser(byte[] b) {
        super(new ByteArrayInputStream(b));
        bmap = new Beatmap();
        parse();
    }

    void parse() {
        bmap.setArtist(nextString()); //artist
        nextString();
        bmap.setSong(nextString()); //song
        nextString();
        bmap.setMapper(nextString()); //Mapper
        bmap.setDifficulty(nextString()); //Diff
        nextString();
        bmap.setHash(nextString()); //Hash
        bmap.setFilename(nextString()); //filename
        bmap.setRankedStatus(nextByte()); //rankedStatus

        //skips extra information not relevant
        skip(38);
        for (int i = 0; i < 4; i++) {
            int pairs = nextInt();
            skip(pairs*14);
        }
        skip(12);
        int timings = nextInt();
        //System.out.println(timings);
        skip(timings*17);
        //System.out.println(bytesToHex(Arrays.copyOfRange(b, itr, itr + 6)));
        bmap.setMapID(nextInt());
        bmap.setSetID(nextInt());
        skip(14);
        bmap.setMode(nextByte());
        bmap.setSource(nextString());
        bmap.setTags(nextString());
        skip(3);
        nextString();
        skip(10);
        bmap.setFoldername(nextString());

        //System.out.println(bmap);
    }

    Beatmap getBeatmap() {return bmap;}

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
