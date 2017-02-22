package samurai.action.guild;

import samurai.action.Action;
import samurai.annotations.Guild;
import samurai.annotations.Key;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;
import samurai.message.dynamic.RankList;

/**
 * @author TonTL
 * @version 4.x - 2/17/2017
 */
@Key("rank")
@Guild
public class Rank extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        int size = getListSize();
        if (size == -1) return FixedMessage.build("No users found.");
        else if (size == 0) return FixedMessage.build("Invalid Arguments");
        long id;
        if (mentions.size() == 0) id = Long.parseLong(author.getUser().getId());
        else if (mentions.size() == 1) id = Long.parseLong(mentions.get(0).getId());
        else {
            return FixedMessage.build("Too many mentions");
        }
        int listSize = guild.getUserCount();
        int target = guild.getUser(id).getL_rank() - 1;
        if (listSize < 18) {
            return new RankList(0, 18, target, guild.getUsers());
        }
        int from = target, to = target;
        int i = 1;
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
        return new RankList(from, to, target, guild.getUsers());
    }

    private int getListSize() {
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

