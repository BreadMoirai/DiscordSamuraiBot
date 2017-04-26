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
import samurai.Bot;
import samurai.database.Database;
import samurai.entities.model.SGuild;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author TonTL
 * @version 3/14/2017
 */
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
    private SGuild sGuild;
    private final TextChannel channel;
    private final OffsetDateTime time;
    private int shardId;
    private JDA client;

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

    public List<Message.Attachment> getAttaches() {
        return attaches;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getMessageId() {
        return messageId;
    }

    public SGuild getSamuraiGuild() {
        if (sGuild == null) {
            final long[] userID = channel.getGuild().getMembers().stream().map(Member::getUser).mapToLong(User::getIdLong).toArray();
            final Optional<SGuild> guildOptional = Database.getDatabase().getGuild(guildId, userID);
            if (guildOptional.isPresent())
                this.sGuild = guildOptional.get();
            else {
                final Optional<SGuild> guild = Database.getDatabase().createGuild(guildId, CommandModule.getDefaultEnabledCommands());
                guild.ifPresent(sGuild -> {
                    sGuild.getManager().setUsers(userID);
                    this.sGuild = sGuild;
                });
            }
        }
        return sGuild;
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
            if (isInteger(split[0]))
                return IntStream.of(Integer.parseInt(split[0]));
        } else if (split.length == 2) {
            if (isInteger(split[0]) && isInteger(split[1])) {
                return IntStream.rangeClosed(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }
        }
        return IntStream.empty();
    }

    public boolean isInt() {
        return isInteger(content);
    }

    public static boolean isInteger(String s) {
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

    public String getStrippedContent() {
        return getArgs().stream().collect(Collectors.joining(" "));
    }
}
