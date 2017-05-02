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
package samurai.messages.impl.util;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import org.apache.commons.lang3.StringUtils;
import samurai.command.debug.ExampleCommand;
import samurai.messages.base.DynamicMessage;
import samurai.messages.listeners.ChannelMessageListener;
import samurai.messages.listeners.ReactionListener;
import samurai.util.CircularlyLinkedList;
import samurai.util.MessageUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A simple template class to explain functionality of this class
 * A DynamicMessage works on stages
 *
 * @author TonTL
 * @version 5.0
 * @see ExampleCommand
 * @since 3.10.2017
 */
public class ExampleMessage extends DynamicMessage implements ReactionListener, ChannelMessageListener {
    //In order for the message to update, it should implement a listener
    //ex. ReactionListener, MessageListener, GenericCommandListener, PrivateListener, CommandListener

    // should probably have one of these, a list of discord emojis;
    private static final List<String> REACTIONS = Collections.unmodifiableList(Arrays.asList("\uD83D\uDD04", "\uD83D\uDD12"));
    private CircularlyLinkedList<TemplateState> states;

    // this is the first command that is called
    @Override
    protected Message initialize() {
        //step 1. Build a message
        MessageBuilder mb = new MessageBuilder();
        //add some text
        mb.append("This is a dynamic message");
        //let's build it
        Message m = mb.build();

        //now we want to send it out.
        return m;
        //once the message is sent, onReady will be called with the sent message.
    }

    //this is called when the message has been sent
    @Override
    protected void onReady(Message message) {
        //this adds the reactions to the message to use as a menu
        MessageUtil.addReaction(message, REACTIONS);

        states = new CircularlyLinkedList<>();
        states.insert(new ReverseState());
        states.insert(new AppendState());
        states.insert(new CopyState());
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        //first let's check if the event has the reaction we want
        final String name = event.getReaction().getEmote().getName();
        //name is name of the reaction/emoji
        if (!REACTIONS.contains(name)) return;


        //if we get a reaction circle arrows
        if (name.equals(REACTIONS.get(0))) {
            //let's change the state
            states.advance(); //the next time a message is sent, the message will be updated with onGuildMessageEvent

            //now let's remove it for the user's convienence
            event.getReaction().removeReaction(event.getUser()).queue();
        }
        //else if we get the lock reaction
        else if (name.equals(REACTIONS.get(1))) {
            //let's clear the reactions so that the user knows that it's not taking anymore events
            event.getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> message.clearReactions().queue());

            //we'll unregister ourselves and thus no longer listen to any events;
            unregister();
            //all references to this class should be lost at this point
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        states.current().onGuildMessageReceived(event);
    }

    //state pattern classes
    private abstract class TemplateState implements ChannelMessageListener {
        //if you could do without a reference to the context, that could be marginally better
        //like a singleton with enums or something
    }

    private class CopyState extends TemplateState {

        @Override
        public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
            event.getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> message.editMessage("**COPY STATE**\n" + event.getMessage().getContent()).queue());
        }
    }


    private class ReverseState extends TemplateState {

        @Override
        public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
            event.getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> message.editMessage("**REVERSE STATE**\n" + StringUtils.reverse(message.getContent())).queue());
        }

    }


    private class AppendState extends TemplateState {

        @Override
        public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
            event.getChannel().getMessageById(String.valueOf(getMessageId())).queue(message -> message.editMessage(message.getContent().contains("APPEND STATE") ? "" : "**APPEND STATE**\n" + message.getContent() + '\n' + event.getMessage().getContent()).queue());
        }

    }


}
