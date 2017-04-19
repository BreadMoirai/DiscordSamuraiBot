package samurai.messages.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.CommandContext;
import samurai.command.generic.GenericCommand;
import samurai.command.music.Play;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.GenericCommandListener;
import samurai.messages.listeners.ReactionListener;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author TonTL
 * @version 4/11/2017
 */
public class TrackLoader extends DynamicMessage implements AudioLoadResultHandler, GenericCommandListener, ReactionListener {

    private static final String SHUFFLE_REACTION = "\uD83D\uDD00";
    private static final String CANCEL_REACTION = "⏏";
    private static final String CONFIRM_REACTION = "▶";
    private static final String PAGE_REACTION = "↔";

    private final GuildAudioManager audioManager;
    private final String request;
    private boolean playNow;
    private boolean playNext;
    private boolean lucky;
    private boolean front;
    private AudioPlaylist playlist;
    private List<AudioTrack> tracklist;
    private MessageChannel channel;


    public TrackLoader(GuildAudioManager audioManager, String content, boolean playNow, boolean playNext, boolean lucky) {
        this.audioManager = audioManager;
        this.request = content;
        this.playNow = playNow;
        this.playNext = playNext;
        this.lucky = lucky;
        front = true;
    }

    @Override
    protected Message initialize() {
        return new MessageBuilder().append("Searching for track...").build();
    }

    @Override
    protected void onReady(Message message) {
        channel = message.getChannel();
        SamuraiAudioManager.loadItem(audioManager, request, this);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if (playNow) {
            audioManager.scheduler.clear();
        }
        if (playNext) audioManager.scheduler.queueFirst(track);
        else this.audioManager.scheduler.queue(track);

        final AudioTrackInfo trackInfo = track.getInfo();
        channel.editMessageById(getMessageId(), new EmbedBuilder().setDescription(String.format("Queued track: %s at position `%d`", Play.trackInfoDisplay(trackInfo), audioManager.scheduler.getQueue().indexOf(track))).build()).queue();
        unregister();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        this.playlist = playlist;
        this.tracklist = Collections.synchronizedList(playlist.getTracks());
        if (lucky) {
            trackLoaded(tracklist.get(0));
            return;
        }
        channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
        channel.addReactionById(getMessageId(), SHUFFLE_REACTION).queue();
        channel.addReactionById(getMessageId(), CANCEL_REACTION).queue();
        channel.addReactionById(getMessageId(), CONFIRM_REACTION).queue();
        if (tracklist.size() > 10) {
            channel.addReactionById(getMessageId(), PAGE_REACTION).queue();
        }
    }

    private Message buildPlaylistDisplay() {
        EmbedBuilder eb = new EmbedBuilder();
        final StringBuilder sb = eb.getDescriptionBuilder();
        sb.append("**").append(playlist.getName()).append("**");
        final int tSize = tracklist.size();
        AtomicInteger i = new AtomicInteger(front ? 0 : tSize - 10);
        if (front) {
            tracklist.stream().limit(10).map(AudioTrack::getInfo).map(audioTrackInfo -> String.format("%n`%d.` %s", i.incrementAndGet(), Play.trackInfoDisplay(audioTrackInfo))).forEachOrdered(sb::append);
        }
        if (tSize > 10) {
            sb.append("\n... `").append(tSize - 10).append("` more tracks");
            if (!front) {
                IntStream.range(tSize - 10, tSize).mapToObj(tracklist::get).map(AudioTrack::getInfo).map(audioTrackInfo -> String.format("%n`%d.` %s", i.incrementAndGet(), Play.trackInfoDisplay(audioTrackInfo))).forEachOrdered(sb::append);
            }
        }
        return new MessageBuilder().setEmbed(eb.build()).build();
    }

    @Override
    public void noMatches() {
        channel.editMessageById(getMessageId(), "No tracks found").queue();
        unregister();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        channel.editMessageById(getMessageId(), exception.toString()).queue();
        unregister();
    }

    @Override
    public void onCommand(GenericCommand command) {
        final CommandContext context = command.getContext();
        if (!context.hasContent()) return;
        final String key = context.getKey().toLowerCase();
        final int size = tracklist.size();
        if (key.startsWith("select") || key.startsWith("sel")) {
            final List<Integer> integerList = IntStream.rangeClosed(1, size).map(i -> size - i + 1).boxed().collect(Collectors.toList());
            integerList.removeAll(context.getIntArgs().boxed().collect(Collectors.toList()));
            integerList.forEach(integer -> tracklist.remove(integer - 1));
        } else if ((key.startsWith("remove") || key.startsWith("rm")) && context.hasContent()) {
            context.getIntArgs().filter(value -> value <= size && value > 0).boxed().distinct().sorted((o1, o2) -> o2 - o1).forEachOrdered(integer -> tracklist.remove(integer - 1));
        } else if (key.startsWith("filter")) {
            final ListIterator<AudioTrack> itr = tracklist.listIterator();
            final List<String> args = context.getArgs();
            while (itr.hasNext()) {
                final AudioTrack track = itr.next();
                final String title = track.getInfo().title.toLowerCase();
                if (key.contains("out")) {
                    if (args.stream().anyMatch(title::contains)) {
                        itr.remove();
                    }
                } else {
                    if (args.stream().noneMatch(title::contains)) {
                        itr.remove();
                    }
                }
            }
        }
        if (key.endsWith("p")) {
            channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
            channel.editMessageById(getMessageId(), String.format("`%d` tracks loaded", tracklist.size())).queue();
            channel.getMessageById(getMessageId()).queue(message -> message.clearReactions().queue());
            unregister();
            if (playNow) audioManager.scheduler.clear();
            if (playNext) audioManager.scheduler.queueFirst(tracklist);
            else audioManager.scheduler.queue(tracklist);
            return;
        }
        channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
        if (tracklist.size() <= 10) {
            channel.getMessageById(getMessageId()).queue(
                    message -> message.getReactions().stream().filter(
                            messageReaction -> messageReaction.getEmote().getName().equals(PAGE_REACTION)).findAny().ifPresent(
                            pageReaction -> pageReaction.getUsers().queue(users -> users.forEach(
                                    user -> pageReaction.removeReaction(user).queue()))));
        }
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        final String name = event.getReactionEmote().getName();
        switch (name) {
            case SHUFFLE_REACTION:
                Collections.shuffle(tracklist);
                channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
                event.getReaction().removeReaction(event.getUser()).queue();
                break;
            case CANCEL_REACTION:
                channel.editMessageById(getMessageId(), new EmbedBuilder()
                        .appendDescription("**")
                        .appendDescription(playlist.getName())
                        .appendDescription("**\n")
                        .appendDescription("Track Loading Canceled").build()).queue();
                channel.getMessageById(getMessageId()).queue(message -> message.clearReactions().queue());
                unregister();
                break;
            case PAGE_REACTION:
                if (tracklist.size() > 10) {
                    front = !front;
                    channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
                break;
            case CONFIRM_REACTION:
                channel.editMessageById(getMessageId(), String.format("`%d` tracks loaded", tracklist.size())).queue();
                channel.getMessageById(getMessageId()).queue(message -> message.clearReactions().queue());
                unregister();
                if (playNow) audioManager.scheduler.clear();
                if (playNext) audioManager.scheduler.queueFirst(tracklist);
                else audioManager.scheduler.queue(tracklist);
                break;
        }
    }
}
