package samurai.core;

import samurai.command.Command;
import samurai.command.admin.Groovy;
import samurai.entities.base.DynamicMessage;
import samurai.entities.base.SamuraiMessage;
import samurai.events.GuildMessageEvent;
import samurai.events.PrivateMessageEvent;
import samurai.events.ReactionEvent;
import samurai.events.listeners.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author TonTL
 * @version 4.x - 3/9/2017
 */
public class MessageManager implements MessageListener, ReactionListener, CommandListener, PrivateListener {

    private static final LinkedList<DynamicMessage> EMPTY_LIST = new LinkedList<>(Collections.emptyList());
    private final ConcurrentHashMap<Long, LinkedList<DynamicMessage>> listeners;
    private final ScheduledExecutorService executorService;

    public MessageManager() {
        listeners = new ConcurrentHashMap<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(this::clearInactive, 60, 30, TimeUnit.MINUTES);
    }


    private void clearInactive() {
        final Iterator<Map.Entry<Long, LinkedList<DynamicMessage>>> iterator = listeners.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Long, LinkedList<DynamicMessage>> next = iterator.next();
            final LinkedList<DynamicMessage> value = next.getValue();
            value.removeIf(DynamicMessage::isExpired);
            if (value.isEmpty()) iterator.remove();
        }
    }

    public void submit(SamuraiMessage samuraiMessage) {
        samuraiMessage.onReady(this);
    }

    public void register(DynamicMessage dynamicMessage) {
        if (dynamicMessage instanceof SamuraiListener) {
            listeners.putIfAbsent(dynamicMessage.getChannelId(), new LinkedList<>());
            listeners.get(dynamicMessage.getChannelId()).add(dynamicMessage);
        }
    }


    @Override
    public void onGuildMessageEvent(GuildMessageEvent event) {
        listeners.getOrDefault(event.getChannelId(), EMPTY_LIST).forEach(dynamicMessage -> {
            if (dynamicMessage instanceof MessageListener)
                ((MessageListener) dynamicMessage).onGuildMessageEvent(event);
        });
    }

    @Override
    public void onReaction(ReactionEvent event) {
        listeners.getOrDefault(event.getChannelId(), EMPTY_LIST).stream().filter(dynamicMessage -> dynamicMessage.getMessageId() == event.getMessageId()).findFirst().ifPresent(dynamicMessage -> {
            if (dynamicMessage instanceof ReactionListener)
                ((ReactionListener) dynamicMessage).onReaction(event);
        });
    }

    @Override
    public void onCommand(Command command) {
        listeners.getOrDefault(command.getContext().getChannelId(), EMPTY_LIST).forEach(dynamicMessage -> {
            if (dynamicMessage instanceof CommandListener)
                ((CommandListener) dynamicMessage).onCommand(command);
        });
    }

    @Override
    public void onPrivateMessageEvent(PrivateMessageEvent event) {
        listeners.getOrDefault(event.getChannelId(), EMPTY_LIST).forEach(dynamicMessage -> {
            if (dynamicMessage instanceof PrivateListener)
                ((PrivateListener) dynamicMessage).onPrivateMessageEvent(event);
        });
    }


    public void unregister(DynamicMessage dynamicMessage) {
        listeners.getOrDefault(dynamicMessage.getChannelId(), EMPTY_LIST).removeFirstOccurrence(dynamicMessage);
    }

    public void remove(long channelId) {
        listeners.remove(channelId);
    }

    public void remove(long channelId, long messageId) {
        listeners.getOrDefault(channelId, EMPTY_LIST).removeIf(dynamicMessage -> dynamicMessage.getMessageId() == messageId);
    }

    public void shutdown() {
        listeners.clear();
        executorService.shutdown();
    }

}
