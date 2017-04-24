package samurai.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.annotations.Key;
import samurai.messages.impl.FixedMessage;
import samurai.messages.base.SamuraiMessage;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author TonTL
 * @version 4/11/2017
 */
@Key("skip")
public class Skip extends Command {
    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final Optional<GuildAudioManager> managerOptional = SamuraiAudioManager.retrieveManager(context.getGuildId());
        if (managerOptional.isPresent()) {
            final GuildAudioManager audioManager = managerOptional.get();
            final EmbedBuilder eb = new EmbedBuilder().appendDescription("Skipped: ");
            final List<AudioTrack> queue = audioManager.scheduler.getQueue();
            if (context.hasContent()) {
                if (context.getContent().equalsIgnoreCase("all")) {
                    int size = queue.size();
                    audioManager.scheduler.clear();
                    final StringBuilder db = eb.getDescriptionBuilder();
                    db.append('`').append(size).append(" songs`");
                    return FixedMessage.build(eb.build());
                } else {
                    final List<Integer> argList = context.getIntArgs().boxed().collect(Collectors.toList());
                    final int size = queue.size();
                    final ArrayDeque<AudioTrack> skip = audioManager.scheduler.skip(argList.stream()).collect(Collectors.toCollection(ArrayDeque::new));
                    final IntStream intStream = argList.stream().distinct().mapToInt(Integer::intValue).sorted().map(operand -> operand - 1).filter(integer -> integer >= 0 && integer < size);
                    intStream.forEachOrdered(value -> eb.appendDescription(String.format("\n`%d.` %s", value + 1, Play.trackInfoDisplay(skip.removeLast().getInfo()))));
                }
            } else {
                AudioTrack current = audioManager.player.getPlayingTrack();
                eb.appendDescription(Play.trackInfoDisplay(current.getInfo()));
                if (queue.size() > 0) {
                    eb.appendDescription("\nNow Playing: ").appendDescription(Play.trackInfoDisplay(queue.get(0).getInfo()));
                }
                audioManager.scheduler.nextTrack();
            }
            return FixedMessage.build(eb.build());
        }
        return null;
    }
}
