package samurai.action.guild;

import samurai.action.Action;
import samurai.annotations.Client;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;
import samurai.message.dynamic.RankList;

import java.util.ArrayList;

/**
 * @author TonTL
 * @version 4.x - 2/17/2017
 */
@SuppressWarnings("ALL")
@Key("rank")
@Guild
@Client
public class Rank extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        int size = getPageSize();
        if (size == -1) return FixedMessage.build("No users found.");
        else if (size == 0) return FixedMessage.build("Invalid Arguments");
        long id;
        if (mentions.size() == 0) {
            id = Long.parseLong(author.getUser().getId());
            if (!guild.hasUser(id)) return FixedMessage.build("You have not linked an osu account to yourself yet.");
        } else if (mentions.size() == 1) {
            id = Long.parseLong(mentions.get(0).getId());
            if (!guild.hasUser(id))
                return FixedMessage.build(String.format("**%s** does not have an osu account linked.", mentions.get(0).getName()));
        }
        else {
            return FixedMessage.build("Too many mentions");
        }
        int listSize = guild.getUserCount();
        int target = guild.getUser(id).getL_rank() - 1;
        ArrayList<String> nameList = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            if (i != target) {
                nameList.add(String.format("%d. %s : %s", i, client.getUserById(String.valueOf(guild.getUsers().get(i).getDiscordId())).getName(), guild.getUsers().get(i).getOsuName()));
            } else {
                nameList.add(String.format("#%d %s : %s", i, client.getUserById(String.valueOf(guild.getUsers().get(i).getDiscordId())).getName(), guild.getUsers().get(i).getOsuName()));
            }
        }
//        if (listSize < 17) {
//            return new RankList(0, listSize, nameList);
//        }
        int from = target, to = target;
        int i = 1;
        size = 2;
        while (i < size) {
            {
                if (from > 0) {
                    from--;
                } else {
                    to++;
                }
                i++;
            }
            {
                if (to < listSize) {
                    to++;
                } else {
                    from--;
                }
                i++;
            }
        }
        return new RankList(from, to, nameList);
    }

    private int getPageSize() {
        if (guild.getUserCount() == 0) {
            return -1;
        }
        if (args.size() == 1) {
            String arg = args.get(0);
            if (arg.equalsIgnoreCase("max") ||
                    arg.equalsIgnoreCase("full") ||
                    arg.equalsIgnoreCase("all")) {
                return guild.getUserCount();
            } else {
                return 0;
            }
        } else {
            int max = guild.getUserCount();
            if (max >= 10) {
                return 10;
            } else
                return max - 1;
        }
    }
}

