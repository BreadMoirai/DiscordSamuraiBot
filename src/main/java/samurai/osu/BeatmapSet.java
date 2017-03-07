package samurai.osu;

import org.json.JSONArray;
import org.json.JSONObject;
import samurai.Bot;
import samurai.data.SamuraiDatabase;
import samurai.osu.enums.GameMode;
import samurai.osu.enums.RankedStatus;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author TonTL
 * @version 4.x - 2/26/2017
 */
public class BeatmapSet implements Externalizable {

    private transient static final long serialVersionUID = 756680962858925830L;

    private String song, artist, mapper, source, tags;
    private int setId;
    private RankedStatus rankedStatus;
    private ArrayList<Beatmap> beatmapArrayList;
    private transient int current;

    public BeatmapSet() {
        current = -1;
    }


    public BeatmapSet(JSONArray jsonArray) {
        final int length = jsonArray.length();
        if (length == 0) {
            Bot.log("Empty Json Array");
            return;
        }
        {
            JSONObject o = jsonArray.getJSONObject(0);
            song = o.getString("title");
            artist = o.getString("artist");
            mapper = o.getString("creator");
            source = o.getString("source");
            tags = o.getString("tags");
            setId = o.getInt("beatmapset_id");
            rankedStatus = RankedStatus.fromAPI(o.getInt("approved"));
        }

        beatmapArrayList = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            JSONObject o = jsonArray.getJSONObject(i);
            Beatmap beatmap = new Beatmap()
                    .setMapID(o.getInt("beatmap_id"))
                    .setHash(o.getString("file_md5"))
                    .setDifficulty(o.getString("version"))
                    .setDifficultyRating(o.getDouble("difficultyrating"))
                    .setGameMode(GameMode.get(o.getInt("mode")))
                    .setTotalTime(o.getInt("total_length"))
                    .setDrainTime(o.getInt("hit_length"))
                    .setCs((float) o.getDouble("diff_size"))
                    .setOd((float) o.getDouble("diff_overall"))
                    .setAr((float) o.getDouble("diff_approach"))
                    .setHp((float) o.getDouble("diff_drain"));
            if (!o.get("max_combo").equals(JSONObject.NULL))
                beatmap.setMaxCombo(o.getInt("max_combo"));
            else beatmap.setMaxCombo(-1);
            beatmapArrayList.add(beatmap);
            SamuraiDatabase.put(beatmap.getHash(), beatmap.getMapID());
            SamuraiDatabase.put(beatmap.getMapID(), setId);
        }
        sort();
        current = -1;
    }

    public boolean hasCurrent() {
        return current != -1;
    }

    public void forward() {
        if (current < beatmapArrayList.size() - 1) current++;
    }

    public Beatmap current() {
        return beatmapArrayList.get(current);
    }

    public void back() {
        if (current > 0) current--;
    }


    public String getSong() {
        return song;
    }

    public String getArtist() {
        return artist;
    }

    public String getMapper() {
        return mapper;
    }

    public String getSource() {
        return source;
    }

    public String getTags() {
        return tags;
    }

    public int getSetId() {
        return setId;
    }

    public RankedStatus getRankedStatus() {
        return rankedStatus;
    }

    public ArrayList<Beatmap> getBeatmapArrayList() {
        return beatmapArrayList;
    }

    private void sort() {
        beatmapArrayList.sort(Comparator.comparingDouble(Beatmap::getDifficultyRating));
    }

    public Beatmap getBeatmapByHash(String hash) {
        for (int i = 0; i < beatmapArrayList.size(); i++) {
            final Beatmap beatmap = beatmapArrayList.get(i);
            if (beatmap.getHash().equals(hash)) {
                current = i;
                return beatmap;
            }
        }
        return null;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(setId);
        out.writeUTF(song);
        out.writeUTF(artist);
        out.writeUTF(mapper);
        out.writeUTF(source);
        out.writeUTF(tags);
        out.writeByte(beatmapArrayList.size());
        for (Beatmap b : beatmapArrayList) {
            out.writeInt(b.getMapID());
            out.writeUTF(b.getHash());
            out.writeByte(b.getGameMode().value());
            out.writeUTF(b.getDifficulty());
            out.writeDouble(b.getDifficultyRating());
            out.writeInt(b.getTotalTime());
            out.writeInt(b.getDrainTime());
            out.writeFloat(b.getAr());
            out.writeFloat(b.getCs());
            out.writeFloat(b.getHp());
            out.writeFloat(b.getOd());
            out.writeInt(b.getMaxCombo());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setId = in.readInt();
        song = in.readUTF();
        artist = in.readUTF();
        mapper = in.readUTF();
        source = in.readUTF();
        tags = in.readUTF();
        int mapSize = in.readUnsignedByte();
        beatmapArrayList = new ArrayList<>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            beatmapArrayList.add(new Beatmap()
                    .setMapID(in.readInt())
                    .setHash(in.readUTF())
                    .setGameMode(GameMode.get(in.readByte()))
                    .setDifficulty(in.readUTF())
                    .setDifficultyRating(in.readDouble())
                    .setTotalTime(in.readInt())
                    .setDrainTime(in.readInt())
                    .setAr(in.readFloat())
                    .setCs(in.readFloat())
                    .setHp(in.readFloat())
                    .setOd(in.readFloat())
                    .setMaxCombo(in.readInt()));
        }
    }


    public class Beatmap {

        private String difficulty, hash;
        private float ar, cs, hp, od;
        private int mapID, totalTime, drainTime, maxCombo;
        private GameMode gameMode;
        private double difficultyRating;

        public String getDifficulty() {
            return difficulty;
        }

        public Beatmap setDifficulty(String difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public String getHash() {
            return hash;
        }

        public Beatmap setHash(String hash) {
            this.hash = hash;
            return this;
        }

        public float getAr() {
            return ar;
        }

        public Beatmap setAr(float ar) {
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

        public Beatmap setHp(float hp) {
            this.hp = hp;
            return this;
        }

        public float getOd() {
            return od;
        }

        public Beatmap setOd(float od) {
            this.od = od;
            return this;
        }

        public int getMapID() {
            return mapID;
        }

        public Beatmap setMapID(int mapID) {
            this.mapID = mapID;
            return this;
        }

        public int getTotalTime() {
            return totalTime;
        }

        public Beatmap setTotalTime(int totalTime) {
            this.totalTime = totalTime;
            return this;
        }

        public int getDrainTime() {
            return drainTime;
        }

        public Beatmap setDrainTime(int drainTime) {
            this.drainTime = drainTime;
            return this;
        }

        public GameMode getGameMode() {
            return gameMode;
        }

        public Beatmap setGameMode(GameMode gameMode) {
            this.gameMode = gameMode;
            return this;
        }

        public double getDifficultyRating() {
            return difficultyRating;
        }

        public Beatmap setDifficultyRating(double difficultyRating) {
            this.difficultyRating = difficultyRating;
            return this;
        }

        public int getMaxCombo() {
            return maxCombo;
        }

        public Beatmap setMaxCombo(int maxCombo) {
            this.maxCombo = maxCombo;
            return this;
        }
    }
}
