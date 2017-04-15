package samurai.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;


/**

 * Holder for both the player and a track scheduler for one guild.

 */

public class GuildAudioManager {

    private net.dv8tion.jda.core.managers.AudioManager guildAudioManager;


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
    GuildAudioManager(AudioPlayerManager manager, net.dv8tion.jda.core.managers.AudioManager guildAudioManager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        this.guildAudioManager = guildAudioManager;
        guildAudioManager.setSendingHandler(new AudioPlayerSendHandler(player));
    }

    boolean openAudioConnection(VoiceChannel channel) {
        try {
            guildAudioManager.openAudioConnection(channel);
            return true;
        }
        catch(IllegalArgumentException | PermissionException e) {
            return false;
        }
    }


}