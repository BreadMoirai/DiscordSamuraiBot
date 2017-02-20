package samurai.data;

import samurai.osu.Score;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

/**
 * manages guild specific osu data!
 * Created by TonTL on 2/3/2017.
 */
public class SamuraiGuild implements Externalizable {

    private String prefix;
    private long guildId;
    private transient HashMap<String, LinkedList<Score>> scoreMap;
    private HashMap<Long, SamuraiUser> userMap;
    private ArrayList<SamuraiChart> charts;
    private boolean active;

    public SamuraiGuild() {
    }

    public SamuraiGuild(long guildId) {
        prefix = "!";
        this.guildId = guildId;
        scoreMap = new HashMap<>();
        userMap = new HashMap<>();
        charts = new ArrayList<>();
        active = false;
    }

    public int getUserCount() {
        return userMap.size();
    }

    public HashMap<String, LinkedList<Score>> getScoreMap() {
        return scoreMap;
    }

    public int mergeScoreMap(HashMap<String, LinkedList<Score>> source) {
        int scoresMerged = 0;
        for (Map.Entry<String, LinkedList<Score>> sourceEntry : source.entrySet()) {
            if (scoreMap.containsKey(sourceEntry.getKey())) {
                List<Score> destinationScores = scoreMap.get(sourceEntry.getKey());
                for (Score sourceScore : sourceEntry.getValue())
                    if (!destinationScores.contains(sourceScore)) {
                        destinationScores.add(sourceScore);
                        scoresMerged++;
                    }
            } else {
                scoreMap.put(sourceEntry.getKey(), sourceEntry.getValue());
                scoresMerged += sourceEntry.getValue().size();
            }
        }
        return scoresMerged;
    }

    public int getScoreCount() {
        int scoreCount = 0;
        for (LinkedList<Score> scoreList : scoreMap.values()) scoreCount += scoreList.size();
        return scoreCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setInactive() {
        this.active = false;
    }

    public HashMap<Long, SamuraiUser> getUserMap() {
        return userMap;
    }

    public long getGuildId() {
        return guildId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SamuraiGuild that = (SamuraiGuild) o;

        return guildId == that.guildId;
    }

    @Override
    public int hashCode() {
        return (int) (guildId ^ (guildId >>> 32));
    }


    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(prefix);
        out.writeLong(guildId);
        out.writeShort(userMap.size());
        for (SamuraiUser u : userMap.values()) {
            out.writeLong(u.getDiscordId());
            out.writeInt(u.getOsuId());
            out.writeUTF(u.getOsuName());
            out.writeInt(u.getG_rank());
            out.writeInt(u.getC_rank());
            out.writeShort(u.getL_rank());
        }
        out.writeShort(charts.size());
        for (SamuraiChart c : charts) {
            out.writeInt(c.getChartId());
            out.writeUTF(c.getChartName());
            out.writeByte(c.getBeatmapIds().size());
            for (int i : c.getBeatmapIds())
                out.writeInt(i);
        }
        System.out.println(this);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        prefix = in.readUTF();
        guildId = in.readLong();
        userMap = new HashMap<>();
        for (int i = 0, size = in.readShort(); i < size; i++) {
            long id = in.readLong();
            userMap.put(id, new SamuraiUser(id, in.readInt(), in.readUTF(), in.readInt(), in.readInt(), in.readShort()));
        }
        charts = new ArrayList<>();
        for (int i = 0, size = in.readShort(); i < size; i++) {
            int id = in.readInt();
            String name = in.readUTF();
            int chartSize = in.readByte();
            ArrayList<Integer> chart = new ArrayList<>(chartSize);
            for (int j = 0; j < chartSize; j++) {
                chart.add(in.readInt());
            }
            charts.add(new SamuraiChart(id, name, chart));
        }
        scoreMap = new HashMap<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SamuraiGuild{");
        sb.append("\n\tprefix='").append(prefix).append('\'');
        sb.append("\n\tguildId=").append(guildId);
        sb.append("\n\tscoreCount=").append(getScoreCount());
        sb.append("\n\tuserMap=").append(userMap);
        sb.append("\n\tcharts=").append(charts);
        sb.append("\n\tactive=").append(active);
        sb.append("\n}");
        return sb.toString();
    }
}
