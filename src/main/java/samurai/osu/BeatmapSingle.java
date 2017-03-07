package samurai.osu;

import samurai.osu.enums.GameMode;

import java.io.IOException;
import java.io.ObjectInput;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Created by TonTL on 1/23/2017.
 * Beatmap class
 */
public class BeatmapSingle {

    private transient static final long serialVersionUID = 756680962858925830L;

    private String song, artist, mapper, difficulty,
            hash/*, filename, foldername*/, source, tags/*,
            audiofile*/;
    private float ar, cs, hp, od;
    private int mapID, setID, totalTime, drainTime;
    private GameMode gameMode;
    //private Grade grade;
    //private RankedStatus rankedStatus;
    //private List<Map<Integer, Double>> starRating;
    private double difficultyRating;
    //private boolean isEmpty;

    public String getSong() {
        if (song == null) {
            return "null";
        }
        return song;
    }

    BeatmapSingle setSong(String song) {
        this.song = song;
        return this;
    }

    public String getArtist() {
        if (artist == null) {
            return "null";
        } return artist;
    }

    BeatmapSingle setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getMapper() {
        if (mapper == null) {
            return "null";
        } return mapper;
    }

    BeatmapSingle setMapper(String mapper) {
        this.mapper = mapper;
        return this;
    }

    public String getDifficulty() {
        if (difficulty == null) {
            return "null";
        } return difficulty;
    }

    BeatmapSingle setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public String getHash() {
        if (hash == null) {
            return "null";
        } return hash;
    }

    BeatmapSingle setHash(String hash) {
        this.hash = hash;
        return this;
    }
//
//    public String getFilename() {
//        if (filename == null) {
//            return "null";
//        } return filename;
//    }
//
//    public Beatmap setFilename(String filename) {
//        this.filename = filename;
//        return this;
//    }
//
//    public String getFoldername() {
//        if (foldername == null) {
//            return "null";
//        } return foldername;
//    }
//
//    public Beatmap setFoldername(String foldername) {
//        this.foldername = foldername;
//        return this;
//    }

    public String getSource() {
        if (source == null) {
            return "null";
        } return source;
    }

    BeatmapSingle setSource(String source) {
        this.source = source;
        return this;
    }

    public String getTags() {
        if (tags == null) {
            return "null";
        } return tags;
    }

    BeatmapSingle setTags(String tags) {
        this.tags = tags;
        return this;
    }

//    public String getAudiofile() {
//        if (audiofile == null) {
//            return "null";
//        } return audiofile;
//    }
//
//    public Beatmap setAudiofile(String audiofile) {
//        this.audiofile = audiofile;
//        return this;
//    }

    public float getAr() {
        return ar;
    }

    BeatmapSingle setAr(float ar) {
        this.ar = ar;
        return this;
    }

    public float getCs() {
        return cs;
    }

    public BeatmapSingle setCs(float cs) {
        this.cs = cs;
        return this;
    }

    public float getHp() {
        return hp;
    }

    BeatmapSingle setHp(float hp) {
        this.hp = hp;
        return this;
    }

    public float getOd() {
        return od;
    }

    BeatmapSingle setOd(float od) {
        this.od = od;
        return this;
    }

    public int getMapID() {
        return mapID;
    }

    BeatmapSingle setMapID(int mapID) {
        this.mapID = mapID;
        return this;
    }

    public int getSetID() {
        return setID;
    }

    public BeatmapSingle setSetID(int setID) {
        this.setID = setID;
        return this;
    }

    public int getTotalTime() {
        return totalTime;
    }

    BeatmapSingle setTotalTime(int totalTime) {
        this.totalTime = totalTime;
        return this;
    }

    public int getDrainTime() {
        return drainTime;
    }

    BeatmapSingle setDrainTime(int drainTime) {
        this.drainTime = drainTime;
        return this;
    }

    public GameMode getGameMode() {
        if (gameMode==null) {
            return GameMode.OSU;
        }
        return gameMode;
    }

    BeatmapSingle setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        return this;
    }

//    public Grade getGrade() {
//        return grade;
//    }
//
//    public Beatmap setGrade(Grade grade) {
//        this.grade = grade;
//        return this;
//    }

//    public RankedStatus getRankedStatus() {
//        if (rankedStatus == null) {
//            return RankedStatus.UNKNOWN;
//        }
//        return rankedStatus;
//    }

//    Beatmap setRankedStatus(RankedStatus rankedStatus) {
//        this.rankedStatus = rankedStatus;
//        return this;
//    }

//    List<Map<Integer, Double>> getStarRating() {
//        return starRating;
//    }
//
//    public Beatmap setStarRating(List<Map<Integer, Double>> starRating) {
//        this.starRating = starRating;
//        return this;
//    }

    public double getDifficultyRating() {
        return difficultyRating;
    }

    BeatmapSingle setDifficultyRating(double difficultyRating) {
        this.difficultyRating = difficultyRating;
        return this;
    }

//    public boolean isEmpty() {
//        return isEmpty;
//    }
//
//    public Beatmap setEmpty(boolean empty) {
//        isEmpty = empty;
//        return this;
//    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Beatmap{");
        sb.append("song='").append(song).append('\'');
        sb.append(", artist='").append(artist).append('\'');
        sb.append(", mapper='").append(mapper).append('\'');
        sb.append(", difficulty='").append(difficulty).append('\'');
        sb.append(", hash='").append(hash).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", tags='").append(tags).append('\'');
        sb.append(", ar=").append(ar);
        sb.append(", cs=").append(cs);
        sb.append(", hp=").append(hp);
        sb.append(", od=").append(od);
        sb.append(", mapID=").append(mapID);
        sb.append(", setID=").append(setID);
        sb.append(", totalTime=").append(totalTime);
        sb.append(", drainTime=").append(drainTime);
        sb.append(", gameMode=").append(gameMode);
        sb.append(", difficultyRating=").append(difficultyRating);
        sb.append('}');
        return sb.toString();
    }


    public byte[] toBytes() {
        StandardCharsets.UTF_8.encode("34");
        ByteBuffer buffer = ByteBuffer.allocateDirect(0);
        return null;
    }


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}

