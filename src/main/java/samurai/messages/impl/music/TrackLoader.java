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
package samurai.messages.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.audio.GuildAudioManager;
import samurai.audio.SamuraiAudioManager;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.basic.GenericCommand;
import samurai.command.music.Play;
import samurai.messages.annotations.MessageScope;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.UniqueMessage;
import samurai.messages.listeners.CommandListener;
import samurai.messages.listeners.ReactionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrackLoader extends DynamicMessage implements AudioLoadResultHandler, CommandListener, ReactionListener, UniqueMessage {

    private static final String SHUFFLE_REACTION = "\uD83D\uDD00";
    private static final String CANCEL_REACTION = "\u23cf";
    private static final String CONFIRM_REACTION = "\u25b6";
    private static final String PAGE_REACTION = "\u2194";

    private final GuildAudioManager audioManager;
    private final List<String> request;
    private boolean playNow;
    private boolean lucky;
    private int page;
    private boolean loadAsPlaylist;
    private AudioPlaylist playlist;
    private List<AudioTrack> tracklist;
    private MessageChannel channel;
    private boolean closed;
    private String requester;

    public TrackLoader(GuildAudioManager audioManager, boolean playNow, boolean lucky, String... content) {
        this(audioManager, playNow, lucky, Arrays.asList(content));
    }

    public TrackLoader(GuildAudioManager audioManager, List<String> content, String playlistName) {
        this(audioManager, false, false, content);
        loadAsPlaylist = true;
        playlist = new BasicAudioPlaylist(playlistName, new ArrayList<>(20), null, true);
        tracklist = Collections.synchronizedList(playlist.getTracks());
        closed = false;
    }

    public TrackLoader(GuildAudioManager audioManager, boolean playNow, boolean lucky, List<String> content) {
        this.audioManager = audioManager;
        this.request = content;
        this.playNow = playNow;
        this.lucky = lucky;
        page = 0;
        loadAsPlaylist = false;
        closed = false;
    }

    @Override
    protected Message initialize() {
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
    protected void onReady(Message message) {
        channel = message.getChannel();
        request.forEach(s -> SamuraiAudioManager.loadItem(audioManager, s, this));
        requester = message.getGuild().getMemberById(getAuthorId()).getEffectiveName();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        track.setUserData(requester);
        if (closed) return;
        if (!loadAsPlaylist) {
            if (playNow) audioManager.scheduler.queueFirst(track);
            else audioManager.scheduler.queue(track);
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
        return new MessageBuilder().setEmbed(eb.build()).build();
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

    @Override
    public void onCommand(Command command) {
        if (!(command instanceof GenericCommand)) return;
        if (command.getContext().getAuthorId() != this.getAuthorId()) return;
        final CommandContext context = command.getContext();
        if (!context.hasContent()) return;
        page = 0;
        final String key = context.getKey().toLowerCase();
        final int size = tracklist.size();
        if (key.startsWith("select") || key.startsWith("sel")) {
            final List<Integer> integerList = IntStream.rangeClosed(1, size).map(i -> size - i + 1).boxed().collect(Collectors.toList());
            integerList.removeAll(context.getIntArgs().boxed().collect(Collectors.toList()));
            integerList.forEach(integer -> tracklist.remove(integer - 1));
        } else if ((key.startsWith("remove") || key.startsWith("rm")) && context.hasContent()) {
            context.getIntArgs().filter(value -> value <= size && value > 0).boxed().distinct().sorted((o1, o2) -> o2 - o1).forEachOrdered(integer -> tracklist.remove(integer - 1));
        } else if (key.startsWith("filter")) {
            final List<String> args = context.getArgs();
            Predicate<AudioTrack> trackPredicate;
            if (key.contains("out"))
                trackPredicate = audioTrack -> args.stream().anyMatch(audioTrack.getInfo().title.toLowerCase()::contains);
            else
                trackPredicate = audioTrack -> args.stream().noneMatch(audioTrack.getInfo().title.toLowerCase()::contains);
            final List<AudioTrack> collect = tracklist.stream().filter(trackPredicate).collect(Collectors.toList());
            tracklist.removeAll(collect);
        }
        if (key.endsWith("p")) {
            channel.editMessageById(getMessageId(), buildFinishedDisplay()).queue();
            channel.getMessageById(getMessageId()).queue(message -> message.clearReactions().queue());
            unregister();
            if (playNow) audioManager.scheduler.clear();
            audioManager.scheduler.queue(tracklist);
            return;
        }
        channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
        if (tracklist.size() <= 10) {
            channel.getMessageById(getMessageId()).queue(
                    message -> message.getReactions().stream().filter(
                            messageReaction -> messageReaction.getEmote().getName().equals(PAGE_REACTION)).findAny().ifPresent(
                            pageReaction -> pageReaction.getUsers().queue(users -> users.forEach(
                                    user -> pageReaction.removeReaction(user).queue()))));
        }
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        final String name = event.getReactionEmote().getName();
        final int tSize = tracklist.size();
        switch (name) {
            case SHUFFLE_REACTION:
                if (event.getUser().getIdLong() != this.getAuthorId()) break;
                Collections.shuffle(tracklist);
                page = 0;
                channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
                event.getReaction().removeReaction(event.getUser()).queue();
                break;
            case CANCEL_REACTION:
                if (event.getUser().getIdLong() != this.getAuthorId()) break;
                channel.editMessageById(getMessageId(), new EmbedBuilder()
                        .appendDescription("**")
                        .appendDescription(playlist.getName())
                        .appendDescription("**\n")
                        .appendDescription("Track Loading Canceled").build())
                        .queue(message -> message.clearReactions().queue());
                unregister();
                break;
            case PAGE_REACTION:
                if (tSize > 10) {
                    page = (page + 1) % ((tSize / 10) + 1);
                    channel.editMessageById(getMessageId(), buildPlaylistDisplay()).queue();
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
                break;
            case CONFIRM_REACTION:
                if (event.getUser().getIdLong() != this.getAuthorId()) break;
                channel.editMessageById(getMessageId(), buildFinishedDisplay()).queue();
                channel.getMessageById(getMessageId()).queue(message -> message.clearReactions().queue());
                unregister();
                if (playNow) audioManager.scheduler.clear();
                audioManager.scheduler.queue(tracklist);
                break;
        }
    }

    @Override
    public MessageScope scope() {
        return MessageScope.Author;
    }

    @Override
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
}
