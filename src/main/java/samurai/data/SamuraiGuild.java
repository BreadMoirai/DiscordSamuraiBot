package samurai.data;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import samurai.Bot;
import samurai.osu.Score;

import java.util.*;

/**
 * manages guild specific osu data!
 * Created by TonTL on 2/3/2017.
 */
public class SamuraiGuild {

    private String prefix;
    private long guildId;
    private HashMap<String, LinkedList<Score>> scoreMap;
    private HashMap<Long, SamuraiUser> userMap;
    private ArrayList<Chart> charts;
    private boolean active;

    public SamuraiGuild(String prefix, Guild guild) {
        this.prefix = prefix;
        this.guildId = Long.parseLong(guild.getId());
        if (SamuraiFile.hasFile(guildId)) {
            SamuraiFile.readGuildDataInto(this);
        } else {
            Bot.log(String.format("Creating new Guild Object for %s[%d].", guild.getName(), guildId));
            userMap = new HashMap<>();
            scoreMap = new HashMap<>();
            charts = new ArrayList<>();
            List<Member> memberList = guild.getMembers();
            for (Member m : memberList) {
                userMap.put(Long.parseLong(m.getUser().getId()), new SamuraiUser(Long.parseLong(m.getUser().getId()), 0, null));
            }
        }
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

}
