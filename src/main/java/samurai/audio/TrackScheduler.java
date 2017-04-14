package samurai.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * Taken directly from sedmelluq examples
 * <p>
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingDeque<AudioTrack> queue;
    private final Deque<AudioTrack> history;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingDeque<>(20);
        history = new ArrayDeque<AudioTrack>(10) {
            @Override
            public void addFirst(AudioTrack audioTrack) {
                if (this.size() > 9)
                    super.removeLast();
                super.addFirst(audioTrack);
            }
        };
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
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        final AudioTrack track = queue.poll();
        player.startTrack(track, false);
        history.addFirst(track);
    }

    public void prevTrack() {
        final AudioTrack track = history.pollFirst();
        player.startTrack(track, false);
        queue.addFirst(track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public void clear() {
        player.stopTrack();
        queue.clear();
    }

    public AudioTrack getCurrent() {
        return player.getPlayingTrack();
    }

    public Collection<AudioTrack> getQueue() {
        return Collections.unmodifiableCollection(queue);
    }

    public void skip(int skipSize) {
        queue.removeAll(queue.stream().limit(skipSize).collect(Collectors.toList()));
        //todo ??? maybe inefficient who knows
    }
}