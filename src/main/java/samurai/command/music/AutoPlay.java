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

import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.Optional;

@Key("autoplay")
public class AutoPlay extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            if (context.hasContent()) {
                switch (context.getContent().toLowerCase()) {
                    case "true":
                    case "on":
                        audioManager.scheduler.setAutoPlay(true);
                        return FixedMessage.build("AutoPlay set to `true`");
                    case "false":
                    case "off":
                        audioManager.scheduler.setAutoPlay(false);
                        return FixedMessage.build("AutoPlay set to `false`");
                    default:
                        return null;
                }
            } else {
                return FixedMessage.build("AutoPlay is currently `" + (audioManager.scheduler.isAutoPlay() ? "enabled`" : "disabled`"));
            }
        }
        return null;
    }
}
