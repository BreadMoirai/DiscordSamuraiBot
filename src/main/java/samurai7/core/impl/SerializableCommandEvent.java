/*
 *       Copyright 2017 Ton Ly
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
package samurai7.core.impl;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import samurai7.core.ICommandEvent;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@SuppressWarnings("Duplicates")
public class SerializableCommandEvent implements ICommandEvent, Serializable {

    private static final long serialVersionUID = 1;

    private transient JDA jda;

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
    String attachment;
    OffsetDateTime time;


    /**
     * public no-args constructor for serialization purposes only
     */
    public SerializableCommandEvent() {}

    SerializableCommandEvent(MessageReceivedCommandEvent commandEvent) {
        prefix = commandEvent.getPrefix();
        key = commandEvent.getKey();
        authorId = commandEvent.getAuthorId();
        content = commandEvent.getContent();
        guildId = commandEvent.getGuildId();
        channelId = commandEvent.getChannelId();
        messageId = commandEvent.getMessageId();
        mentionedUsers = commandEvent.getMentionedUsers().stream().mapToLong(ISnowflake::getIdLong).toArray();
        mentionedRoles = commandEvent.getMentionedRoles().stream().mapToLong(ISnowflake::getIdLong).toArray();
        mentionedChannels = commandEvent.getMentionedChannels().stream().mapToLong(ISnowflake::getIdLong).toArray();
        time = commandEvent.getTime();
    }

    public void load(JDA jda) {
        this.jda = jda;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public User getAuthor() {
        return getJDA().getUserById(getAuthorId());
    }

    @Override
    public long getAuthorId() {
        return authorId;
    }

    @Override
    public Member getMember() {
        return getGuild().getMemberById(getAuthorId());
    }

    @Override
    public SelfUser getSelfUser() {
        return getJDA().getSelfUser();
    }

    @Override
    public Member getSelfMember() {
        return getGuild().getMember(getSelfUser());
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    @Override
    public Guild getGuild() {
        return getJDA().getGuildById(guildId);
    }

    @Override
    public long getGuildId() {
        return guildId;
    }

    @Override
    public TextChannel getChannel() {
        return getJDA().getTextChannelById(channelId);
    }

    @Override
    public long getChannelId() {
        return channelId;
    }

    @Override
    public OffsetDateTime getTime() {
        return time;
    }

    @Override
    public Instant getInstant() {
        return getTime().toInstant();
    }

    @Override
    public JDA getJDA() {
        return jda;
    }

    @Override
    public List<User> getMentionedUsers() {
        return Arrays.stream(mentionedUsers).mapToObj(id -> getJDA().getUserById(id)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<Role> getMentionedRoles() {
        return null;
    }

    @Override
    public List<TextChannel> getMentionedChannels() {
        return null;
    }

    @Override
    public List<Member> getMentionedMembers() {
        return null;
    }

    @Override
    public ICommandEvent serialize() {
        return this;
    }
}
