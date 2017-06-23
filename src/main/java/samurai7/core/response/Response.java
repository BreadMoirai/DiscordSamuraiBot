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

package samurai7.core.response;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public abstract class Response implements Serializable {

    private long authorId, messageId, channelId, guildId;

    abstract public Message getMessage();

    abstract public void onSend(Message message);

    abstract public void register(ResponseHandler responseHandler);

    abstract public void unregister();

    public final long getAuthorId() {
        return authorId;
    }

    public final void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public final long getMessageId() {
        return messageId;
    }

    public final void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public final long getChannelId() {
        return channelId;
    }

    public final void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public final long getGuildId() {
        return guildId;
    }

    public final void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    @Contract("null -> null")
    public static BasicResponse of(String message) {
        if (message == null || message.isEmpty()) return null;
        return new BasicResponse(new MessageBuilder().append(message).build());
    }

 
    @Contract("null, _ -> null")
    public static BasicResponse ofFormat(String format, Object... args) {
        if (format == null || format.isEmpty()) return null;
        return new BasicResponse(new MessageBuilder().appendFormat(format, args).build());
    }

    @Contract("null -> null; !null -> !null")
    public static BasicResponse of(MessageEmbed embed) {
        if (embed == null) return null;
        return new BasicResponse(new MessageBuilder().setEmbed(embed).build());
    }

    @Contract("null -> null; !null -> !null")
    public static BasicResponse of(Message message) {
        if (message == null) return null;
        return new BasicResponse(message);
    }
}
