package samurai.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author TonTL
 * @version 4/11/2017
 */
public class SamuraiAudioManager {

    private static final AudioPlayerManager playerManager;
    private static final ConcurrentHashMap<Long, GuildAudioManager> audioManagers;
    private static final ConcurrentHashMap<Long, ScheduledFuture> terminationTask;

    static {
        playerManager = new DefaultAudioPlayerManager();
        playerManager.setFrameBufferDuration((int) TimeUnit.SECONDS.toMillis(15));
        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);
        audioManagers = new ConcurrentHashMap<>();
        terminationTask = new ConcurrentHashMap<>();
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
        if (orderingKey instanceof GuildAudioManager)
            orderingKey = ((GuildAudioManager) orderingKey).scheduler;
        playerManager.loadItemOrdered(orderingKey, request, resultHandler);
    }

    public static Optional<GuildAudioManager> retrieveManager(long guildId) {
        return Optional.ofNullable(audioManagers.get(guildId));
    }

    public static Optional<GuildAudioManager> removeManager(long guildId) {
        return Optional.ofNullable(audioManagers.remove(guildId));
    }

    public static void scheduleLeave(long idLong) {
        final ScheduledFuture<?> scheduledFuture = new RestAction.EmptyRestAction<Void>(null).queueAfter(5, TimeUnit.MINUTES, aVoid -> {
            removeManager(idLong).ifPresent(GuildAudioManager::destroy);
            terminationTask.remove(idLong);
        });
        terminationTask.put(idLong, scheduledFuture);
    }

    public static void cancelLeave(long idLong) {
        final ScheduledFuture leaveFuture = terminationTask.get(idLong);
        if (leaveFuture != null) {
            terminationTask.remove(idLong);
            leaveFuture.cancel(false);
        }
    }
}
