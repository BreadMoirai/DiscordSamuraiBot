package samurai.core;

import samurai.data.SamuraiGuild;
import samurai.data.SamuraiStore;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TonTL
 * @version 4.x - 3/9/2017
 */
public class GuildManager {

    private final ConcurrentHashMap<Long, SamuraiGuild> guildMap;

    GuildManager() {
        this.guildMap = new ConcurrentHashMap<>();
    }

    String getPrefix(long id) {
        if (guildMap.containsKey(id))
            return guildMap.get(id).getPrefix();
        else {
            if (SamuraiStore.guildExists(id)) {
                SamuraiGuild guild = SamuraiStore.readGuild(id);
                if (guild == null) {
                    Bot.log(String.format("Could not read data for Guild %d", id));
                    return "!";
                }
                guildMap.put(id, guild);
                guild.setScoreMap(SamuraiStore.readScores(id));
                return guild.getPrefix();
            } else {
                guildMap.put(id, new SamuraiGuild(id));
                return "!";
            }
        }
    }

    void shutdown() {
        for (SamuraiGuild g : guildMap.values()) {
            SamuraiStore.writeGuild(g);
            SamuraiStore.writeScoreData(g.getGuildId(), g.getScoreMap());
        }
    }

    void clearInactive() {
        guildMap.forEachValue(100L, guild -> {
            if (guild.isActive()) guild.setInactive();
            else {
                SamuraiStore.writeGuild(guild);
                if (!guildMap.remove(guild.getGuildId(), guild))
                    Bot.log("Failed to remove " + guild.getGuildId());
                SamuraiStore.writeScoreData(guild.getGuildId(), guild.getScoreMap());
            }
        });
    }

    SamuraiGuild getGuild(Long guildId) {
        return guildMap.get(guildId);
    }
}
