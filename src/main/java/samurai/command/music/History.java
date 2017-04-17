package samurai.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.base.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author TonTL
 * @version 4/16/2017
 */
@Key("history")
public class History extends Command{
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager manager = managerOptional.get();
            final Deque<AudioTrack> history = manager.scheduler.getHistory();
            final EmbedBuilder eb = new EmbedBuilder();
            final StringBuilder sb = eb.getDescriptionBuilder();
            final AtomicInteger i = new AtomicInteger(0);
            sb.append("**History**");
            history.stream().limit(10).map(audioTrack -> Play.trackInfoDisplay(audioTrack.getInfo())).map(s -> String.format("\n`%d.` %s", i.incrementAndGet(), s)).forEachOrdered(sb::append);
            return FixedMessage.build(eb.build());
        }
        return null;
    }
}
