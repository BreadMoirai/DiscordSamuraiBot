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
package samurai.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import org.assertj.core.util.Lists;
import samurai.SamuraiDiscord;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrimitiveContext implements Serializable {

    private static final long serialVersionUID = 10012L;

    String prefix;
    String key;
    String content;
    long authorId;
    long guildId;
    long channelId;
    long messageId;
    long[] mentionedUsers;
    long[] mentionedRoles;
    long[] mentionedChannels;
    OffsetDateTime time;
    int shardId;

    public PrimitiveContext() {
    }

    public PrimitiveContext(CommandContext commandContext) {
        prefix = commandContext.getPrefix();
        key = commandContext.getKey();
        authorId = commandContext.getAuthorId();
        content = commandContext.getContent();
        guildId = commandContext.getGuildId();
        channelId = commandContext.getChannelId();
        messageId = commandContext.getMessageId();
        mentionedUsers = commandContext.getMentionedUsers().stream().mapToLong(ISnowflake::getIdLong).toArray();
        mentionedRoles = commandContext.getMentionedRoles().stream().mapToLong(ISnowflake::getIdLong).toArray();
        mentionedChannels = commandContext.getMentionedChannels().stream().mapToLong(ISnowflake::getIdLong).toArray();
        time = commandContext.getTime();
        shardId = commandContext.getShardId();
    }

    public CommandContext buildContext(JDA client) {
        final Guild guild = client.getGuildById(guildId);
        if (guild == null) return null;
        final Member author = guild.getMemberById(authorId);
        if (author == null) return null;
        final TextChannel channel = guild.getTextChannelById(channelId);
        if (channel == null) return null;
        final List<User> userList = Arrays.stream(mentionedUsers).mapToObj(client::getUserById).filter(Objects::nonNull).collect(Collectors.toList());
        final List<TextChannel> channelList = Arrays.stream(mentionedChannels).mapToObj(guild::getTextChannelById).filter(Objects::nonNull).collect(Collectors.toList());
        final List<Role> roleList = Arrays.stream(mentionedRoles).mapToObj(guild::getRoleById).filter(Objects::nonNull).collect(Collectors.toList());
        return new CommandContext(prefix, key, author, userList, roleList, channelList, content, Lists.emptyList(), guildId, channelId, messageId, channel, time);
    }
}
