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

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import samurai.messages.annotations.MessageScope;

/**
 * @author TonTL
 * @version 4/19/2017
 */
public interface UniqueMessage {

    /**
     * Returns to what extent this message should be unique
     * <ol>
     *     <li>Author - one per user</li>
     *     <li>Channel - one per channel for all uesrs</li>
     *     <li>Guild - one per guild for all channels & users</li>
     * </ol>
     * @return the scope of uniqueness
     */
    default MessageScope scope() {
        return MessageScope.Author;
    }

    /**
     * States whether the user should be asked to remove the previous instance or cancel.
     * <p>if returns true, prompt() will be called</p>
     * <p>if returns false, the previous instance will have close() called</p>
     * @return true if the user should be asked, false if the previous instance should be removed
     */
    default boolean shouldPrompt() {
        return false;
    }

    /**
     * Provides a message to display to the user
     * <p>defaults to "Another instance of this command is already running. Do you want to remove the previous instance?"</p>
     * @return a Message Object to send to the channel where the duplicate instance is called
     */
    default Message prompt() {
        return new MessageBuilder().append("Another instance of this command is already running. Do you want to remove the previous instance?").build();
    }

    /**
     * This operation automatically unregisters this message. Additional methods that indicate that this instance will no longer recieve events should be called here.
     * @param channel the Text channel of where this message is in.
     */
    void close(TextChannel channel);

}
