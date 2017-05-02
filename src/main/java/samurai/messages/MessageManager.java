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
package samurai.messages;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.command.basic.GenericCommand;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.base.UniqueMessage;
import samurai.messages.impl.util.Prompt;
import samurai.messages.listeners.ChannelMessageListener;
import samurai.messages.listeners.GenericCommandListener;
import samurai.messages.listeners.PrivateMessageListener;
import samurai.messages.listeners.ReactionListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author TonTL
 * @version 4.x - 3/9/2017
 */
public class MessageManager implements ReactionListener, ChannelMessageListener, GenericCommandListener, PrivateMessageListener {

    private static final ArrayDeque<DynamicMessage> EMPTY_DEQUE;
    private final ConcurrentHashMap<Long, ArrayDeque<DynamicMessage>> listeners;
    private final ScheduledExecutorService executorService;
    private final JDA client;

    static {
        EMPTY_DEQUE = new ArrayDeque<DynamicMessage>(0) {
            @Override
            public void addFirst(DynamicMessage dynamicMessage) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void addLast(DynamicMessage dynamicMessage) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public MessageManager(JDA client) {
        this.client = client;
        listeners = new ConcurrentHashMap<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(this::clearInactive, 2, 2, TimeUnit.HOURS);
    }

    private void clearInactive() {
        final Iterator<Map.Entry<Long, ArrayDeque<DynamicMessage>>> iterator = listeners.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Long, ArrayDeque<DynamicMessage>> next = iterator.next();
            final ArrayDeque<DynamicMessage> value = next.getValue();
            value.removeIf(DynamicMessage::isExpired);
            if (value.isEmpty()) iterator.remove();
        }
    }

    public void submit(SamuraiMessage samuraiMessage) {
        if (samuraiMessage instanceof UniqueMessage) {
            final Class<? extends SamuraiMessage> aClass = samuraiMessage.getClass();
            final UniqueMessage uniqueMessage = (UniqueMessage) samuraiMessage;
            final TextChannel textChannel = client.getTextChannelById(samuraiMessage.getChannelId());
            final long authorId = samuraiMessage.getAuthorId();
            final Optional<DynamicMessage> optionalPrevious;
            switch (uniqueMessage.scope()) {
                case Author:
                    optionalPrevious = listeners.getOrDefault(samuraiMessage.getChannelId(), EMPTY_DEQUE).stream().filter(dynamicMessage -> dynamicMessage.getClass() == aClass && dynamicMessage.getAuthorId() == authorId).findAny();
                    break;
                case Channel:
                    optionalPrevious = listeners.getOrDefault(samuraiMessage.getChannelId(), EMPTY_DEQUE).stream().filter(dynamicMessage -> dynamicMessage.getClass() == aClass).findAny();
                    break;
                case Guild:
                    optionalPrevious = textChannel.getGuild().getTextChannels().stream().mapToLong(ISnowflake::getIdLong).mapToObj(listeners::get).filter(Objects::nonNull).flatMap(ArrayDeque::stream).filter(dynamicMessage -> dynamicMessage.getClass() == aClass).findAny();
                    break;
                default:
                    throw new UnsupportedOperationException("Scope not found");
            }

            if (optionalPrevious.isPresent()) {
                final DynamicMessage previousMessage = optionalPrevious.get();
                final UniqueMessage previousUnique = (UniqueMessage) previousMessage;
                if (uniqueMessage.shouldPrompt()) {
                    SamuraiMessage prompt = new Prompt(uniqueMessage.prompt(),
                            yesPrompt -> {
                                previousUnique.close(client.getTextChannelById(previousMessage.getChannelId()));
                                yesPrompt.getMessage().clearReactions().queue();
                                samuraiMessage.replace(this, yesPrompt.getMessage());
                            },
                            noPrompt -> noPrompt.getMessage().delete().queue());
                    prompt.setAuthorId(samuraiMessage.getAuthorId());
                    prompt.setChannelId(samuraiMessage.getChannelId());
                    prompt.send(this);
                    return;
                } else {
                    unregister(previousMessage);
                    previousUnique.close(textChannel);
                }
            }
        }
        samuraiMessage.send(this);
    }

    public void register(DynamicMessage dynamicMessage) {
        listeners.putIfAbsent(dynamicMessage.getChannelId(), new ArrayDeque<>());
        listeners.get(dynamicMessage.getChannelId()).addLast(dynamicMessage);
    }


    public void unregister(DynamicMessage dynamicMessage) {
        listeners.getOrDefault(dynamicMessage.getChannelId(), EMPTY_DEQUE).removeFirstOccurrence(dynamicMessage);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        listeners.getOrDefault(Long.parseLong(event.getChannel().getId()), EMPTY_DEQUE).forEach(dynamicMessage -> {
            if (dynamicMessage instanceof ChannelMessageListener)
                ((ChannelMessageListener) dynamicMessage).onGuildMessageReceived(event);
        });
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        listeners.getOrDefault(Long.parseLong(event.getChannel().getId()), EMPTY_DEQUE).forEach(dynamicMessage -> {
            if (dynamicMessage instanceof ChannelMessageListener)
                ((ChannelMessageListener) dynamicMessage).onGuildMessageUpdate(event);
        });
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        System.out.println("event = " + event.getReactionEmote().getName() + event.getUser().getName());
        listeners.getOrDefault(event.getChannel().getIdLong(), EMPTY_DEQUE).stream().filter(dynamicMessage -> dynamicMessage.getMessageId() == event.getMessageIdLong()).findFirst().ifPresent(dynamicMessage -> {
            if (dynamicMessage instanceof ReactionListener)
                ((ReactionListener) dynamicMessage).onReaction(event);
        });
    }

    @Override
    public void onCommand(GenericCommand command) {
        listeners.getOrDefault(command.getContext().getChannelId(), EMPTY_DEQUE).forEach(dynamicMessage -> {
            if (dynamicMessage instanceof GenericCommandListener)
                ((GenericCommandListener) dynamicMessage).onCommand(command);
        });
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        event.getAuthor().getMutualGuilds().stream().flatMap(guild -> guild.getTextChannels().stream()).filter(TextChannel::canTalk).map(ISnowflake::getId).map(Long::parseLong).filter(listeners::containsKey).flatMap(aLong -> listeners.get(aLong).stream()).filter(dynamicMessage -> dynamicMessage instanceof PrivateMessageListener).forEach(dynamicMessage -> ((PrivateMessageListener) dynamicMessage).onPrivateMessageReceived(event));
    }

    public void remove(long channelId) {
        listeners.remove(channelId);
    }

    public void remove(long channelId, long messageId) {
        listeners.getOrDefault(channelId, EMPTY_DEQUE).removeIf(dynamicMessage -> dynamicMessage.getMessageId() == messageId);
    }

    public JDA getClient() {
        return client;
    }

    public void shutdown() {
        listeners.clear();
        executorService.shutdown();
    }
}
