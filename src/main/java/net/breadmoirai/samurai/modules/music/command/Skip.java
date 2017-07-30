/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package net.breadmoirai.samurai.modules.music.command;

import net.breadmoirai.samurai.util.PermissionFailureResponse;

import net.breadmoirai.samurai.modules.music.MusicModule;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.breadmoirai.sbf.core.CommandEvent;
import net.breadmoirai.sbf.core.command.Key;
import net.breadmoirai.sbf.core.command.ModuleCommand;
import net.breadmoirai.sbf.core.response.Response;
import net.breadmoirai.sbf.core.response.Responses;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Key("skip")
public class Skip extends ModuleCommand<MusicModule> {

    @Override
    public Response execute(CommandEvent event, MusicModule module) {
        if (!event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_EMBED_LINKS)) {
            return new PermissionFailureResponse(event.getSelfMember(), event.getChannel(), Permission.MESSAGE_EMBED_LINKS);
        }
        return module.retrieveManager(event.getGuildId()).<Response>map(audioManager -> {
            final EmbedBuilder eb = new EmbedBuilder().appendDescription("Skipped: ");
            final List<AudioTrack> queue = audioManager.getScheduler().getQueue();
            if (event.hasContent()) {
                if (event.getContent().equalsIgnoreCase("all")) {
                    int size = queue.size();
                    audioManager.getScheduler().clear();
                    final StringBuilder db = eb.getDescriptionBuilder();
                    db.append('`').append(size).append(" songs`");
                    return Responses.of(eb.build());
                } else {
                    final List<Integer> argList = event.getIntArgs().boxed().collect(Collectors.toList());
                    final int size = queue.size();
                    final ArrayDeque<AudioTrack> skip = audioManager.getScheduler().skip(argList.stream()).collect(Collectors.toCollection(ArrayDeque::new));
                    final IntStream intStream = argList.stream().distinct().mapToInt(Integer::intValue).sorted().map(operand -> operand - 1).filter(integer -> integer >= 0 && integer < size);
                    intStream.forEachOrdered(value -> eb.appendDescription(String.format("\n`%d.` %s", value + 1, MusicModule.trackInfoDisplay(skip.removeLast(), true))));
                }
            } else {
                AudioTrack current = audioManager.getScheduler().getCurrent();
                eb.appendDescription(MusicModule.trackInfoDisplay(current, true));
                audioManager.getScheduler().skipTrack();
                if (queue.size() > 0) {
                    eb.appendDescription("\nNow Playing: ").appendDescription(MusicModule.trackInfoDisplay(audioManager.getScheduler().getCurrent(), true));
                } else return Responses.of(eb.build()).andThen(message -> {
                    final AudioTrack currentTrack = audioManager.getScheduler().getCurrent();
                    if (currentTrack != null) {
                        message.editMessage(new EmbedBuilder(message.getEmbeds().get(0)).appendDescription("\n**Now Playing:** ").appendDescription(MusicModule.trackInfoDisplay(currentTrack, false)).build()).queue();
                    }
                });
            }
            return Responses.of(eb.build());
        }).orElse(null);
    }
}
