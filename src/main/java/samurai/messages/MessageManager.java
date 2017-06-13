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
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.command.Command;
import samurai.messages.annotations.GhostMessage;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.Reloadable;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.base.UniqueMessage;
import samurai.messages.impl.util.Prompt;
import samurai.messages.listeners.ChannelMessageListener;
import samurai.messages.listeners.CommandListener;
import samurai.messages.listeners.PrivateMessageListener;
import samurai.messages.listeners.ReactionListener;
import samurai.qte.QuizMessage;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MessageManager implements ReactionListener, ChannelMessageListener, CommandListener, PrivateMessageListener {

    private final Map<Long, List<DynamicMessage>> listeners;
    private final ScheduledExecutorService executorService;
    private final JDA client;


    public MessageManager(JDA client) {
        this.client = client;
        listeners = new ConcurrentHashMap<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(this::clearInactive, 2, 6, TimeUnit.HOURS);
    }

    private void clearInactive() {
        final Iterator<Map.Entry<Long, List<DynamicMessage>>> iterator = listeners.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Long, List<DynamicMessage>> next = iterator.next();
            final List<DynamicMessage> value = next.getValue();
            value.removeIf(DynamicMessage::isExpired);
            if (value.isEmpty()) iterator.remove();
        }
    }

    public void submit(SamuraiMessage samuraiMessage) {
        if (!verifyUnique(samuraiMessage))
            samuraiMessage.send(this);
    }

    private boolean verifyUnique(SamuraiMessage samuraiMessage) {
        if (samuraiMessage instanceof UniqueMessage) {
            final Class<? extends SamuraiMessage> aClass = samuraiMessage.getClass();
            final UniqueMessage uniqueMessage = (UniqueMessage) samuraiMessage;
            final TextChannel textChannel = client.getTextChannelById(samuraiMessage.getChannelId());
            final long authorId = samuraiMessage.getAuthorId();
            final Optional<DynamicMessage> optionalPrevious;
            switch (uniqueMessage.scope()) {
                case Author:
                    optionalPrevious = listeners.getOrDefault(samuraiMessage.getChannelId(), Collections.emptyList()).stream().filter(dynamicMessage -> dynamicMessage.getClass() == aClass && dynamicMessage.getAuthorId() == authorId).findAny();
                    break;
                case Channel:
                    optionalPrevious = listeners.getOrDefault(samuraiMessage.getChannelId(), Collections.emptyList()).stream().filter(dynamicMessage -> dynamicMessage.getClass() == aClass).findAny();
                    break;
                case Guild:
                    optionalPrevious = textChannel.getGuild().getTextChannels().stream().mapToLong(ISnowflake::getIdLong).mapToObj(listeners::get).filter(Objects::nonNull).flatMap(List::stream).filter(dynamicMessage -> dynamicMessage.getClass() == aClass).findAny();
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
                                yesPrompt.getChannel().clearReactionsById(yesPrompt.getMessageId()).queue();
                                samuraiMessage.replace(this, yesPrompt.getMessageId());
                            },
                            noPrompt -> noPrompt.getChannel().deleteMessageById(noPrompt.getMessageId()).queue());
                    prompt.setAuthorId(samuraiMessage.getAuthorId());
                    prompt.setChannelId(samuraiMessage.getChannelId());
                    prompt.send(this);
                    return true;
                } else {
                    unregister(previousMessage);
                    previousUnique.close(textChannel);
                }
            }
        }
        return false;
    }

    public void register(DynamicMessage dynamicMessage) {
        listeners.putIfAbsent(dynamicMessage.getChannelId(), new CopyOnWriteArrayList<>());
        final List<DynamicMessage> list = listeners.get(dynamicMessage.getChannelId());
        list.add(dynamicMessage);
    }


    public void unregister(DynamicMessage dynamicMessage) {
        final List<DynamicMessage> list = listeners.get(dynamicMessage.getChannelId());
        if (list == null) return;
        list.remove(dynamicMessage);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final List<DynamicMessage> list = listeners.get(event.getChannel().getIdLong());
        if (list == null) return;
        for (DynamicMessage dynamicMessage : list) {
            if (dynamicMessage instanceof ChannelMessageListener)
                ((ChannelMessageListener) dynamicMessage).onGuildMessageReceived(event);
        }

    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        final List<DynamicMessage> list = listeners.get(event.getChannel().getIdLong());
        if (list == null) return;
        for (DynamicMessage dynamicMessage : list) {
            if (dynamicMessage instanceof ChannelMessageListener)
                ((ChannelMessageListener) dynamicMessage).onGuildMessageUpdate(event);
        }

    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        final List<DynamicMessage> list = listeners.get(event.getTextChannel().getIdLong());
        if (list == null) return;
        for (DynamicMessage dynamicMessage : list) {
            if (dynamicMessage instanceof ReactionListener)
                ((ReactionListener) dynamicMessage).onReaction(event);
        }

    }

    @Override
    public void onCommand(Command command) {
        final List<DynamicMessage> list = listeners.get(command.getContext().getChannelId());
        if (list == null) return;
        for (DynamicMessage dynamicMessage : list) {
            if (dynamicMessage instanceof CommandListener)
                ((CommandListener) dynamicMessage).onCommand(command);
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        event.getAuthor().getMutualGuilds().stream().flatMap(guild -> guild.getTextChannels().stream()).filter(TextChannel::canTalk).map(ISnowflake::getId).map(Long::parseLong).filter(listeners::containsKey).flatMap(aLong -> listeners.get(aLong).stream()).filter(dynamicMessage -> dynamicMessage instanceof PrivateMessageListener).forEach(dynamicMessage -> ((PrivateMessageListener) dynamicMessage).onPrivateMessageReceived(event));
    }

    public void remove(long channelId) {
        listeners.remove(channelId);
    }

    public void remove(long channelId, long messageId) {
        final List<DynamicMessage> list = listeners.get(channelId);
        if (list == null) return;
        list.removeIf(next -> next.getMessageId() == messageId && !next.getClass().isAnnotationPresent(GhostMessage.class));
    }

    public JDA getClient() {
        return client;
    }

    public List<Reloadable> shutdown() {
        executorService.shutdown();
        return listeners.values().stream().flatMap(Collection::stream).filter(dynamicMessage -> dynamicMessage instanceof Reloadable).map(dynamicMessage -> (Reloadable) dynamicMessage).collect(Collectors.toList());
    }

}
