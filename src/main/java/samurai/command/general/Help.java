/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.command.general;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import samurai.Bot;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.files.SamuraiStore;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Key("help")
public class Help extends Command {

    private static final Pattern PREFIX_PATTERN = Pattern.compile("[prefix]", Pattern.LITERAL);

    /**
     * @return A Message with an Embed created using resources/help.txt
     */
    @Override
    public SamuraiMessage execute(CommandContext context) {
        if (!context.hasContent()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor("Samurai - help.txt", null, context.getSelfUser().getEffectiveAvatarUrl())
                    .setDescription(PREFIX_PATTERN.matcher(SamuraiStore.getHelp("cmdlist")).replaceAll(Matcher.quoteReplacement(context.getPrefix())));

            return new FixedMessage()
                    .setMessage(new MessageBuilder().setEmbed(embedBuilder.build()).build());
        } else {
            return FixedMessage.build(PREFIX_PATTERN.matcher(SamuraiStore.getHelp(context.getContent())).replaceAll(Matcher.quoteReplacement(context.getPrefix())));
        }
        //return FixedMessage.build("Yeah I don't think that's a real command.");
    }
}
