package samurai.util;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import samurai.SamuraiDiscord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TonTL
 * @version 3/14/2017
 */
public class BotUtil {

    private static ArrayList<SamuraiDiscord> shards;

    public static void initialize(ArrayList<SamuraiDiscord> shards) {
        BotUtil.shards = shards;
    }


    public static List<Permission> getChannelPermissions(int shardId, long channelId) {
        TextChannel textChannel = shards.get(shardId).getClient().getTextChannelById(String.valueOf(channelId));
        return textChannel.getGuild().getSelfMember().getPermissions(textChannel);
    }

    public static User retrieveUser(long discordId) {
        return shards.get(0).getClient().retrieveUserById(String.valueOf(discordId)).complete();
    }
}
