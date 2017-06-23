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
package samurai7.core.impl;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import samurai7.core.ICommandEvent;
import samurai7.util.DiscordPatterns;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Matcher;

public class MessageReceivedCommandEvent implements ICommandEvent {

    private GenericGuildMessageEvent event;
    private Message message;
    private String prefix;
    private String key;
    private String content;

    public MessageReceivedCommandEvent(GenericGuildMessageEvent event, Message message) {
        this.event = event;
        this.message = message;
    }

    @Override
    public boolean validate(String prefix) {
        this.prefix = prefix;
        String contentRaw = message.getContentRaw();
        final Matcher matcher = DiscordPatterns.USER_MENTION_PREFIX.matcher(contentRaw);
        if (matcher.matches()) {
            contentRaw = contentRaw.substring(matcher.end(1)).trim();
            final String[] split = DiscordPatterns.WHITE_SPACE.split(contentRaw, 2);
            key = split[0];
            content = split[1].trim();
            return true;
        } else {
            if (contentRaw.startsWith(prefix)) {
                final String[] split = DiscordPatterns.WHITE_SPACE.split(contentRaw.substring(prefix.length()), 2);
                key = split[0];
                content = split[1].trim();
                return true;
            } return false;
        }
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
        return message.getAuthor();
    }

    @Override
    public long getAuthorId() {
        return getAuthor().getIdLong();
    }

    @Override
    public Member getMember() {
        return getGuild().getMember(getAuthor());
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
        return message.getIdLong();
    }

    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    @Override
    public long getGuildId() {
        return getGuild().getIdLong();
    }

    @Override
    public TextChannel getChannel() {
        return event.getChannel();
    }

    @Override
    public long getChannelId() {
        return getChannel().getIdLong();
    }

    @Override
    public OffsetDateTime getTime() {
        return message.isEdited() ? message.getEditedTime() : message.getCreationTime();
    }

    @Override
    public Instant getInstant() {
        return getTime().toInstant();
    }

    @Override
    public JDA getJDA() {
        return event.getJDA();
    }

    @Override
    public List<User> getMentionedUsers() {
        return message.getMentionedUsers();
    }

    @Override
    public List<Role> getMentionedRoles() {
        return message.getMentionedRoles();
    }

    @Override
    public List<TextChannel> getMentionedChannels() {
        return message.getMentionedChannels();
    }

    @Override
    public List<Member> getMentionedMembers() {
        return message.getMentionedMembers();
    }

    @Override
    public ICommandEvent serialize() {
        return new SerializableCommandEvent(this);
    }
}
