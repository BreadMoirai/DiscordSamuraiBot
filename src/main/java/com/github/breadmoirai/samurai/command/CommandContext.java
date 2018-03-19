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
package com.github.breadmoirai.samurai.command;

import com.github.breadmoirai.samurai.items.Inventory;
import com.github.breadmoirai.samurai.plugins.points.DerbyPointPlugin;
import com.github.breadmoirai.samurai.plugins.points.PointSession;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CommandContext {
    private final String prefix;
    private final String key;
    private final Member author;
    private final List<User> mentionedUsers;
    private final List<Role> mentionedRoles;
    private final String content;
    private final List<Message.Attachment> attaches;
    private final long guildId;
    private final long channelId;
    private final long messageId;
    private final List<TextChannel> mentionedChannels;
    private List<Member> mentionedMembers;
    private List<String> args;
    private final TextChannel channel;
    private final OffsetDateTime time;
    private int shardId;
    private DerbyPointPlugin pointTracker;
    private CommandScheduler commandScheduler;
    private transient Inventory authorInventory;

    public CommandContext(String prefix, String key, Member author, List<User> mentionedUsers, List<Role> mentionedRoles, List<TextChannel> mentionedChannels, String content, List<Message.Attachment> attaches, long guildId, long channelId, long messageId, TextChannel channel, OffsetDateTime time) {
        this.prefix = prefix;
        this.key = key;
        this.author = author;
        this.mentionedUsers = mentionedUsers;
        this.mentionedRoles = mentionedRoles;
        this.mentionedChannels = mentionedChannels;
        this.content = content;
        this.attaches = attaches;
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.channel = channel;
        this.time = time;
    }

    public long getGuildId() {
        return guildId;
    }

    public Member getAuthor() {
        return author;
    }

    public List<User> getMentionedUsers() {
        return mentionedUsers;
    }

    public List<Member> getMentionedMembers() {
        if (mentionedMembers == null) {
            final Guild guild = channel.getGuild();
            mentionedMembers = Collections.unmodifiableList(mentionedUsers.stream().map(guild::getMember).collect(Collectors.toList()));
        }
        return mentionedMembers;
    }

    public String getContent() {
        return content;
    }

    public List<String> getArgs() {
        if (args == null) return (args = CommandFactory.parseArgs(content));
        else return args;
    }

    private static final Pattern EMOTE_PATTERN = Pattern.compile("<:([^\\s]*):([0-9]*)>");

    public List<Emote> getEmotes() {
        final List<Emote> emotes = new ArrayList<>();
        final Matcher emoteMatcher = EMOTE_PATTERN.matcher(content);
        while (emoteMatcher.find()) {
            final String id = emoteMatcher.group(2);
            final Emote emoteById = getClient().getEmoteById(id);
            if (emoteById != null)
                emotes.add(emoteById);
        }
        return emotes;
    }

    public List<Message.Attachment> getAttaches() {
        return attaches;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getMessageId() {
        return messageId;
    }


    public OffsetDateTime getTime() {
        return time;
    }

    public Instant getInstant() {
        return time.toInstant();
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }

    public String getKey() {
        return key;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public boolean hasContent() {
        return content != null && content.length() > 0;
    }

    public List<TextChannel> getMentionedChannels() {
        return mentionedChannels;
    }

    public List<Role> getMentionedRoles() {
        return mentionedRoles;
    }

    public Guild getGuild() {
        return channel.getGuild();
    }

    public long getAuthorId() {
        return author.getUser().getIdLong();
    }

    public String getPrefix() {
        return prefix;
    }

    public IntStream getIntArgs() {
        return Arrays.stream(content.split(" ")).flatMapToInt(this::parseIntArg);
    }

    private IntStream parseIntArg(String s) {
        try {
            final String[] split = s.split("-");
            if (split.length == 1) {
                if (isNumber(split[0]))
                    return IntStream.of(Integer.parseInt(split[0]));
            } else if (split.length == 2) {
                if (isNumber(split[0]) && isNumber(split[1])) {
                    final int a = Integer.parseInt(split[0]);
                    final int b = Integer.parseInt(split[1]);
                    if (a < b)
                        return IntStream.rangeClosed(a, b);
                    else return IntStream.rangeClosed(b, a).map(i -> a - i + b);

                }
            }
        } catch (NumberFormatException e) {
            return IntStream.empty();
        }
        return IntStream.empty();
    }

    public boolean isNumeric() {
        return isNumber(content);
    }

    public static boolean isNumber(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), 10) < 0) return false;
        }
        return true;
    }

    private static final Pattern HEX = Pattern.compile("^(0x|#)?[0-9A-Fa-f]+$");

    public boolean isHex() {
        return HEX.matcher(content).matches();
    }


    public JDA getClient() {
        return channel.getJDA();
    }

    private static final Pattern FORMATTED = Pattern.compile("<.*>");

    /**
     * @return only plain text and default emojis
     */
    public String getStrippedContent() {
        return getArgs().stream().filter(s -> !FORMATTED.matcher(s).matches()).collect(Collectors.joining(" "));
    }

    public JDA getJDA() {
        return channel.getJDA();
    }

    public SelfUser getSelfUser() {
        return channel.getJDA().getSelfUser();
    }

    public Member getSelfMember() {
        return channel.getGuild().getSelfMember();
    }

    private static final Pattern URL = Pattern.compile("(?:<)?((?:http(s)?://.)?(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b(?:[-a-zA-Z0-9@:%_+.~#?&/=]*))(?:>)?");

    /**
     * @return the url if found, null if content is not a url.
     */
    public String getAsUrl() {
        final Matcher matcher = URL.matcher(content);
        if (matcher.matches()) {
            return matcher.group(1);
        } else return null;
    }


    public void setPointTracker(DerbyPointPlugin pointTracker) {
        this.pointTracker = pointTracker;
    }

    public DerbyPointPlugin getPointTracker() {
        return pointTracker;
    }

    public PointSession getAuthorPoints() {
        return pointTracker.getMemberPointSession(getGuildId(), getAuthorId());
    }

    public Stream<PointSession> getMemberPoints() {
        return getGuild().getMembers().stream()
                .filter(member ->
                        !((member.getUser().isBot() || member.getUser().isFake())
                                && member.getUser().getIdLong() != member.getJDA().getSelfUser().getIdLong()))
                .map(member -> pointTracker.getMemberPointSession(member))
                .sorted(Comparator.comparingDouble(PointSession::getPoints).reversed());
    }

    private static final Pattern LINES = Pattern.compile("[\n](?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    public String[] lines() {
        return LINES.split(content);
    }

    public PrimitiveContext createPrimitive() {
        return new PrimitiveContext(this);
    }

    public void setCommandScheduler(CommandScheduler commandScheduler) {
        this.commandScheduler = commandScheduler;
    }

    public CommandScheduler getCommandScheduler() {
        return commandScheduler;
    }

    public CommandContext clone(String key, String content) {
        return clone(key, content, mentionedUsers, mentionedRoles, mentionedChannels);
    }

    public CommandContext clone(String key, String content, List<User> users, List<Role> roles, List<TextChannel> channels) {
        return new CommandContext(prefix, key, author, users, roles, channels, content, attaches, guildId, channelId, messageId, channel, time);
    }

    public boolean isFloat() {
        return isFloat(content);
    }

    public static boolean isFloat(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (i == 0 && c == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(c, 10) < 0 && c != '.') return false;
        }
        return true;
    }

    public Inventory getAuthorInventory() {
        if (authorInventory == null) {
            authorInventory = Inventory.ofMember(getGuildId(), getAuthorId());
        }
        return authorInventory;
    }
}
