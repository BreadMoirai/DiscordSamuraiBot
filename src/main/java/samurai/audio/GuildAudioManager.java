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
package samurai.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;


/**

 * Holder for both the player and a track scheduler for one guild.

 */

public class GuildAudioManager {

    private net.dv8tion.jda.core.managers.AudioManager audioManager;

    /**
     * Audio player for the guild.
     */
    public final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    public final TrackScheduler scheduler;

    /**
     * Creates a player and a track scheduler.
     * @param manager Audio player manager to use for creating the player.
     */
    GuildAudioManager(AudioPlayerManager manager, net.dv8tion.jda.core.managers.AudioManager audiomanager) {
        player = manager.createPlayer();
        player.setVolume(30);
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        this.audioManager = audiomanager;
        audiomanager.setSendingHandler(new AudioPlayerSendHandler(player));
    }

    boolean openAudioConnection(VoiceChannel channel) {
        try {
            audioManager.openAudioConnection(channel);
            return true;
        }
        catch(IllegalArgumentException | PermissionException e) {
            return false;
        }
    }

    public void destroy() {
        player.destroy();
        scheduler.clear();
        audioManager.closeAudioConnection();
    }
}