package samurai.messages.dynamic;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.GenericCommand;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.GenericCommandListener;

/**
 * @author TonTL
 * @version 4/11/2017
 */
public class TrackLoader extends DynamicMessage implements AudioLoadResultHandler, GenericCommandListener {

    private final GuildAudioManager audioManager;
    private final String request;
    private boolean playNow;
    private Message message;


    public TrackLoader(GuildAudioManager audioManager, String content, boolean playNow) {
        this.audioManager = audioManager;
        this.request = content;
        this.playNow = playNow;
    }

    @Override
    protected Message initialize() {
        return new MessageBuilder().append("Searching for track...").build();
    }

    @Override
    protected void onReady(Message message) {
        this.message = message;
        SamuraiAudioManager.loadItem(audioManager, request,this);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if (playNow) {
            audioManager.scheduler.clear();
        }
        this.audioManager.scheduler.queue(track);
        final AudioTrackInfo trackInfo = track.getInfo();
        message.editMessage(String.format("Queued track: **%s** - %s [%d:%02d]", trackInfo.title, trackInfo.author, trackInfo.length/(60*1000), trackInfo.length/1000)).queue();
        unregister();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playNow) audioManager.scheduler.clear();
        playlist.getTracks().forEach(this.audioManager.scheduler::queue);
        message.editMessage("Queued playlist: " + playlist.getName()).queue();
        unregister();
    }

    @Override
    public void noMatches() {
        message.editMessage("No tracks found").queue();
        unregister();

    }

    @Override
    public void loadFailed(FriendlyException exception) {
        message.editMessage(exception.toString()).queue();
        unregister();
    }

    @Override
    public void onCommand(GenericCommand command) {
        
    }
}
