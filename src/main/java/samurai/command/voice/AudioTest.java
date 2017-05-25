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

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandScheduler;
import samurai.command.annotations.Key;
import samurai.command.annotations.Source;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Source
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
        audioManager.openAudioConnection(voiceChannel);
        final AudioTestHandler audioTestHandler = new AudioTestHandler(context.getAuthor());
        audioManager.setReceivingHandler(audioTestHandler);
        audioManager.setSendingHandler(audioTestHandler);
//        CommandScheduler.getCommandExecutor().schedule(() -> {
//            audioManager.closeAudioConnection();
//            audioManager.setSendingHandler(null);
//            audioManager.setReceivingHandler(null);
//        }, 1, TimeUnit.MINUTES);
        return FixedMessage.build("AudioTesting enabled for 1 minute");
    }

    private class AudioTestHandler implements AudioReceiveHandler, AudioSendHandler {
        private final User tester;
        private BlockingQueue<byte[]> audioData;
        AudioTestHandler(Member tester) {
            this.tester = tester.getUser();
            audioData = new LinkedBlockingDeque<>();
        }

        @Override
        public boolean canReceiveCombined() {
            return false;
        }

        @Override
        public boolean canReceiveUser() {
            return true;
        }

        @Override
        public void handleCombinedAudio(CombinedAudio combinedAudio) {
            //don't
        }

        @Override
        public void handleUserAudio(UserAudio userAudio) {
            if (userAudio.getUser().equals(tester)) {
                System.out.println("audio recieved");
                final byte[] data = userAudio.getAudioData(1.0);
                audioData.offer(data);
            } else {
                System.out.println("audio recieved from " + userAudio.getUser().getName());
            }
        }

        @Override
        public boolean canProvide() {
            final boolean b = !audioData.isEmpty();
            System.out.println("canProvide = " + b);
            return b;
        }

        @Override
        public byte[] provide20MsAudio() {
            System.out.println("audio sent");
            return audioData.poll();
        }

        @Override
        public boolean isOpus() {
            return true;
        }
    }
}
