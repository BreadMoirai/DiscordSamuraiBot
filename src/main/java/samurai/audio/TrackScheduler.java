package samurai.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Taken directly from sedmelluq examples
 * <p>
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final List<AudioTrack> queue;
    private final Deque<AudioTrack> history;
    private boolean autoPlay;

    /**
     * @param player The audio player this scheduler uses
     */
    TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new ArrayList<>(20);
        history = new ArrayDeque<AudioTrack>(10) {
            @Override
            public void addFirst(AudioTrack audioTrack) {
                if (this.size() > 9)
                    super.removeLast();
                super.addFirst(audioTrack);
            }
        };
        autoPlay = true;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (player.startTrack(track, true)) history.addFirst(track);
        else queue.add(track);
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        if (queue.isEmpty()) {
            if (autoPlay) {
                final AudioTrack track = history.peekFirst();
                if (track == null) {
                    return;
                }
                final String uri = track.getInfo().uri;
                if (uri.toLowerCase().contains("youtube")) {
                    final List<String> related = YoutubeAPI.getRelated(uri.substring(uri.lastIndexOf('=') + 1), 15L);
                    SamuraiAudioManager.loadItem(this, related.get((int) (Math.random() * related.size())), new AutoplayHandler());
                }
            } else {
                player.stopTrack();
            }
        } else {
            AudioTrack track = queue.remove(0);
            player.startTrack(track, false);
            history.addFirst(track);
        }
    }

    public void prevTrack() {
        if (history.isEmpty()) return;
        final AudioTrack playingTrack = player.getPlayingTrack();

        if (playingTrack != null) {
            if (playingTrack.getPosition() < 20) {
                history.pollFirst();
            } else {
                queue.add(playingTrack.makeClone());
            }
        }
        final AudioTrack prevTrack = history.pollFirst();
        if (prevTrack != null)
            player.startTrack(prevTrack.makeClone(), false);
        else player.stopTrack();
    }


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        exception.printStackTrace();
        nextTrack();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        System.err.println("Track stuck: " + track.getInfo().uri);
        nextTrack();
    }

    public void clear() {
        player.stopTrack();
        queue.clear();
    }

    public AudioTrack getCurrent() {
        return player.getPlayingTrack();
    }

    public List<AudioTrack> getQueue() {
        return Collections.unmodifiableList(queue);
    }

    public int shuffleQueue(){
        Collections.shuffle(queue);
        return queue.size();
    }

    public Stream<AudioTrack> skip(Stream<Integer> indexes) {
        final int size = queue.size();
        return indexes.distinct().sorted((o1, o2) -> o2 - o1).mapToInt(Integer::intValue).map(operand -> operand - 1).filter(integer -> integer >= 0 && integer < size).mapToObj(queue::remove);
    }

    public void queueFirst(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.add(0, track);
        }
    }

    public void queue(List<AudioTrack> playlist) {
        queue.addAll(playlist);
        if (player.getPlayingTrack() == null) {
            nextTrack();
        }
    }

    public void queueFirst(List<AudioTrack> playlist) {
        queue.addAll(0, playlist);
        if (player.getPlayingTrack() == null) {
            nextTrack();
        }
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public Deque<AudioTrack> getHistory() {
        return history;
    }

    private class AutoplayHandler implements AudioLoadResultHandler {
        @Override
        public void trackLoaded(AudioTrack track) {
            player.startTrack(track, false);
            history.addFirst(track);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
        }

        @Override
        public void noMatches() {
        }

        @Override
        public void loadFailed(FriendlyException exception) {
        }
    }
}