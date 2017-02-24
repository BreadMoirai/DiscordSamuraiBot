package samurai;

import net.dv8tion.jda.core.JDA;
import samurai.action.Action;
import samurai.annotations.*;
import samurai.data.SamuraiGuild;
import samurai.data.SamuraiStore;
import samurai.message.DynamicMessage;
import samurai.message.SamuraiMessage;
import samurai.message.modifier.Direct;
import samurai.message.modifier.MessageEdit;
import samurai.message.modifier.Reaction;

import java.util.Optional;
import java.util.concurrent.*;


/**
 * Controller for the SamuraiBot.
 *
 * @author TonTL
 * @version 4.2
 */
public class SamuraiController {

    private final ExecutorService commandPool;
    private final ScheduledExecutorService executorPool;
    private final BlockingQueue<Future<Optional<SamuraiMessage>>> actionQueue;
    private final BlockingQueue<Future<MessageEdit>> reactionQueue;
    private final ConcurrentHashMap<Long, DynamicMessage> messageMap;
    private final ConcurrentHashMap<Long, SamuraiGuild> guildMap;
    private JDA client;


    SamuraiController() {
        commandPool = Executors.newFixedThreadPool(1);
        actionQueue = new LinkedBlockingQueue<>();
        reactionQueue = new LinkedBlockingQueue<>();
        messageMap = new ConcurrentHashMap<>();
        guildMap = new ConcurrentHashMap<>();
        executorPool = Executors.newScheduledThreadPool(3);
        executorPool.scheduleWithFixedDelay(this::takeReaction, 1000, 1, TimeUnit.MILLISECONDS);
        executorPool.scheduleWithFixedDelay(this::takeAction, 1000, 1, TimeUnit.MILLISECONDS);
        executorPool.scheduleAtFixedRate(this::clearInactive, 60, 15, TimeUnit.MINUTES);
    }

    void execute(Action action) {
        Bot.CALLS.incrementAndGet();
        if (!checkAnts(action)) {
            return;
        }
        if (!actionQueue.offer(commandPool.submit(action)))
            Bot.logError(new RejectedExecutionException("Could not add Action to Queue"));
    }

    private boolean checkAnts(Action action) {
        if (action.getClass().isAnnotationPresent(Source.class) && action.getGuildId() != Long.parseLong(Bot.SOURCE_GUILD)) {
            return false;
        }
        if (action.getClass().isAnnotationPresent(Creator.class) && !action.getAuthor().isOwner())
            return false;
        if (action.getClass().isAnnotationPresent(Admin.class) && !action.getAuthor().canInteract(client.getGuildById(String.valueOf(action.getGuildId())).getSelfMember())) {
            Bot.log(String.format("%s does not have adequate privileges to use `%s`", action.getAuthor().getEffectiveName(), action.getClass().getAnnotation(Key.class).value()));
            return false;
        }
        if (action.getClass().isAnnotationPresent(Client.class)) action.setClient(client);
        if (action.getClass().isAnnotationPresent(Guild.class)) {
            action.setGuild(guildMap.get(action.getGuildId()));
        }
        return true;
    }

    void execute(Reaction reaction) {
        System.out.println("Reaction Found: " + reaction.getName());
        DynamicMessage samuraiMessage = messageMap.get(reaction.getMessageId());
        if (samuraiMessage.setValidReaction(reaction)) {
            System.out.println("Executing...");
            if (!reactionQueue.offer(commandPool.submit(samuraiMessage)))
                Bot.log("Could not add ReAction to Queue");
        }
    }

    void execute(Direct message) {

    }

    private void takeReaction() {
        try {
            final Future<MessageEdit> editFuture = reactionQueue.take();
            if (editFuture == null)
                return;
            final MessageEdit edit = editFuture.get();
            client.getTextChannelById(String.valueOf(edit.getChannelId())).editMessageById(String.valueOf(edit.getMessageId()), edit.getContent()).queue(edit.getConsumer());
        } catch (ExecutionException e) {
            Bot.logError(e);
        } catch (InterruptedException e) {
            Bot.log("Reaction Thread Shutdown");
        }
    }

    private void takeAction() {
        try {
            Future<Optional<SamuraiMessage>> smOption = actionQueue.take();
            if (!smOption.get().isPresent()) return;
            SamuraiMessage samuraiMessage = smOption.get().get();

            //This is not the best practice.
            //if FixedMessage and DynamicMessage extends SamuraiMessage,
            // should exhibit same behavior
            // remove and merge if-else block
//            if (samuraiMessage instanceof DynamicMessage) {
//                DynamicMessage dynamicMessage = (DynamicMessage) samuraiMessage;
//                client.getTextChannelById(String.valueOf(dynamicMessage.getChannelId())).sendMessage(dynamicMessage.getMessage()).queue(dynamicMessage.getConsumer().get());
//
//                messageMap.putIfAbsent(dynamicMessage.getMessageId(), dynamicMessage);
//
//            } else if (samuraiMessage instanceof FixedMessage) {
//                if (((FixedMessage) samuraiMessage).getConsumer().isPresent())
//                    client.getTextChannelById(String.valueOf(samuraiMessage.getChannelId())).sendMessage(samuraiMessage.getMessage()).queue(((FixedMessage) samuraiMessage).getConsumer().get());
//                else
//                    client.getTextChannelById(String.valueOf(samuraiMessage.getChannelId())).sendMessage(samuraiMessage.getMessage()).queue();
//            }

            client.getTextChannelById(String.valueOf(samuraiMessage.getChannelId())).sendMessage(samuraiMessage.getMessage()).queue(samuraiMessage.isPersistent() ? samuraiMessage.getConsumer().andThen(message -> messageMap.put(Long.valueOf(message.getId()), (DynamicMessage) samuraiMessage)) : samuraiMessage.getConsumer());


        } catch (ExecutionException e) {
            Bot.logError(e);
        } catch (InterruptedException e) {
            Bot.log("Command Thread Shutdown");
        }
    }

    private void clearInactive() {
        messageMap.forEachValue(1000L, message -> {
            if (message.isExpired()) {
                messageMap.remove(message.getMessageId());
            }
        });
        guildMap.forEachValue(100L, guild -> {
            if (guild.isActive()) guild.setInactive();
            else {
                SamuraiStore.writeGuild(guild);
                if (!guildMap.remove(guild.getGuildId(), guild))
                    Bot.log("Failed to remove " + guild.getGuildId());
            }
        });
    }

    boolean isWatching(long messageId) {
        return messageMap.containsKey(messageId);
    }

    void setJDA(JDA jda) {
        this.client = jda;
    }

    String getPrefix(long id) {
        if (guildMap.containsKey(id))
            return guildMap.get(id).getPrefix();
        else {
            if (SamuraiStore.containsGuild(id)) {
                SamuraiGuild guild = SamuraiStore.readGuild(id);
                if (guild == null) {
                    Bot.log(String.format("Could not read data for Guild %d", id));
                    return "!";
                }
                guildMap.put(id, guild);
                return guild.getPrefix();
            } else {
                guildMap.put(id, new SamuraiGuild(id));
                return "!";
            }
        }
    }

    void shutdown() {
        Bot.log("Shutting Down");
        executorPool.shutdownNow();
        commandPool.shutdownNow();
        for (SamuraiGuild g : guildMap.values())
            SamuraiStore.writeGuild(g);
        client.shutdown();
    }
}
