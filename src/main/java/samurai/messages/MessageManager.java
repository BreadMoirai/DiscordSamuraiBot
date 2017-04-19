package samurai.messages;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import samurai.command.generic.GenericCommand;
import samurai.messages.annotations.Unique;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.util.Prompt;
import samurai.messages.listeners.ChannelMessageListener;
import samurai.messages.listeners.GenericCommandListener;
import samurai.messages.listeners.PrivateMessageListener;
import samurai.messages.listeners.ReactionListener;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
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
        executorService.scheduleWithFixedDelay(this::clearInactive, 60, 30, TimeUnit.MINUTES);
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
        final Class<? extends SamuraiMessage> aClass = samuraiMessage.getClass();
        final Unique annotation = aClass.getAnnotation(Unique.class);
        if (annotation != null) {
            final long authorId = samuraiMessage.getAuthorId();
            switch (annotation.scope()) {
                case Author:
                    final Optional<DynamicMessage> any = listeners.getOrDefault(samuraiMessage.getChannelId(), EMPTY_DEQUE).stream().filter(dynamicMessage -> dynamicMessage.getClass() == aClass && dynamicMessage.getAuthorId() == authorId).findAny();
                    if (any.isPresent()) {
                        final DynamicMessage previousMessage = any.get();
                        SamuraiMessage prompt = new Prompt(annotation.prompt(), message -> {
                            samuraiMessage.replace(this, message);
                            unregister(previousMessage);
                        },null);
                        prompt.setAuthorId(samuraiMessage.getAuthorId());
                        prompt.setChannelId(samuraiMessage.getChannelId());
                    }
            }
        } else
            samuraiMessage.send(this);

    }

    public void register(DynamicMessage dynamicMessage) {
        listeners.putIfAbsent(dynamicMessage.getChannelId(), new ArrayDeque<>());
        listeners.get(dynamicMessage.getChannelId()).add(dynamicMessage);
    }


    public void unregister(DynamicMessage dynamicMessage) {
        listeners.getOrDefault(dynamicMessage.getChannelId(), EMPTY_DEQUE).removeFirstOccurrence(dynamicMessage);
    }

    @Override
    public void onGuildMessageEvent(GenericGuildMessageEvent event) {
        listeners.getOrDefault(Long.parseLong(event.getChannel().getId()), EMPTY_DEQUE).forEach(dynamicMessage -> {
            if (dynamicMessage instanceof ChannelMessageListener)
                ((ChannelMessageListener) dynamicMessage).onGuildMessageEvent(event);
        });
    }

    @Override
    public void onReaction(MessageReactionAddEvent event) {
        listeners.getOrDefault(Long.parseLong(event.getChannel().getId()), EMPTY_DEQUE).stream().filter(dynamicMessage -> dynamicMessage.getMessageId() == Long.parseLong(event.getMessageId())).findFirst().ifPresent(dynamicMessage -> {
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
    public void onPrivateMessageEvent(GenericPrivateMessageEvent event) {
        event.getAuthor().getMutualGuilds().stream().flatMap(guild -> guild.getTextChannels().stream()).filter(TextChannel::canTalk).map(ISnowflake::getId).map(Long::parseLong).filter(listeners::containsKey).flatMap(aLong -> listeners.get(aLong).stream()).filter(dynamicMessage -> dynamicMessage instanceof PrivateMessageListener).forEach(dynamicMessage -> ((PrivateMessageListener) dynamicMessage).onPrivateMessageEvent(event));
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
