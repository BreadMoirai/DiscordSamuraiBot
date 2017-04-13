package samurai.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TonTL
 * @version 4/11/2017
 */
public class SamuraiAudioManager {

    private static final AudioPlayerManager playerManager;
    private static final ConcurrentHashMap<Long, GuildAudioManager> audioManagers;

    static {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);
        audioManagers = new ConcurrentHashMap<>();
    }

    private SamuraiAudioManager() {
    }

    public static boolean openConnection(VoiceChannel channel) {
        final long idLong = channel.getGuild().getIdLong();
        if (!audioManagers.containsKey(idLong)) {
            audioManagers.put(idLong, new GuildAudioManager(playerManager, channel.getGuild().getAudioManager()));
        }
        return audioManagers.get(idLong).openAudioConnection(channel);
    }

    public static void loadItem(Object orderingKey, String request, AudioLoadResultHandler resultHandler) {
        playerManager.loadItemOrdered(orderingKey, request, resultHandler);
    }

    public static Optional<GuildAudioManager> retrieveManager(long guildId) {
        return Optional.ofNullable(audioManagers.get(guildId));
    }

    public static Optional<GuildAudioManager> removeManager(long guildId) {
        return Optional.ofNullable(audioManagers.remove(guildId));
    }
}
