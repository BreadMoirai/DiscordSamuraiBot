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
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;
import samurai.messages.impl.PermissionFailureMessage;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author TonTL
 * @version 4/11/2017
 */
@Key("skip")
public class Skip extends Command {

    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (!context.getSelfMember().hasPermission(context.getChannel(), PERMISSIONS)) {
            return new PermissionFailureMessage(context.getSelfMember(), context.getChannel(), PERMISSIONS);
        }
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            final EmbedBuilder eb = new EmbedBuilder().appendDescription("Skipped: ");
            final List<AudioTrack> queue = audioManager.scheduler.getQueue();
            if (context.hasContent()) {
                if (context.getContent().equalsIgnoreCase("all")) {
                    int size = queue.size();
                    audioManager.scheduler.clear();
                    final StringBuilder db = eb.getDescriptionBuilder();
                    db.append('`').append(size).append(" songs`");
                    return FixedMessage.build(eb.build());
                } else {
                    final List<Integer> argList = context.getIntArgs().boxed().collect(Collectors.toList());
                    final int size = queue.size();
                    final ArrayDeque<AudioTrack> skip = audioManager.scheduler.skip(argList.stream()).collect(Collectors.toCollection(ArrayDeque::new));
                    final IntStream intStream = argList.stream().distinct().mapToInt(Integer::intValue).sorted().map(operand -> operand - 1).filter(integer -> integer >= 0 && integer < size);
                    intStream.forEachOrdered(value -> eb.appendDescription(String.format("\n`%d.` %s", value + 1, Play.trackInfoDisplay(skip.removeLast(), true))));
                }
            } else {
                AudioTrack current = audioManager.player.getPlayingTrack();
                eb.appendDescription(Play.trackInfoDisplay(current, true));
                if (queue.size() > 0) {
                    eb.appendDescription("\nNow Playing: ").appendDescription(Play.trackInfoDisplay(queue.get(0), true));
                }
                audioManager.scheduler.nextTrack();
            }
            return FixedMessage.build(eb.build());
        }
        return null;
    }
}
