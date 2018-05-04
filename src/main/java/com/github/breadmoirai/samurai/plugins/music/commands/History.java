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

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class History extends AbstractMusicCommand {
    
    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE};

    @Override
    public void onCommand(CommandEvent event) {
        if (event.requirePermission(PERMISSIONS)) {
            return;
        }
        final Optional<GuildAudioManager> managerOptional = getPlugin(event).retrieveManager(event.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager manager = managerOptional.get();
            final Deque<AudioTrack> history = manager.scheduler.getHistory();
            final EmbedBuilder eb = new EmbedBuilder();
            final StringBuilder sb = eb.getDescriptionBuilder();
            final AtomicInteger i = new AtomicInteger(0);
            sb.append("**History**");
            history.stream().limit(10).map(track -> Play.trackInfoDisplay(track, true)).map(s -> String.format("\n`%d.` %s", i.incrementAndGet(), s)).forEachOrdered(sb::append);
            event.reply().setEmbed(eb.build()).send();
        }
    }

}
