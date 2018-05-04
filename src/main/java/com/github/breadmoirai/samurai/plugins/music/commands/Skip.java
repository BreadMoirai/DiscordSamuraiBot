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
package com.github.breadmoirai.samurai.plugins.music.commands;

import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.samurai.plugins.music.AbstractMusicCommand;
import com.github.breadmoirai.samurai.plugins.music.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author TonTL
 * @version 4/11/2017
 */
public class Skip extends AbstractMusicCommand {

    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};

    @Override
    public void onCommand(CommandEvent event) {
        if (event.requirePermission(PERMISSIONS)) {
            return;
        }
        final Optional<GuildAudioManager> managerOptional = getPlugin(event).retrieveManager(event.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            final EmbedBuilder eb = new EmbedBuilder().appendDescription("Skipped: ");
            final List<AudioTrack> queue = audioManager.scheduler.getQueue();
            if (event.hasContent()) {
                if (event.getContent().equalsIgnoreCase("all")) {
                    int size = queue.size();
                    audioManager.scheduler.clear();
                    final StringBuilder db = eb.getDescriptionBuilder();
                    db.append('`').append(size).append(" songs`");
                    event.reply().setEmbed(eb.build()).send();
                    return;
                } else {
                    final List<Integer> argList = event.getArguments().ints().boxed().collect(Collectors.toList());
                    final int size = queue.size();
                    final ArrayDeque<AudioTrack> skip = audioManager.scheduler.skip(argList.stream()).collect(Collectors.toCollection(ArrayDeque::new));
                    final IntStream intStream = argList.stream().distinct().mapToInt(Integer::intValue).sorted().map(operand -> operand - 1).filter(integer -> integer >= 0 && integer < size);
                    intStream.forEachOrdered(value -> eb.appendDescription(String.format("\n`%d.` %s", value + 1, Play.trackInfoDisplay(skip.removeLast(), true))));
                }
            } else {
                AudioTrack current = audioManager.scheduler.getCurrent();
                eb.appendDescription(Play.trackInfoDisplay(current, true));
                audioManager.scheduler.nextTrack();
                if (queue.size() > 0) {
                    eb.appendDescription("\nNow Playing: ").appendDescription(Play.trackInfoDisplay(audioManager.scheduler.getCurrent(), true));
                } else {
                    event.reply().setEmbed(eb.build()).after(2, TimeUnit.SECONDS).onSuccess(message -> {
                        final AudioTrack currentTrack = audioManager.scheduler.getCurrent();
                        if (currentTrack != null) {
                            message.editMessage(new EmbedBuilder(message.getEmbeds().get(0)).appendDescription("\n**Now Playing:** ").appendDescription(Play.trackInfoDisplay(currentTrack)).build()).queue();
                        }
                    }).send();
                    return;
                }
            }
            event.reply().setEmbed(eb.build()).send();
        }
    }
}
