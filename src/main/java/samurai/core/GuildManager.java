package samurai.core;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.User;
import samurai.Bot;
import samurai.data.SamuraiStore;
import samurai.entities.SamuraiGuild;
import samurai.entities.SamuraiUser;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 4.x - 3/9/2017
 */
public class GuildManager {

    private final ConcurrentHashMap<Long, SamuraiGuild> guildMap;

    public GuildManager() {
        this.guildMap = new ConcurrentHashMap<>();
    }

    public String getPrefix(long id) {
        if (guildMap.containsKey(id))
            return guildMap.get(id).getPrefix();
        else {
            if (SamuraiStore.guildExists(id)) {
                SamuraiGuild guild = SamuraiStore.readGuild(id);
                if (guild == null) {
                    System.err.println("Could not read data for Guild " + id);
                    return Bot.DEFAULT_PREFIX;
                }
                guildMap.put(id, guild);
                guild.setScoreMap(SamuraiStore.readScores(id));
                return guild.getPrefix();
            } else {
                guildMap.put(id, new SamuraiGuild(id));
                return Bot.DEFAULT_PREFIX;
            }
        }
    }

    void clearInactive() {
        guildMap.forEachValue(100L, guild -> {
            if (guild.isActive()) guild.setInactive();
            else {
                SamuraiStore.writeGuild(guild);
                if (!guildMap.remove(guild.getGuildId(), guild))
                    System.err.println("Failed to remove " + guild.getGuildId());
                SamuraiStore.writeScoreData(guild.getGuildId(), guild.getScoreMap());
            }
        });
    }

    public SamuraiGuild getGuild(Long guildId) {
        return guildMap.get(guildId);
    }

    public List<SamuraiUser> getUser(User user) {
        final long id = Long.parseLong(user.getId());
        return user.getMutualGuilds().stream().map(ISnowflake::getId).mapToLong(Long::parseLong).mapToObj(guildMap::get).filter(samuraiGuild -> samuraiGuild.hasUser(id)).map(samuraiGuild -> samuraiGuild.getUser(id)).collect(Collectors.toList());
    }

    public void shutdown() {
        for (SamuraiGuild g : guildMap.values()) {
            SamuraiStore.writeGuild(g);
            SamuraiStore.writeScoreData(g.getGuildId(), g.getScoreMap());
        }
    }

    public void refresh(List<Guild> guilds) {
        for (Guild g : guilds) {
            final long id = Long.parseLong(g.getId());
            if (SamuraiStore.guildExists(id)) {
                SamuraiStore.writeGuild(SamuraiStore.readGuild(id));
            }
        }
    }


}
