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
package samurai.command.voice;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import samurai.audio.AudioTestHandler;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Creator;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

@Creator
@Key("audiotest")
public class AudioTest extends Command{
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (SamuraiAudioManager.retrieveManager(context.getGuildId()).isPresent()) {
            return FixedMessage.build("Testing can not be done while music playback is in effect");
        }
        final VoiceChannel voiceChannel = context.getAuthor().getVoiceState().getChannel();
        if (voiceChannel == null) {
            return FixedMessage.build("Try joining a voice channel before using this command");
        }

        final AudioManager audioManager = context.getGuild().getAudioManager();
        final AudioTestHandler audioTestHandler = new AudioTestHandler(context.getAuthor().getUser());

        audioManager.setReceivingHandler(audioTestHandler);
        audioManager.setSendingHandler(audioTestHandler);
        audioManager.openAudioConnection(voiceChannel);

        return FixedMessage.build("AudioTesting enabled");
    }

}
