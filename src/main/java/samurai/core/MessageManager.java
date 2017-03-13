package samurai.core;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.ErrorResponse;
import samurai.command.Command;
import samurai.command.admin.Groovy;
import samurai.entities.base.DynamicMessage;
import samurai.entities.base.SamuraiMessage;
import samurai.events.GuildMessageEvent;
import samurai.events.PrivateMessageEvent;
import samurai.events.ReactionEvent;
import samurai.events.listeners.CommandListener;
import samurai.events.listeners.MessageListener;
import samurai.events.listeners.PrivateListener;
import samurai.events.listeners.ReactionListener;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 4.x - 3/9/2017
 */
public class MessageManager implements MessageListener, ReactionListener, CommandListener, PrivateListener {

    private final JDA client;
    private final ConcurrentHashMap<Long, DynamicMessage> reactionListeners;
    private final ConcurrentHashMap<Long, LinkedList<DynamicMessage>> channelListeners;

    MessageManager(JDA client) {
        this.client = client;

        reactionListeners = new ConcurrentHashMap<>();
        channelListeners = new ConcurrentHashMap<>();
        Groovy.addBinding("mm", this);
    }


    void clearInactive() {
        reactionListeners.forEachValue(1000L, dynamicMessage -> {
            if (dynamicMessage.isExpired()) reactionListeners.remove(dynamicMessage.getMessageId(), dynamicMessage);
        });
        channelListeners.forEachValue(1000L, dynamicMessages -> dynamicMessages.removeIf(DynamicMessage::isExpired));
    }

    void submit(SamuraiMessage samuraiMessage) {
        samuraiMessage.onReady(this);
    }

    public void register(DynamicMessage dynamicMessage) {
        if (dynamicMessage instanceof ReactionListener)
            reactionListeners.put(dynamicMessage.getMessageId(), dynamicMessage);
        if (dynamicMessage instanceof MessageListener || dynamicMessage instanceof CommandListener || dynamicMessage instanceof PrivateListener) {
            if (!channelListeners.containsKey(dynamicMessage.getChannelId()))
                channelListeners.putIfAbsent(dynamicMessage.getChannelId(), new LinkedList<>());
            channelListeners.get(dynamicMessage.getChannelId()).add(dynamicMessage);
        }
    }

    @Override
    public void onMessageEvent(GuildMessageEvent event) {
        if (channelListeners.containsKey(event.getChannelId()))
            channelListeners.get(event.getChannelId()).forEach(dynamicMessage -> {
                if (dynamicMessage instanceof MessageListener)
                    ((MessageListener) dynamicMessage).onMessageEvent(event);
            });
    }

    @Override
    public void onReaction(ReactionEvent event) {
        if (reactionListeners.containsKey(event.getMessageId()))
            ((ReactionListener) reactionListeners.get(event.getMessageId())).onReaction(event);
    }

    @Override
    public void onCommand(Command command) {
        if (channelListeners.containsKey(command.getChannelId()))
            channelListeners.get(command.getChannelId()).forEach(dynamicMessage -> {
                if (dynamicMessage instanceof CommandListener)
                    ((CommandListener) dynamicMessage).onCommand(command);
            });
    }

    @Override
    public void onPrivateMessageEvent(PrivateMessageEvent event) {
        if (channelListeners.containsKey(event.getChannelId()))
            channelListeners.get(event.getChannelId()).forEach(dynamicMessage -> {
                if (dynamicMessage instanceof PrivateListener)
                    ((PrivateListener) dynamicMessage).onPrivateMessageEvent(event);
            });
    }

    void shutdown() {
        reactionListeners.clear();
    }

    public void submit(long channelId, Message message, Consumer<Message> success) {
        final TextChannel textChannel = client.getTextChannelById(String.valueOf(channelId));
        if (textChannel == null) {
            System.err.println("Channel " + channelId + " does not exist.");
            channelListeners.remove(channelId);
            return;
        }
        textChannel.sendMessage(message).queue(success, throwable -> {
            final String throwableMessage = throwable.getMessage();
            if (throwableMessage.equals(ErrorResponse.UNAUTHORIZED.getMeaning()) || throwableMessage.equals(ErrorResponse.CANNOT_SEND_TO_USER.getMeaning()))
                System.err.println("Access blocked to channel " + channelId);
        });
    }


    public void editMessage(long channelId, long messageId, Message message, Consumer<Message> success, Consumer<Throwable> failure) {
        if (success == null) success = emptyConsumer -> {
        };
        getMessage(channelId, messageId, ((Consumer<Message>) message1 -> message1.editMessage(message).queue()).andThen(success), failure);


                /*throwable -> {
            final String throwableMessage = throwable.getMessage();
            if (throwableMessage.equals(ErrorResponse.UNKNOWN_MESSAGE.getMeaning()))
                channelListeners.get(channelId).removeIf(dynamicMessage -> dynamicMessage.getMessageId() == messageId);
            else if (throwableMessage.equals(ErrorResponse.MISSING_PERMISSIONS.getMeaning())) {
                final List<Permission> permissions = textChannel.getGuild().getMemberById(Bot.ID).getPermissions(textChannel);
                if (!permissions.contains(Permission.MESSAGE_HISTORY))
                    textChannel.sendMessage("ERROR \uD83D\uDE35: SamuraiBot works better with **READ MESSAGE HISTORY**.").queue();
                else if (!textChannel.canTalk())
                    channelListeners.remove(channelId);
            }
        });*/

    }

    private void getMessage(long channelId, long messageId, Consumer<Message> success, Consumer<Throwable> failure) {
        final TextChannel textChannel = client.getTextChannelById(String.valueOf(channelId));
        if (textChannel == null || !textChannel.canTalk()) {
            return;
        }
        textChannel.getMessageById(String.valueOf(messageId)).queue(success, failure);
    }

    boolean hasMessageListener(long l) {
        return reactionListeners.containsKey(l);
    }

    boolean hasChannelListener(long l) {
        return channelListeners.containsKey(l);
    }

    public void unregister(DynamicMessage dynamicMessage) {
        reactionListeners.remove(dynamicMessage.getMessageId(), dynamicMessage);
        final LinkedList<DynamicMessage> listenerList = channelListeners.get(dynamicMessage.getChannelId());
        if (listenerList == null) return;
        listenerList.remove(dynamicMessage);
        if (listenerList.isEmpty())
            channelListeners.remove(dynamicMessage.getChannelId());
    }

    //todo error check
    public void deleteMessage(long channelId, long messageId, Consumer<Throwable> failure) {
        getMessage(channelId, messageId, message -> message.delete().queue(), failure);
    }

    public void clearReactions(long channelId, long messageId, Consumer<Throwable> failure) {
        getMessage(channelId, messageId, message -> message.clearReactions().queue(), failure);
    }

    //todo perm check
    public void removeReaction(long channelId, long messageId, long userId, String name) {
        getMessage(channelId, messageId, message -> {
            for (MessageReaction messageReaction : message.getReactions())
                if (messageReaction.getEmote().getName().equals(name)) {
                    messageReaction.removeReaction(client.getUserById(String.valueOf(userId))).queue();
                    return;
                }
        }, null);
    }

    public void submit(long channelId, InputStream data, String fileName, Message message) {
        client.getTextChannelById(String.valueOf(channelId)).sendFile(data, fileName, message).queue();
    }

}
