/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package samurai.command.general;

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
