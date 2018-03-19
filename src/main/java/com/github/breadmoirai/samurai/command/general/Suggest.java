package com.github.breadmoirai.samurai.command.general;

import com.github.breadmoirai.samurai.command.Command;
import com.github.breadmoirai.samurai.command.CommandContext;
import com.github.breadmoirai.samurai.command.annotations.Key;
import com.github.breadmoirai.samurai.command.annotations.Source;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import com.github.breadmoirai.samurai.messages.impl.SuggestionPoll;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

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
        String type;
        switch (context.getKey().toLowerCase()) {
            case "fix":
                type = "Bug";
                break;
            case "suggest":
                type = "Feature";
                break;
            case "enhance":
                type = "Enhancement";
                break;
            default:
                type = "Unknown";
        }
        final SuggestionPoll suggestionPoll = new SuggestionPoll(type, context.getContent(), context.getAuthor(), context.getInstant());
        suggestionPoll.setChannelId(SUGGESTION_QUEUE_ID);
        return suggestionPoll;
    }
}
