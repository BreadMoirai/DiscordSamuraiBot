package samurai.command.guild;

import samurai.Bot;
import samurai.command.Command;
import samurai.command.annotations.Key;
import samurai.entities.base.FixedMessage;
import samurai.entities.base.SamuraiMessage;
import samurai.entities.dynamic.Book;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * @author TonTL
 * @version 4.x - 2/17/2017
 */
@SuppressWarnings("ALL")
@Key("rank")
public class Rank extends Command {

    @Override
    protected SamuraiMessage buildMessage() {
        if (guild.getUsers().size() == 0) return FixedMessage.build("No users found.");
        long id;
        if (mentions.size() == 0) {
            id = Long.parseLong(author.getUser().getId());
            if (!guild.hasUser(id)) return FixedMessage.build("You have not linked an osu account to yourself yet.");
        } else if (mentions.size() == 1) {
            id = Long.parseLong(mentions.get(0).getUser().getId());
            if (!guild.hasUser(id))
                return FixedMessage.build(String.format("**%s** does not have an osu account linked.", mentions.get(0).getEffectiveName()));
        } else {
            return FixedMessage.build("Too many mentions");
        }
        int listSize = guild.getUserCount();
        int target = guild.getUser(id).getL_rank() - 1;
        ArrayList<String> nameList = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            final String name = Bot.getUser(guild.getUsers().get(i).getDiscordId()).getName();
            final String osuName = guild.getUsers().get(i).getOsuName();
            if (i != target) {
                nameList.add(String.format("%d. %s : %s (%d)%n", i, name, osuName, guild.getScoreCount(osuName)));
            } else {
                nameList.add(String.format("#%d %s : %s (%d)%n", i, name, osuName, guild.getScoreCount(osuName)));
            }
        }

        ListIterator<String> itr = nameList.listIterator();
        int pageLen = listSize % 10 >= 5 ? listSize / 10 + 1 : listSize / 10;
        ArrayList<String> book = new ArrayList<>(pageLen);
        for (int i = 0; i < pageLen - 1; i++) {
            StringBuilder sb = new StringBuilder().append("```md\n");
            int j = 0;
            while (j++ < 10) {
                sb.append(itr.next());
            }
            sb.append("```");
            book.add(sb.toString());
        }
        StringBuilder sb = new StringBuilder().append("```md\n");
        itr.forEachRemaining(sb::append);
        sb.append("```");
        book.add(sb.toString());


        return new Book(target / 10, book);
    }

}

