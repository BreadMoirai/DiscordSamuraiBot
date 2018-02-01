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
package com.github.breadmoirai.samurai.plugins.music;

import com.github.breadmoirai.breadbot.framework.event.CommandEvent;
import com.github.breadmoirai.breadbot.plugins.waiter.EventActionFuture;
import com.github.breadmoirai.breadbot.plugins.waiter.EventWaiter;
import com.github.breadmoirai.samurai.Dispatchable;
import com.github.breadmoirai.samurai.plugins.music.commands.Play;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import gnu.trove.set.hash.TIntHashSet;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackLoader implements AudioLoadResultHandler, Dispatchable {

    private static final String SHUFFLE_REACTION = "\uD83D\uDD00";
    private static final String CANCEL_REACTION = "\u23cf";
    private static final String CONFIRM_REACTION = "\u25b6";
    private static final String PAGE_REACTION = "\u2194";

    private final MusicPlugin plugin;
    private final GuildAudioManager audioManager;
    private final List<String> request;
    private final boolean lucky;
    private final boolean loadAsPlaylist;
    private int page;
    private AudioPlaylist playlist;
    private List<AudioTrack> tracklist;
    private MessageChannel channel;
    private boolean closed;
    private String requester;

    private long authorId;
    private long messageId;

    private final EventActionFuture[] futures = new EventActionFuture[6];

    public TrackLoader(MusicPlugin plugin, GuildAudioManager audioManager, List<String> content, String playlistName) {
        this.plugin = plugin;
        this.audioManager = audioManager;
        this.request = content;
        this.lucky = false;
        this.page = 0;
        this.loadAsPlaylist = true;
        this.playlist = new BasicAudioPlaylist(playlistName, new ArrayList<>(20), null, true);
        this.tracklist = playlist.getTracks();
        this.closed = false;
    }

    public TrackLoader(MusicPlugin plugin, GuildAudioManager audioManager, boolean lucky, String query) {
        this.plugin = plugin;
        this.audioManager = audioManager;
        this.request = Collections.singletonList(query);
        this.lucky = lucky;
        this.page = 0;
        this.loadAsPlaylist = !lucky;
        this.closed = false;
    }

    @Override
    public void dispatch(CommandEvent event, EventWaiter waiter, MessageChannel channel) {
        Message initialMessage = initialize();
        requester = event.getMember().getEffectiveName();
        authorId = event.getAuthorId();
        event.reply(initialMessage)
                .onSuccess(message -> {
                    this.messageId = message.getIdLong();
                    this.channel = message.getChannel();
                    request.forEach(s -> plugin.loadItem(audioManager, s, this));
                    waitForEvents(waiter);
                });

    }

    private Message initialize() {
        if (!request.isEmpty()) {
            if (request.get(0).startsWith("ytsearch:")) {
                return new MessageBuilder().append("Searching Youtube...").build();
            } else if (request.get(0).startsWith("scsearch:")) {
                return new MessageBuilder().append("Searching SoundCloud...").build();
            }
        }
        return new MessageBuilder().append("Loading tracks...").build();
    }

    private void waitForEvents(EventWaiter waiter) {
        futures[0] = waiter.waitForCommand()
                .withKeys("select", "selectp", "sel", "selp")
                .inChannel(channel.getIdLong())
                .fromUsers(authorId)
                .matching(CommandEvent::hasContent)
                .action(this::onSelect)
                .stopIf(this::commandIsPlay)
                .finish(this::unregister)
                .build();

        futures[1] = waiter.waitForCommand()
                .withKeys("remove", "removep", "rm", "rmp")
                .inChannel(channel.getIdLong())
                .fromUsers(authorId)
                .matching(CommandEvent::hasContent)
                .action(this::onRemove)
                .stopIf(this::commandIsPlay)
                .finish(this::unregister)
                .build();

        futures[2] = waiter.waitForReaction()
                .fromUsers(getAuthorId())
                .onMessages(getMessageId())
                .withName(CONFIRM_REACTION)
                .action(this::confirmReaction)
                .finish(this::unregister)
                .build();

        futures[3] = waiter.waitForReaction()
                .fromUsers(getAuthorId())
                .onMessages(getMessageId())
                .withName(PAGE_REACTION)
                .action(this::pageReaction)
                .stopIf((e, i) -> false)
                .build();

        futures[4] = waiter.waitForReaction()
                .fromUsers(getAuthorId())
                .onMessages(getMessageId())
                .withName(SHUFFLE_REACTION)
                .action(this::shuffleReaction)
                .stopIf((e, i) -> false)
                .build();

        futures[5] = waiter.waitForReaction()
                .fromUsers(getAuthorId())
                .onMessages(getMessageId())
                .withName(CANCEL_REACTION)
                .action(this::cancelReaction)
                .finish(this::unregister)
                .build();
    }

    private void onRemove(CommandEvent event) {
        onFilter(event, false);
    }

    private void onSelect(CommandEvent event) {
        onFilter(event, true);
    }

    private void onFilter(CommandEvent event, boolean select) {
        page = 0;
        final int size = tracklist.size();
        final boolean isInts = event.getArguments().stream().allMatch(arg -> arg.isInteger() || arg.isRange());
        if (isInts) {
            final TIntHashSet argInts = event.getArguments().ints().collect(TIntHashSet::new, TIntHashSet::add, TIntHashSet::addAll);
            final IntPredicate contains = argInts::contains;
            final List<AudioTrack> collect = IntStream.rangeClosed(1, size)
                    .map(i -> size - i + 1)
                    .filter(select ? contains.negate() : contains)
                    .map(i -> i - 1)
                    .mapToObj(tracklist::get)
                    .collect(Collectors.toList());
            if (!collect.isEmpty()) {
                tracklist.removeAll(collect);
                resetMessage(true);
            }
        }
    }

    private boolean commandIsPlay(CommandEvent event, @SuppressWarnings("unused") int ignored) {
        if (event.getKey().endsWith("p")) {
            confirmReaction(null);
            return true;
        }
        return false;
    }


    private void resetMessage(boolean listChanged) {
        page = 0;
        channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
        if (listChanged && tracklist.size() <= 10) {
            channel.getMessageById(getMessageId()).queue(
                    message -> message.getReactions().stream().filter(
                            messageReaction -> messageReaction.getReactionEmote().getName().equals(PAGE_REACTION)).findAny().ifPresent(
                            pageReaction -> pageReaction.getUsers().queue(users -> users.forEach(
                                    user -> pageReaction.removeReaction(user).queue()))));
        }
    }

    private void unregister() {
        for (EventActionFuture future : futures) {
            if (future != null)
                future.cancel();
        }
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        track.setUserData(requester);
        if (closed) return;
        if (!loadAsPlaylist) {
            audioManager.scheduler.queue(track);
            final int i = audioManager.scheduler.getQueue().indexOf(track);

            if (i != -1) {
                channel.editMessageById(getMessageId(), new EmbedBuilder().setDescription(String.format("Queued track: %s at position `%d`", Play.trackInfoDisplay(track, true), i + 1)).build()).queue();
            } else {
                channel.editMessageById(getMessageId(), new EmbedBuilder().setDescription("Now Playing: ").appendDescription(Play.trackInfoDisplay(track, true)).build()).queue();
            }
            unregister();
        } else {
            tracklist.add(track);
            if (tracklist.size() == 1) {
                channel.addReactionById(getMessageId(), SHUFFLE_REACTION).queue();
                channel.addReactionById(getMessageId(), CANCEL_REACTION).queue();
                channel.addReactionById(getMessageId(), CONFIRM_REACTION).queue();
                if (request.size() > 10) {
                    channel.addReactionById(getMessageId(), PAGE_REACTION).queue();
                }
            } else if (tracklist.size() % 5 == 2 || tracklist.size() == request.size()) {
                channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
            }
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        playlist.getTracks().forEach(track -> track.setUserData(requester));
        if (closed) return;
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
        final int start = page * 10;
        if (page != 0) {
            sb.append("\n... `").append(start).append("` more tracks");
        }
        final int end = start + 10;
        IntStream.range(start, end).filter(value -> value < tSize).mapToObj(i -> String.format("%n`%d.` %s", i + 1, Play.trackInfoDisplay(tracklist.get(i), false))).forEachOrdered(sb::append);
        if (end < tSize)
            sb.append("\n... `").append(tSize - end).append("` more tracks");
        return new MessageBuilder().setContent("").setEmbed(eb.build()).build();
    }

    private Message buildFinishedDisplay() {
        EmbedBuilder eb = new EmbedBuilder();
        final StringBuilder sb = eb.getDescriptionBuilder();
        sb.append("**").append(playlist.getName()).append("**\n`")
                .append(tracklist.size()).append("` tracks loaded at position `").append(audioManager.scheduler.getQueue().size()).append('`');
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

    private void confirmReaction(GenericMessageReactionEvent event) {
        channel.editMessageById(getMessageId(), buildFinishedDisplay()).queue();
        channel.getMessageById(getMessageId()).queue(message -> message.clearReactions().queue());
        unregister();
        audioManager.scheduler.queue(tracklist);
    }

    private void pageReaction(GenericMessageReactionEvent event) {
        int tSize = tracklist.size();
        if (tSize > 10) {
            page = (page + 1) % ((tSize / 10) + 1);
            channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }

    private void shuffleReaction(GenericMessageReactionEvent event) {
        Collections.shuffle(tracklist);
        resetMessage(false);
    }

    private void cancelReaction(GenericMessageReactionEvent event) {
        channel.editMessageById(getMessageId(), new EmbedBuilder()
                .appendDescription("**")
                .appendDescription(playlist.getName())
                .appendDescription("**\n")
                .appendDescription("Track Loading Canceled").build())
                .queue(message -> message.clearReactions().queue());
        unregister();
    }


    public void close(TextChannel channel) {
        closed = true;
        channel.editMessageById(getMessageId(), new EmbedBuilder()
                .appendDescription("**")
                .appendDescription(playlist.getName())
                .appendDescription("**\n")
                .appendDescription("Track Loading Canceled").build())
                .queue(message -> message.clearReactions().queue());
        unregister();
    }

    public long getAuthorId() {
        return authorId;
    }

    public long getMessageId() {
        return messageId;
    }
}
