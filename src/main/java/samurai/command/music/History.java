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
package samurai.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.PermissionFailureMessage;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Key("history")
public class History extends Command{
    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE};

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (!context.getSelfMember().hasPermission(context.getChannel(), PERMISSIONS)) {
            return new PermissionFailureMessage(context.getSelfMember(), context.getChannel(), PERMISSIONS);
        }
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager manager = managerOptional.get();
            final Deque<AudioTrack> history = manager.scheduler.getHistory();
            final EmbedBuilder eb = new EmbedBuilder();
            final StringBuilder sb = eb.getDescriptionBuilder();
            final AtomicInteger i = new AtomicInteger(0);
            sb.append("**History**");
            history.stream().limit(10).map(audioTrack -> Play.trackInfoDisplay(audioTrack.getInfo())).map(s -> String.format("\n`%d.` %s", i.incrementAndGet(), s)).forEachOrdered(sb::append);
            return FixedMessage.build(eb.build());
        }
        return null;
    }

}
