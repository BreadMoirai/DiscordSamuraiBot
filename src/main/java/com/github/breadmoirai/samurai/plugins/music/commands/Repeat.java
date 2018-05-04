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

import java.util.Optional;

public class Repeat extends AbstractMusicCommand {

    @Override
    public void onCommand(CommandEvent event) {
        final Optional<GuildAudioManager> managerOptional = getPlugin(event).retrieveManager(event.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager guildAudioManager = managerOptional.get();
            final AudioTrack current = guildAudioManager.scheduler.getCurrent();
            if (current == null) event.send("There is nothing to repeat");
            final boolean b = guildAudioManager.scheduler.toggleRepeat();
            if (!b) event.send("Repeat stopped");
            else
                event.reply()
                        .setEmbed(new EmbedBuilder()
                                          .appendDescription(Play.trackInfoDisplay(current, true))
                                          .appendDescription(" is now playing on repeat")
                                          .build())
                        .send();
        }
        event.send("There is nothing to repeat.");
    }
}
