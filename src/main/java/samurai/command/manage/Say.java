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
package samurai.command.manage;

import net.dv8tion.jda.core.entities.TextChannel;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Admin;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Key("say")
public class Say extends Command{

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (context.getContent().startsWith("<#")) {
            final List<TextChannel> channels = context.getMentionedChannels();
            if (channels.size() == 1) {
                final TextChannel targetChannel = channels.get(0);
                final FixedMessage build = FixedMessage.build(context.getContent().replaceFirst(targetChannel.getAsMention(), ""));
                build.setChannelId(targetChannel.getIdLong());
                return build;
            }
        }
        return FixedMessage.build(context.getContent());
    }
}
