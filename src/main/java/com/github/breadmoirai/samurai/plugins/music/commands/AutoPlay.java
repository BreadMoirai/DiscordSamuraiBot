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
import com.github.breadmoirai.samurai.plugins.music.MusicPlugin;

import java.util.Optional;

public class AutoPlay extends AbstractMusicCommand {

    @Override
    public void onCommand(CommandEvent event) {
        final MusicPlugin music = event.getClient().getPlugin(MusicPlugin.class);
        final Optional<GuildAudioManager> managerOptional = music.retrieveManager(event.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            if (event.hasContent()) {
                switch (event.getContent().toLowerCase()) {
                    case "true":
                    case "on":
                    case "enable":
                        audioManager.scheduler.setAutoPlay(true);
                        event.reply("AutoPlay set to `true`");
                        break;
                    case "false":
                    case "off":
                    case "disable":
                        audioManager.scheduler.setAutoPlay(false);
                        event.reply("AutoPlay set to `false`");
                        break;
                }
            } else {
                event.reply("AutoPlay is currently `" + (audioManager.scheduler.isAutoPlay() ? "enabled`" : "disabled`"));
            }
        }
    }
}
