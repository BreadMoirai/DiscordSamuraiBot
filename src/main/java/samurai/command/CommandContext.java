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
package samurai.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.lang3.tuple.Pair;
import samurai.Bot;
import samurai.database.Database;
import samurai.database.dao.GuildDao;
import samurai.database.objects.SamuraiGuild;
import samurai.database.objects.GuildUpdater;
import samurai.points.PointSession;
import samurai.points.PointTracker;

import java.time.OffsetDateTime;
import java.util.*;
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
    private List<Member> mentionedMembers;
    private final List<Role> mentionedRoles;
    private final String content;
    private final List<Message.Attachment> attaches;
    private final long guildId;
    private final long channelId;
    private final long messageId;
    private List<TextChannel> mentionedChannels;
    private List<String> args;
    private SamuraiGuild samuraiGuild;
    private final TextChannel channel;
    private final OffsetDateTime time;
    private int shardId;
    private PointTracker pointTracker;

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

    private static final Pattern EMOTE_PATTERN = Pattern.compile("<:(.*):([0-9]*)>");

    public List<Emote> getEmotes() {
        final List<Emote> emotes = new ArrayList<>();
        final Matcher emoteMatcher = EMOTE_PATTERN.matcher(content);
        while (emoteMatcher.find()) {
            emotes.add(getGuild().getEmoteById(emoteMatcher.group(2)));
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

    public SamuraiGuild getSamuraiGuild() {
        if (samuraiGuild == null) {
            samuraiGuild = Database.get().<GuildDao, SamuraiGuild>openDao(GuildDao.class, guildDao -> guildDao.getGuild(getGuildId()));
        }
        return samuraiGuild;
    }

    public GuildUpdater getSamuraiGuildUpdater() {
        return GuildUpdater.of(getGuildId());
    }

    public OffsetDateTime getTime() {
        return time;
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

    public boolean isSource() {
        return guildId == Bot.SOURCE_GUILD;
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
        final String[] split = s.split("-");
        if (split.length == 1) {
            if (isNumber(split[0]))
                return IntStream.of(Integer.parseInt(split[0]));
        } else if (split.length == 2) {
            if (isNumber(split[0]) && isNumber(split[1])) {
                return IntStream.rangeClosed(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }
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


    public CommandContext clone(String key, String content) {
        return new CommandContext(prefix, key, author, mentionedUsers, mentionedRoles, mentionedChannels, content, attaches, guildId, channelId, messageId, channel, time);
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

    public void setPointTracker(PointTracker pointTracker) {
        this.pointTracker = pointTracker;
    }

    public PointTracker getPointTracker() {
        return pointTracker;
    }

    public PointSession getAuthorPoints() {
        return pointTracker.getPoints(getGuildId(), getAuthorId());
    }

    public Stream<PointSession> getMemberPoints() {
        return getGuild().getMembers().stream()
                .filter(member -> !(member.getUser().isBot() || member.getUser().isFake()))
                .map(member -> {
            PointSession points = pointTracker.getPoints(getGuildId(), member.getUser().getIdLong());
            points.setMember(member);
            return points;
        }).sorted(Comparator.comparingDouble(PointSession::getPoints).reversed());
    }
}
