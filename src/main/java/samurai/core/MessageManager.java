package samurai.core;

import samurai.command.Command;
import samurai.command.admin.Groovy;
import samurai.entities.base.DynamicMessage;
import samurai.entities.base.SamuraiMessage;
import samurai.events.GuildMessageEvent;
import samurai.events.PrivateMessageEvent;
import samurai.events.ReactionEvent;
import samurai.events.listeners.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TonTL
 * @version 4.x - 3/9/2017
 */
public class MessageManager implements MessageListener, ReactionListener, CommandListener, PrivateListener {

    private final ConcurrentHashMap<Long, LinkedList<DynamicMessage>> listeners;

    private static final LinkedList<DynamicMessage> EMPTY_LIST = new LinkedList<>( Collections.emptyList());

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

    void shutdown() {
        listeners.clear();
    }
//
//    public void submit(long channelId, Message message, Consumer<Message> success) {
//        final TextChannel textChannel = client.getTextChannelById(String.valueOf(channelId));
//        if (textChannel == null) {
//            System.err.println("Channel " + channelId + " does not exist.");
//            channelListeners.remove(channelId);
//            return;
//        }
//        textChannel.sendMessage(message).queue(success, throwable -> {
//            final String throwableMessage = throwable.getMessage();
//            if (throwableMessage.equals(ErrorResponse.UNAUTHORIZED.getMeaning()) || throwableMessage.equals(ErrorResponse.CANNOT_SEND_TO_USER.getMeaning()))
//                System.err.println("Access blocked to channel " + channelId);
//        });
//    }
//
//
//    public void editMessage(long channelId, long messageId, Message message, Consumer<Message> success, Consumer<Throwable> failure) {
//        if (success == null) success = emptyConsumer -> {
//        };
//        getMessage(channelId, messageId, ((Consumer<Message>) message1 -> message1.editMessage(message).queue()).andThen(success), failure);
//
//
//                /*throwable -> {
//            final String throwableMessage = throwable.getMessage();
//            if (throwableMessage.equals(ErrorResponse.UNKNOWN_MESSAGE.getMeaning()))
//                channelListeners.get(channelId).removeIf(dynamicMessage -> dynamicMessage.getMessageId() == messageId);
//            else if (throwableMessage.equals(ErrorResponse.MISSING_PERMISSIONS.getMeaning())) {
//                final List<Permission> permissions = textChannel.getGuild().getMemberById(Bot.ID).getPermissions(textChannel);
//                if (!permissions.contains(Permission.MESSAGE_HISTORY))
//                    textChannel.sendMessage("ERROR \uD83D\uDE35: SamuraiBot works better with **READ MESSAGE HISTORY**.").queue();
//                else if (!textChannel.canTalk())
//                    channelListeners.remove(channelId);
//            }
//        });*/
//
//    }
//
//    private void getMessage(long channelId, long messageId, Consumer<Message> success, Consumer<Throwable> failure) {
//        final TextChannel textChannel = client.getTextChannelById(String.valueOf(channelId));
//        if (textChannel == null || !textChannel.canTalk()) {
//            return;
//        }
//        textChannel.getMessageById(String.valueOf(messageId)).queue(success, failure);
//    }


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


//    //todo error check
//    public void deleteMessage(long channelId, long messageId, Consumer<Throwable> failure) {
//        getMessage(channelId, messageId, message -> message.delete().queue(), failure);
//    }
//
//    public void clearReactions(long channelId, long messageId, Consumer<Throwable> failure) {
//        getMessage(channelId, messageId, message -> message.clearReactions().queue(), failure);
//    }
//
//    //todo perm check
//    public void removeReaction(long channelId, long messageId, long userId, String name) {
//        getMessage(channelId, messageId, message -> {
//            for (MessageReaction messageReaction : message.getReactions())
//                if (messageReaction.getEmote().getName().equals(name)) {
//                    messageReaction.removeReaction(client.getUserById(String.valueOf(userId))).queue();
//                    return;
//                }
//        }, null);
//    }
//
//    public void submit(long channelId, InputStream data, String fileName, Message message) {
//        client.getTextChannelById(String.valueOf(channelId)).sendFile(data, fileName, message).queue();
//    }

}
