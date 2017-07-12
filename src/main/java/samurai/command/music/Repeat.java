/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package samurai.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.util.Optional;

@Key("repeat")
public class Repeat extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager guildAudioManager = managerOptional.get();
            final AudioTrack current = guildAudioManager.scheduler.getCurrent();
            if (current == null) return FixedMessage.build("There is nothing to repeat");
            final boolean b = guildAudioManager.scheduler.toggleRepeat();
            if (!b) return FixedMessage.build("Repeat stopped");
            else return FixedMessage.build(new EmbedBuilder().appendDescription(Play.trackInfoDisplay(current, true) + " is now playing on repeat").build());
        }
        return FixedMessage.build("There is nothing to repeat.");
    }
}
