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
package samurai.messages.listeners;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import samurai.command.Command;
import samurai.command.basic.GenericCommand;

public interface MyEventListener {
    //reactions
    @SubscribeEvent
    default void onGenericMessageReactionEvent(GenericMessageReactionEvent event) {}

    //private messages
    @SubscribeEvent
    default void onPrivateMessageReceivedEvent(PrivateMessageReceivedEvent event) {}

    @SubscribeEvent
    default void onPrivateMessageUpdateEvent(PrivateMessageUpdateEvent event) {}

    //commands
    default void onCommand(Command command) {
        if (command instanceof GenericCommand) onGenericCommand(((GenericCommand) command));
    }

    default void onGenericCommand(GenericCommand command) {}

    //channel messages
    @SubscribeEvent
    default void onChannelMessageReceived(GuildMessageReceivedEvent event) {}

    @SubscribeEvent
    default void onChannelMessageUpdate(GuildMessageUpdateEvent event) {}

}
