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

import java.util.Optional;

public class Pause extends AbstractMusicCommand {

    public Pause() {
        setKeys("pause", "unpause", "resume");
    }

    @Override
    public void onCommand(CommandEvent event) {
        final Optional<GuildAudioManager> managerOptional = getPlugin(event).retrieveManager(event.getGuildId());
        managerOptional.ifPresent(audioManager -> {
            final boolean paused = event.getKey().equalsIgnoreCase("pause");
            audioManager.player.setPaused(paused);
            event.reply("Playback has `").append(paused ? "paused" : "resumed").append('`');
        });
    }
}
