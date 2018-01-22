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

import com.github.breadmoirai.samurai.command.Command;
import com.github.breadmoirai.samurai.command.CommandContext;
import com.github.breadmoirai.samurai.command.annotations.Key;
import com.github.breadmoirai.samurai.messages.base.SamuraiMessage;
import com.github.breadmoirai.samurai.plugins.music.GuildAudioManager;
import com.github.breadmoirai.samurai.plugins.music.MusicPlugin;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Optional;

@Key("repeat")
public class Repeat extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = MusicPlugin.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager guildAudioManager = managerOptional.get();
            final AudioTrack current = guildAudioManager.scheduler.getCurrent();
            if (current == null) event.reply("There is nothing to repeat");
            final boolean b = guildAudioManager.scheduler.toggleRepeat();
            if (!b) event.reply("Repeat stopped");
            else
                event.reply(new EmbedBuilder().appendDescription(Play.trackInfoDisplay(current, true) + " is now playing on repeat").build());
        }
        event.reply("There is nothing to repeat.");
    }
}
