package samurai.osu;

import samurai.osu.enums.GameMode;
import samurai.osu.enums.Grade;
import samurai.osu.enums.RankedStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TonTL on 1/23/2017.
 * Beatmap class
 */
public class Beatmap {
    private String song, artist, mapper, difficulty,
            hash, filename, foldername, source, tags,
            audiofile;
    private float ar, cs, hp, od;
    private int mapID, setID, totalTime, drainTime;
    private GameMode gameMode;
    private Grade grade;
    private RankedStatus rankedStatus;
    private List<Map<Integer, Double>> starRating;
    private List<Score> scores;
    private double difficultyRating;
    private boolean isEmpty;

    Beatmap() {
        isEmpty = false;
        scores = new ArrayList<>();
    }

    public String getSong() {
        if (song == null) {
            return "null";
        }
        return song;
    }

    Beatmap setSong(String song) {
        this.song = song;
        return this;
    }

    public String getArtist() {
        if (artist == null) {
            return "null";
        } return artist;
    }

    Beatmap setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getMapper() {
        if (mapper == null) {
            return "null";
        } return mapper;
    }

    Beatmap setMapper(String mapper) {
        this.mapper = mapper;
        return this;
    }

    public String getDifficulty() {
        if (difficulty == null) {
            return "null";
        } return difficulty;
    }

    Beatmap setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public String getHash() {
        if (hash == null) {
            return "null";
        } return hash;
    }

    Beatmap setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getFilename() {
        if (filename == null) {
            return "null";
        } return filename;
    }

    public Beatmap setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getFoldername() {
        if (foldername == null) {
            return "null";
        } return foldername;
    }

    public Beatmap setFoldername(String foldername) {
        this.foldername = foldername;
        return this;
    }

    public String getSource() {
        if (source == null) {
            return "null";
        } return source;
    }

    Beatmap setSource(String source) {
        this.source = source;
        return this;
    }

    public String getTags() {
        if (tags == null) {
            return "null";
        } return tags;
    }

    Beatmap setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public String getAudiofile() {
        if (audiofile == null) {
            return "null";
        } return audiofile;
    }

    public Beatmap setAudiofile(String audiofile) {
        this.audiofile = audiofile;
        return this;
    }

    public float getAr() {
        return ar;
    }

    Beatmap setAr(float ar) {
        this.ar = ar;
        return this;
    }

    public float getCs() {
        return cs;
    }

    public Beatmap setCs(float cs) {
        this.cs = cs;
        return this;
    }

    public float getHp() {
        return hp;
    }

    Beatmap setHp(float hp) {
        this.hp = hp;
        return this;
    }

    public float getOd() {
        return od;
    }

    Beatmap setOd(float od) {
        this.od = od;
        return this;
    }

    public int getMapID() {
        return mapID;
    }

    Beatmap setMapID(int mapID) {
        this.mapID = mapID;
        return this;
    }

    public int getSetID() {
        return setID;
    }

    public Beatmap setSetID(int setID) {
        this.setID = setID;
        return this;
    }

    public int getTotalTime() {
        return totalTime;
    }

    Beatmap setTotalTime(int totalTime) {
        this.totalTime = totalTime;
        return this;
    }

    public int getDrainTime() {
        return drainTime;
    }

    Beatmap setDrainTime(int drainTime) {
        this.drainTime = drainTime;
        return this;
    }

    public GameMode getGameMode() {
        if (gameMode==null) {
            return GameMode.OSU;
        }
        return gameMode;
    }

    Beatmap setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        return this;
    }

    public Grade getGrade() {
        return grade;
    }

    public Beatmap setGrade(Grade grade) {
        this.grade = grade;
        return this;
    }

    public RankedStatus getRankedStatus() {
        if (rankedStatus == null) {
            return RankedStatus.UNKNOWN;
        }
        return rankedStatus;
    }

    Beatmap setRankedStatus(RankedStatus rankedStatus) {
        this.rankedStatus = rankedStatus;
        return this;
    }

    List<Map<Integer, Double>> getStarRating() {
        return starRating;
    }

    public Beatmap setStarRating(List<Map<Integer, Double>> starRating) {
        this.starRating = starRating;
        return this;
    }

    public List<Score> getScores() {
        return scores;
    }

    public Beatmap setScores(List<Score> scores) {
        this.scores = scores;
        scores.sort((o1, o2) -> o2.getScore() - o1.getScore());
        return this;
    }

    void appendScores(List<Score> scores) {
        this.scores.addAll(scores);
        scores.sort((o1, o2) -> o2.getScore() - o1.getScore());
    }


    @Override
    public String toString() {
        return "Beatmap{" +
                "song='" + song + '\'' +
                ", artist='" + artist + '\'' +
                ", mapper='" + mapper + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", hash='" + hash + '\'' +
                ", filename='" + filename + '\'' +
                ", foldername='" + foldername + '\'' +
                ", source='" + source + '\'' +
                ", tags='" + tags + '\'' +
                ", audiofile='" + audiofile + '\'' +
                ", ar=" + ar +
                ", cs=" + cs +
                ", hp=" + hp +
                ", od=" + od +
                ", mapID=" + mapID +
                ", setID=" + setID +
                ", totalTime=" + totalTime +
                ", drainTime=" + drainTime +
                ", gameMode=" + gameMode +
                ", grade=" + grade +
                ", rankedStatus=" + rankedStatus +
                ", starRating=" + starRating +
                ", osu=" + scores +
                '}';
    }

    public double getDifficultyRating() {
        return difficultyRating;
    }

    Beatmap setDifficultyRating(double difficultyRating) {
        this.difficultyRating = difficultyRating;
        return this;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public Beatmap setEmpty(boolean empty) {
        isEmpty = empty;
        return this;
    }

    public boolean hasScores() {
        return !scores.isEmpty();
    }
}

