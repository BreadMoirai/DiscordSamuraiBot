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
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TonTL
 * @version 4.x - 3/9/2017
 */
public class MessageManager implements MessageListener, ReactionListener, CommandListener, PrivateListener {

    private static final LinkedList<DynamicMessage> EMPTY_LIST = new LinkedList<>( Collections.emptyList());
    private final ConcurrentHashMap<Long, LinkedList<DynamicMessage>> listeners;

    public MessageManager() {
        Groovy.addBinding("mm", this);
        listeners = new ConcurrentHashMap<>();
    }


    void clearInactive() {
        listeners.values().forEach(linkedList -> linkedList.removeIf(DynamicMessage::isExpired));
    }

    public void submit(SamuraiMessage samuraiMessage) {
        samuraiMessage.onReady();
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
        listeners.getOrDefault(event.getChannelId(), EMPTY_LIST).forEach(dynamicMessage -> {
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
        final LinkedList<DynamicMessage> messageLinkedList = listeners.get(dynamicMessage.getChannelId());
        messageLinkedList.removeFirstOccurrence(dynamicMessage);
    }

    public void remove(long channelId) {
        listeners.remove(channelId);
    }

    public void remove(long channelId, long messageId) {
        listeners.getOrDefault(channelId, EMPTY_LIST).removeIf(dynamicMessage -> dynamicMessage.getMessageId()==messageId);

    }

    public void shutdown() {
        listeners.clear();
    }

}
