package samurai.command.debug;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.core.entities.TextChannel;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.SuggestionPoll;

@Source
@Key({"fix", "suggest", "enhance"})
public class Suggest extends Command {

    private static final long SUGGESTION_QUEUE_ID;

    static {
        final Config config = ConfigFactory.load("source_commands.conf");
        SUGGESTION_QUEUE_ID = config.getLong("SuggestionPoll.queue");
    }

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final TextChannel suggestionQueue = context.getGuild().getTextChannelById(SUGGESTION_QUEUE_ID);
        String type;
        switch (context.getKey().toLowerCase()) {
            case "fix":
                type = "Fix";
                break;
            case "suggest":
                type = "Suggestion";
                break;
            case "enhance":
                type = "Enhancement";
                break;
            default:
                type = "Unknown";
        }
        final SuggestionPoll suggestionPoll = new SuggestionPoll(type, context.getContent(), context.getAuthor(), context.getTime());
        suggestionPoll.setChannelId(SUGGESTION_QUEUE_ID);
        return suggestionPoll;
    }
}
