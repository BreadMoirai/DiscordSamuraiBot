package samurai.command.primary;

import net.dv8tion.jda.core.entities.Member;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.entities.SamuraiGuild;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.dynamic.Book;
import samurai.util.BotUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author TonTL
 * @version 4.x - 2/17/2017
 */
@Key("rank")
public class Rank extends Command {

    @Override
    public SamuraiMessage execute(CommandContext context) {
        final SamuraiGuild guild = context.getGuild();
        final List<Member> mentions = context.getMentionsMembers();
        if (guild.getUsers().size() == 0) return FixedMessage.build("No users found.");
        long id;
        if (mentions.size() == 0) {
            id = Long.parseLong(context.getAuthor().getUser().getId());
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
            final String name = BotUtil.retrieveUser(guild.getUsers().get(i).getDiscordId()).getName();
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

