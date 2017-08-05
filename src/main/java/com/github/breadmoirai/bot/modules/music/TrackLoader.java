/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.github.breadmoirai.bot.modules.music;


import com.github.breadmoirai.bot.framework.core.CommandEvent;
import com.github.breadmoirai.bot.framework.core.Response;
import com.github.breadmoirai.bot.framework.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackLoader extends Response implements AudioLoadResultHandler {

    private static final String SHUFFLE_REACTION = "\uD83D\uDD00";
    private static final String CANCEL_REACTION = "\u23cf";
    private static final String CONFIRM_REACTION = "\u25b6";
    private static final String PAGE_REACTION = "\u2194";
    private static final long TIME_OUT = 6L;
    private static final TimeUnit TIME_UNIT = TimeUnit.HOURS;

    private final GuildMusicManager musicManager;
    private final List<String> request;
    private boolean lucky;
    private int page;
    private boolean loadAsPlaylist;
    private AudioPlaylist playlist;
    private List<AudioTrack> tracklist;
    private TextChannel channel;
    private String requester;
    private volatile boolean finished;

    public TrackLoader(GuildMusicManager audioManager, boolean lucky, String... content) {
        this(audioManager, lucky, Arrays.asList(content));
    }

    public TrackLoader(GuildMusicManager audioManager, List<String> content, String playlistName) {
        this(audioManager, false, content);
        loadAsPlaylist = true;
        playlist = new BasicAudioPlaylist(playlistName, new ArrayList<>(20), null, true);
        tracklist = Collections.synchronizedList(playlist.getTracks());
        finished = false;
    }

    public TrackLoader(GuildMusicManager audioManager, boolean lucky, List<String> content) {
        this.musicManager = audioManager;
        this.request = content;
        this.lucky = lucky;
        page = 0;
        loadAsPlaylist = false;
        finished = false;
    }

    @Override
    public Message buildMessage() {
        if (!request.isEmpty()) {
            if (request.get(0).startsWith("ytsearch:")) {
                return new MessageBuilder().append("Searching Youtube...").build();
            } else if (request.get(0).startsWith("scsearch:")) {
                return new MessageBuilder().append("Searching SoundCloud...").build();
            }
        }
        return new MessageBuilder().append("Loading tracks...").build();
    }

    @Override
    public void onSend(Message message) {
        channel = message.getTextChannel();
        request.forEach(s -> musicManager.loadItem(s, this));
        requester = message.getGuild().getMemberById(getAuthorId()).getEffectiveName();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        track.setUserData(requester);
        if (finished) return;
        if (!loadAsPlaylist) {
            musicManager.getScheduler().queue(track);
            final int i = musicManager.getScheduler().getQueue().indexOf(track);
            if (i != -1) {
                channel.editMessageById(getMessageId(), new EmbedBuilder().setDescription(String.format("Queued track: %s at position `%d`", MusicModule.trackInfoDisplay(track, true), i + 1)).build()).queue();
            } else {
                channel.editMessageById(getMessageId(), new EmbedBuilder().setDescription("Now Playing: ").appendDescription(MusicModule.trackInfoDisplay(track, true)).build()).queue();
            }
        } else {
            tracklist.add(track);
            if (tracklist.size() == 1) {
                channel.addReactionById(getMessageId(), SHUFFLE_REACTION).queue();
                channel.addReactionById(getMessageId(), CANCEL_REACTION).queue();
                channel.addReactionById(getMessageId(), CONFIRM_REACTION).queue();
                if (request.size() > 10) {
                    channel.addReactionById(getMessageId(), PAGE_REACTION).queue();
                }
                waitForCommand();
            } else if (tracklist.size() == 8 || tracklist.size() == request.size()) {
                channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
            }
        }
    }

    private void waitForCommand() {
        final EventWaiter waiter = EventWaiter.get();
        waiter.waitForEvent(GenericGuildMessageReactionEvent.class, this::onReaction, TrackLoader.TIME_OUT, TrackLoader.TIME_UNIT, this::cancel);
        waiter.waitForEvent(CommandEvent.class, this::onCommand, TrackLoader.TIME_OUT, TrackLoader.TIME_UNIT, null);
        waiter.waitForEvent(GuildMessageDeleteEvent.class, o -> o.getMessageIdLong() == getMessageId(), TrackLoader.TIME_OUT, TrackLoader.TIME_UNIT, () -> finished = true);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (finished) return;
        playlist.getTracks().forEach(track -> track.setUserData(requester));
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
        waitForCommand();
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
        IntStream.range(start, end).filter(value -> value < tSize).mapToObj(i -> String.format("%n`%d.` %s", i + 1, MusicModule.trackInfoDisplay(tracklist.get(i), false))).forEachOrdered(sb::append);
        if (end < tSize)
            sb.append("\n... `").append(tSize - end).append("` more tracks");
        return new MessageBuilder().setEmbed(eb.build()).build();
    }

    private Message buildFinishedDisplay() {
        EmbedBuilder eb = new EmbedBuilder();
        final StringBuilder sb = eb.getDescriptionBuilder();
        sb.append("**").append(playlist.getName()).append("**\n`")
                .append(tracklist.size()).append("` tracks loaded at position `").append(musicManager.getScheduler().getQueue().size()).append('`');
        return new MessageBuilder().setEmbed(eb.build()).build();
    }

    @Override
    public void noMatches() {
        channel.editMessageById(getMessageId(), new MessageBuilder().append("No tracks found.").build()).queue();
        finished = true;
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        channel.editMessageById(getMessageId(), new MessageBuilder().append(exception.toString()).build()).queue();
        finished = true;
    }


    public boolean onCommand(CommandEvent event) {
        if (finished) return true;
        if (event.getChannelId() != this.getChannelId()) return false;
        if (event.getAuthorId() != this.getAuthorId()) return false;
        if (!event.hasContent()) return false;
        page = 0;
        final String key = event.getKey().toLowerCase();
        final int size = tracklist.size();
        if (key.startsWith("select") || key.startsWith("sel")) {
            tracklist = event.getIntArgs().map(i -> i - 1).filter(i -> i >= 0 && i < tracklist.size()).mapToObj(tracklist::get).collect(Collectors.toList());
        } else if ((key.startsWith("remove") || key.startsWith("rm"))) {
            tracklist.removeAll(event.getIntArgs().map(i -> i - 1).filter(i -> i >= 0 && i < tracklist.size()).mapToObj(tracklist::get).collect(Collectors.toList()));
        } else if (key.startsWith("filter")) {
            final List<String> args = event.getArgs();
            Predicate<AudioTrack> filter;
            if (key.contains("out"))
                filter = audioTrack -> args.stream().anyMatch(audioTrack.getInfo().title.toLowerCase()::contains);
            else
                filter = audioTrack -> args.stream().noneMatch(audioTrack.getInfo().title.toLowerCase()::contains);
            tracklist.removeIf(filter);
        }
        if (key.endsWith("p")) {
            finish();
            return true;
        }
        channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
        if (tracklist.size() <= 10 && size > 10) {
            channel.getMessageById(getMessageId()).queue(message -> {
                message.getReactions()
                        .stream()
                        .filter(messageReaction -> messageReaction.getEmote().getName().equals(PAGE_REACTION))
                        .findAny().ifPresent(messageReaction -> {
                    if (event.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) {
                        messageReaction.getUsers().stream().map(messageReaction::removeReaction).forEach(RestAction::queue);
                    } else
                        messageReaction.removeReaction().queue();
                });
            });
        }
        return false;
    }

    private void finish() {
        if (finished) return;
        finished = true;
        channel.editMessageById(getMessageId(), buildFinishedDisplay()).queue();
        channel.getMessageById(getMessageId()).queue(message -> message.clearReactions().queue());
        musicManager.getScheduler().queue(tracklist);
    }

    public boolean onReaction(GenericGuildMessageReactionEvent event) {
        if (finished) return true;
        if (event.getMessageIdLong() != this.getMessageId()) return false;
        if (event.getUser().getIdLong() != this.getAuthorId()) return false;
        final String name = event.getReactionEmote().getName();
        switch (name) {
            case SHUFFLE_REACTION:
                Collections.shuffle(tracklist);
                page = 0;
                channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
                event.getReaction().removeReaction(event.getUser()).queue();
                return false;
            case CANCEL_REACTION:
                cancel();
                return false;
            case PAGE_REACTION:
                final int tSize = tracklist.size();
                if (tSize > 10) {
                    page = (page + 1) % ((tSize / 10) + 1);
                    channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
                return false;
            case CONFIRM_REACTION:
                finish();
                return true;
            default:
                return false;
        }
    }

    private void cancel() {
        if (finished) return;
        channel.editMessageById(getMessageId(), new EmbedBuilder()
                .appendDescription("**")
                .appendDescription(playlist.getName())
                .appendDescription("**\n")
                .appendDescription("Track Loading Canceled").build())
                .queue(message -> message.clearReactions().queue());
        finished = true;
    }


}
