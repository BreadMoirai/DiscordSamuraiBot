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

import com.google.api.services.youtube.model.Playlist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import net.dv8tion.jda.core.Permission;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.audio.TrackScheduler;
import samurai.audio.YoutubeAPI;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.PermissionFailureMessage;
import samurai.messages.impl.music.TrackLoader;

import java.util.List;
import java.util.Optional;

/**
 * @author TonTL
 * @version 4/19/2017
 */
@Key("related")
public class Related extends Command {

    private static final Permission[] PERMISSIONS = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};

    @Override
    protected SamuraiMessage execute(CommandContext context) {
        if (!context.getSelfMember().hasPermission(context.getChannel(), PERMISSIONS)) {
            return new PermissionFailureMessage(context.getSelfMember(), context.getChannel(), PERMISSIONS);
        }
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            final AudioTrack playingTrack = audioManager.player.getPlayingTrack();
            if (playingTrack != null) {
                final String uri = playingTrack.getInfo().uri;
                if (uri.toLowerCase().contains("youtube")) {
                    final List<String> related = YoutubeAPI.getRelated(uri.substring(uri.lastIndexOf('=') + 1), 20L);
                    return new TrackLoader(audioManager, related, String.format("Tracks related to [%s](%s)", playingTrack.getInfo().title, uri));
                }
            }
        }
        return null;
    }
}
