package samurai.osu.model;


import samurai.files.SamuraiStore;
import samurai.osu.enums.GameMode;
import samurai.osu.enums.Grade;
import samurai.osu.enums.Mod;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.List;

/**
 * Created by TonTL on 1/23/2017.
 * Score
 */
public class Score {
    private GameMode mode;
    private String beatmapHash, player, replayHash;
    private Grade grade;
    private short count300, count100, count50, geki, katu, count0;
    private int score;
    private short maxCombo;
    private boolean perfectCombo;
    private int modCombo;
    private long timestamp;
    private long onlineScoreID;
    private int beatmapId;

    public float getAccuracy() {
        float maxHitPoints = (count0 + count50 + count100 + count300) * 300;
        float totalHitPoints = (count50*50) + (count100*100) + (count300*300);
        return totalHitPoints/maxHitPoints;
    }

    public Grade getGrade() {
        if (grade == null) {
            setGrade(calculateGrade());
        }
        return grade;
    }

    public Grade calculateGrade() {
        float countTotal = count0 + count50 + count100 + count300;
        float percent300 = (float)count300/countTotal;
        List<Mod> mods = Mod.getMods(modCombo);
        boolean h = mods.contains(Mod.Hidden) || mods.contains(Mod.Flashlight);

        if (percent300 == 1.00) {
            if (h)
                return Grade.XH;
            else
                return Grade.X;
        } else if (percent300 > 0.90 && (float)count50/countTotal < 0.10 && count0==0) {
            if (h)
                return Grade.SH;
            else
                return Grade.S;
        } else if (percent300 > 0.80 && (count0==0 || percent300 > 0.90)) {
            return Grade.A;
        } else if (percent300 > 0.70 && (count0==0 || percent300 > 0.80)) {
            return Grade.B;
        } else if (percent300 > 0.60) {
            return Grade.C;
        } else {
            return Grade.D;
        }
    }

    public int getBeatmapId() {
        return beatmapId;
    }

    public Score setBeatmapId(int beatmapId) {
        this.beatmapId = beatmapId;
        return this;
    }


    public Score setGrade(Grade grade) {
        this.grade = grade;
        return this;
    }

    public GameMode getMode() {
        return mode;
    }

    public Score setMode(GameMode mode) {
        this.mode = mode;
        return this;
    }

    public String getBeatmapHash() {
        if (beatmapHash == null) {
            return "null";
        }
        return beatmapHash;
    }

    public Score setBeatmapHash(String beatmapHash) {
        this.beatmapHash = beatmapHash;
        return this;
    }

    public String getPlayer() {
        return player;
    }

    public Score setPlayer(String player) {
        this.player = player;
        return this;
    }

    public String getReplayHash() {
        return replayHash;
    }

    public Score setReplayHash(String replayHash) {
        this.replayHash = replayHash;
        return this;
    }

    public short getCount300() {
        return count300;
    }

    public Score setCount300(short count300) {
        this.count300 = count300;
        return this;
    }

    public short getCount100() {
        return count100;
    }

    public Score setCount100(short count100) {
        this.count100 = count100;
        return this;
    }

    public short getCount50() {
        return count50;
    }

    public Score setCount50(short count50) {
        this.count50 = count50;
        return this;
    }

    public short getGeki() {
        return geki;
    }

    public Score setGeki(short geki) {
        this.geki = geki;
        return this;
    }

    public short getKatu() {
        return katu;
    }

    public Score setKatu(short katu) {
        this.katu = katu;
        return this;
    }

    public int getScore() {
        return score;
    }

    public Score setScore(int score) {
        this.score = score;
        return this;
    }

    public short getMaxCombo() {
        return maxCombo;
    }

    public Score setMaxCombo(short maxCombo) {
        this.maxCombo = maxCombo;
        return this;
    }

    public boolean isPerfectCombo() {
        return perfectCombo;
    }

    public Score setPerfectCombo(boolean perfectCombo) {
        this.perfectCombo = perfectCombo;
        return this;
    }

    public int getModCombo() {
        return modCombo;
    }

    public Score setModCombo(int modCombo) {
        this.modCombo = modCombo;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Score setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getOnlineScoreID() {
        return onlineScoreID;
    }

    public Score setOnlineScoreID(long onlineScoreID) {
        this.onlineScoreID = onlineScoreID;
        return this;
    }

    public short getCount0() {
        return count0;
    }

    public Score setCount0(short count0) {
        this.count0 = count0;
        return this;
    }

    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10*Byte.BYTES + 7*Short.BYTES + 4*Integer.BYTES + 2*Long.BYTES + beatmapHash.length() + player.length() + replayHash.length());
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put((byte) mode.value());
        byteBuffer.putInt(SamuraiStore.VERSION);
        byteBuffer.put((byte) 0x0b);
        byteBuffer.put((byte) beatmapHash.length());
        for (int i = 0; i < beatmapHash.length(); i++) {
            byteBuffer.put((byte) beatmapHash.charAt(i));
        }
        byteBuffer.put((byte) 0x0b);
        byteBuffer.put((byte) player.length());
        for (int i = 0; i < player.length(); i++) {
            byteBuffer.put((byte) player.charAt(i));
        }
        byteBuffer.put((byte) 0x0b);
        byteBuffer.put((byte) replayHash.length());
        for (int i = 0; i < replayHash.length(); i++) {
            byteBuffer.put((byte) replayHash.charAt(i));
        }
        byteBuffer.putShort(count300);
        byteBuffer.putShort(count100);
        byteBuffer.putShort(count50);
        byteBuffer.putShort(geki);
        byteBuffer.putShort(katu);
        byteBuffer.putShort(count0);
        byteBuffer.putInt(score);
        byteBuffer.putShort(maxCombo);
        byteBuffer.put((byte) (perfectCombo ? 0x01 : 0x00));
        byteBuffer.putInt(modCombo);
        byteBuffer.put((byte) 0x0b);
        byteBuffer.put((byte) 0x00);
        byteBuffer.putLong(timestamp);
        byteBuffer.putInt(0xffffffff);
        byteBuffer.putLong(onlineScoreID);
        return byteBuffer.array();
    }

    public boolean passed() {
        return grade != Grade.F;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Score score1 = (Score) o;

        if (count300 != score1.count300) return false;
        if (count100 != score1.count100) return false;
        if (count50 != score1.count50) return false;
        if (geki != score1.geki) return false;
        if (katu != score1.katu) return false;
        if (count0 != score1.count0) return false;
        if (score != score1.score) return false;
        if (maxCombo != score1.maxCombo) return false;
        if (modCombo != score1.modCombo) return false;
        if (mode != score1.mode) return false;
        return player.equals(score1.player);
    }

    @Override
    public int hashCode() {
        int result = mode.hashCode();
        result = 31 * result + beatmapHash.hashCode();
        result = 31 * result + player.hashCode();
        result = 31 * result + (int) count300;
        result = 31 * result + (int) count100;
        result = 31 * result + (int) count50;
        result = 31 * result + (int) geki;
        result = 31 * result + (int) katu;
        result = 31 * result + score;
        result = 31 * result + (int) maxCombo;
        result = 31 * result + modCombo;
        return result;
    }

    public String toString() {
        return "[" + Instant.ofEpochMilli(timestamp).toString() + "] " + player + " " + score;
    }
}
