package samurai.messages.dynamic;

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
import samurai.command.GenericCommand;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.GenericCommandListener;
import samurai.messages.listeners.ReactionListener;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author TonTL
 * @version 4/11/2017
 */
public class TrackLoader extends DynamicMessage implements AudioLoadResultHandler, GenericCommandListener, ReactionListener {

    private static final String SHUFFLE_REACTION = "\uD83D\uDD00";
    private static final String CANCEL_REACTION = "⏏";
    private static final String CONFIRM_REACTION = "▶";

    private final GuildAudioManager audioManager;
    private final String request;
    private boolean playNow;
    private boolean playNext;
    private AudioPlaylist playlist;
    private MessageChannel channel;


    public TrackLoader(GuildAudioManager audioManager, String content, boolean playNow, boolean playNext) {
        this.audioManager = audioManager;
        this.request = content;
        this.playNow = playNow;
        this.playNext = playNext;
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
        String trackLengthDisp;
        if (trackInfo.length == Long.MAX_VALUE) {
            trackLengthDisp = "∞";
        } else {
            trackLengthDisp = String.format("%d:%02d", trackInfo.length / (60 * 1000), trackInfo.length / 1000 % 60);
        }
        channel.editMessageById(getMessageId(), String.format("Queued track: **%s** - %s [%s]", trackInfo.title, trackInfo.author, trackLengthDisp)).queue();
        unregister();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        this.playlist = playlist;
        channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
        channel.addReactionById(getMessageId(), SHUFFLE_REACTION).queue();
        channel.addReactionById(getMessageId(), CANCEL_REACTION).queue();
        channel.addReactionById(getMessageId(), CONFIRM_REACTION).queue();
    }

    private Message buildPlaylistDisplay() {
        EmbedBuilder eb = new EmbedBuilder();
        final StringBuilder sb = eb.getDescriptionBuilder();
        sb.append("**").append(playlist.getName()).append("**");
        AtomicInteger i = new AtomicInteger(0);
        playlist.getTracks().stream().limit(10).map(AudioTrack::getInfo).map(audioTrackInfo -> String.format("%n`%d.` [%s](%s) [%d:%02d]", i.incrementAndGet(), audioTrackInfo.title, audioTrackInfo.uri, audioTrackInfo.length / (60 * 1000), (audioTrackInfo.length / 1000) % 60)).forEachOrdered(sb::append);
        final int tSize = playlist.getTracks().size();
        if (tSize > 10) {
            sb.append("\n... `").append(tSize - 10).append("` more tracks");
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
        final String key = context.getKey();
        if ((key.equalsIgnoreCase("remove") || key.equalsIgnoreCase("rm")) && context.hasContent()) {
            final int size = playlist.getTracks().size();
            context.getIntArgs().filter(value -> value <= size && value > 0).boxed().distinct().sorted((o1, o2) -> o2 - o1).forEachOrdered(integer -> playlist.getTracks().remove(integer - 1));
            channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
        }
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        final String name = event.getReactionEmote().getName();
        switch (name) {
            case SHUFFLE_REACTION:
                Collections.shuffle(playlist.getTracks());
                channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
                event.getReaction().removeReaction(event.getUser()).queue();
                break;
            case CANCEL_REACTION:
                channel.editMessageById(getMessageId(), new MessageBuilder().append("Track Loading canceled.").build()).queue();
                channel.getMessageById(getMessageId()).queue(message -> message.clearReactions().queue());
                unregister();
                break;
            case CONFIRM_REACTION:
                channel.editMessageById(getMessageId(), String.format("`%d` tracks loaded", playlist.getTracks().size())).queue();
                channel.getMessageById(getMessageId()).queue(message -> message.clearReactions().queue());
                unregister();
                if (playNow) audioManager.scheduler.clear();
                if (playNext) audioManager.scheduler.queueFirst(playlist);
                else audioManager.scheduler.queue(playlist);
                unregister();
                break;
        }
    }
}
