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
package samurai.messages.base;

import net.dv8tion.jda.core.entities.Message;
import samurai.messages.MessageManager;

import java.util.function.Consumer;

public abstract class SamuraiMessage {

    private long channelId;
    private long messageId;
    private long guildId;
    private long authorId;

    /**
     * This is the method that retrieves the messages to be sent/updated to.
     *
     * @return the messages that will be sent/replace
     */

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public void send(MessageManager messageManager) {
        messageManager.getClient().getTextChannelById(channelId).sendMessage(initialize()).queue(setId().andThen(this::onReady));
    }

    public void replace(MessageManager messageManager, long messageId) {
        messageManager.getClient().getTextChannelById(channelId).getMessageById(messageId).queue(message -> message.editMessage(initialize()).queue(setId().andThen(this::onReady)));
    }

    public void replace(MessageManager messageManager, Message message) {
        message.editMessage(initialize()).queue(setId().andThen(this::onReady));
    }

    /**
     * provide the initial message to send.
     * @return your message
     */
    protected abstract Message initialize();

    /**
     * This method is used when the message is sent to the channel with initialize()
     * @param message that was sent with initialize()
     */
    protected abstract void onReady(Message message);

    private Consumer<Message> setId() {
        return message -> this.setMessageId(message.getId());
    }

    public long getMessageId() {
        return messageId;
    }

    private void setMessageId(String messageId) {
        this.messageId = Long.parseLong(messageId);
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }
}
