/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package com.github.breadmoirai.samurai.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class TrackScheduler extends AudioEventAdapter {
    private final MusicModule module;
    private AudioPlayer player;
    private final List<AudioTrack> queue;
    private final Deque<AudioTrack> history;
    private AudioTrack current;
    private boolean autoPlay;
    private boolean repeat;

    TrackScheduler(AudioPlayer player, MusicModule module) {
        this.player = player;
        this.module = module;
        this.queue = new ArrayList<>(20);
        history = new ArrayDeque<AudioTrack>(10) {
            @Override
            public void addFirst(AudioTrack audioTrack) {
                if (this.size() > 15)
                    super.removeLast();
                super.addFirst(audioTrack);
            }
        };
        autoPlay = true;
    }

    public AudioTrack queue(AudioTrack audioTrack) {
        if (player.startTrack(audioTrack, true)) {
            current = audioTrack;
        } else queue.add(audioTrack);
        return audioTrack;
    }


    public void nextTrack() {
        if (repeat && current != null) {
            current = current.makeClone();
            player.startTrack(current, false);
            return;
        }
        if (queue.isEmpty()) {
            history.addFirst(current);
            if (autoPlay) {
                AudioTrack track = current;
                if (track == null) {
                    return;
                }
                if (track.getSourceManager().getSourceName().equalsIgnoreCase("youtube")) {
                    final List<String> related = YoutubeAPI.getRelated(track.getIdentifier(), 15L);
                    module.loadItem(this, related.get(ThreadLocalRandom.current().nextInt(related.size())), new AutoPlayHandler());
                }
                current = null;
            } else {
                current = null;
                player.stopTrack();
            }
        } else {
            AudioTrack track = queue.remove(0);
            player.startTrack(track, false);
            if (current != null) {
                history.addFirst(current);
            }
            current = track;
        }
    }

    public void prevTrack() {
        if (history.isEmpty()) return;
        AudioTrack previous = history.pollFirst();
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
        player.destroy();
        this.player = module.createPlayer();
        this.current = null;
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

    public AudioTrack getCurrent() {
        return current;
    }

    public List<AudioTrack> getQueue() {
        return Collections.unmodifiableList(queue);
    }

    public int shuffleQueue() {
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
        for (AudioTrack audioTrack : playlist) {
            queue(audioTrack);
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

    public boolean toggleRepeat() {
        repeat = !repeat;
        return repeat;
    }

    private class AutoPlayHandler implements AudioLoadResultHandler {
        @Override
        public void trackLoaded(AudioTrack track) {
            track.setUserData("Samurai");
            queue(track);
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