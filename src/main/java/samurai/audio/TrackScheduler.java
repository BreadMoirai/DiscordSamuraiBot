/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import samurai.command.music.History;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final List<AudioTrackR> queue;
    private final Deque<AudioTrackR> history;
    private AudioTrackR current;
    private boolean autoPlay;

    TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new ArrayList<>(20);
        history = new ArrayDeque<AudioTrackR>(10) {
            @Override
            public void addFirst(AudioTrackR audioTrack) {
                if (this.size() > 9)
                    super.removeLast();
                super.addFirst(audioTrack);
            }
        };
        autoPlay = true;
    }

    public AudioTrackR queue(AudioTrack audioTrack, String requester) {
        AudioTrackR track = new AudioTrackR(audioTrack, requester);
        if (player.startTrack(track.getTrack(), true)) {
            history.addFirst(current);
            current = track;
        } else queue.add(track);
        return track;
    }


    public void nextTrack() {
        if (queue.isEmpty()) {
            history.addFirst(current);
            if (autoPlay) {
                AudioTrackR track = current;
                if (track == null) {
                    return;
                }
                if (track.getSourceManager().getSourceName().equalsIgnoreCase("youtube")) {
                    final List<String> related = YoutubeAPI.getRelated(track.getIdentifier(), 15L);
                    SamuraiAudioManager.loadItem(this, related.get((int) (Math.random() * related.size())), new AutoPlayHandler());
                }
                current = null;
            } else {
                current = null;
                player.stopTrack();
            }
        } else {
            AudioTrackR track = queue.remove(0);
            player.startTrack(track.getTrack(), false);
            current = track;
        }
    }

    public void prevTrack() {
        if (history.isEmpty()) return;
        AudioTrackR previous = history.pollFirst();
        current = previous.makeClone();
        player.startTrack(current, false);
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
        current = null;
        player.stopTrack();
        queue.clear();
    }

    public AudioTrackR getCurrent() {
        return current;
    }

    public List<AudioTrackR> getQueue() {
        return Collections.unmodifiableList(queue);
    }

    public int shuffleQueue() {
        Collections.shuffle(queue);
        return queue.size();
    }

    public Stream<AudioTrackR> skip(Stream<Integer> indexes) {
        final int size = queue.size();
        return indexes.distinct().sorted((o1, o2) -> o2 - o1).mapToInt(Integer::intValue).map(operand -> operand - 1).filter(integer -> integer >= 0 && integer < size).mapToObj(queue::remove);
    }

    public void queueFirst(AudioTrack track, String requester) {
        if (!player.startTrack(track, true)) {
            queue.add(0, new AudioTrackR(track, requester));
        }
    }

    public void queue(List<AudioTrack> playlist, String requester) {
        for (AudioTrack audioTrack : playlist) {
            queue(audioTrack, requester);
        }
    }

    public void queueFirst(List<AudioTrack> playlist, String requester) {
        queue.addAll(0, playlist.stream().map(audioTrack -> new AudioTrackR(audioTrack, requester)).collect(Collectors.toList()));
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

    public Deque<AudioTrackR> getHistory() {
        return history;
    }

    private class AutoPlayHandler implements AudioLoadResultHandler {
        @Override
        public void trackLoaded(AudioTrack track) {
            queue(track, null);
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