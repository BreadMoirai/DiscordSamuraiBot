package samurai.core;

import samurai.core.command.Command;
import samurai.core.command.annotations.Admin;
import samurai.core.command.annotations.Creator;
import samurai.core.command.annotations.Key;
import samurai.core.command.annotations.Source;
import samurai.core.entities.DynamicMessage;
import samurai.core.entities.SamuraiMessage;
import samurai.core.entities.modifier.DynamicMessageResponse;
import samurai.core.events.ReactionEvent;

import java.util.Optional;
import java.util.concurrent.*;


/**
 * Controller for the SamuraiBot.
 *
 * @author TonTL
 * @version 4.2
 */
class Samurai {

    private final ExecutorService commandPool;
    private final ScheduledExecutorService executorPool;

    private final BlockingQueue<Future<Optional<SamuraiMessage>>> actionQueue;

    private final MessageManager messageManager;
    private final GuildManager guildManager;


    Samurai(MessageManager messageManager) {
        this.messageManager = messageManager;
        guildManager = new GuildManager();

        commandPool = Executors.newFixedThreadPool(1);

        executorPool = Executors.newScheduledThreadPool(3);
        executorPool.scheduleWithFixedDelay(this::pollAction, 1000, 1, TimeUnit.NANOSECONDS);
        executorPool.scheduleAtFixedRate(this::clearInactive, 60, 15, TimeUnit.MINUTES);

        actionQueue = new LinkedBlockingQueue<>();

    }

    private void pollAction() {
        try {
            Future<Optional<SamuraiMessage>> optionalFuture = actionQueue.take();
            Optional<SamuraiMessage> messageOptional = optionalFuture.get();
            messageOptional.ifPresent(samuraiMessage -> messageManager.submit(samuraiMessage));
        } catch (ExecutionException e) {
            Bot.logError(e);
        } catch (InterruptedException e) {
            Bot.log("Command Thread Shutdown");
        }
    }

    void onCommand(Command command) {
        command.setGuild(guildManager.getGuild(command.getGuildId()));
        Bot.CALLS.incrementAndGet();

        if (!checkAnts(command)) {
            return;
        }
        if (!actionQueue.offer(commandPool.submit(command)))
            Bot.logError(new RejectedExecutionException("Could not add Action to Queue"));
    }

    private boolean checkAnts(Command command) {
        if (command.getClass().isAnnotationPresent(Source.class) && command.getGuildId() != Long.parseLong(Bot.SOURCE_GUILD)) {
            return false;
        }
        if (command.getClass().isAnnotationPresent(Creator.class) && !command.getAuthor().isOwner())
            return false;
        if (command.getClass().isAnnotationPresent(Admin.class) && !command.getAuthor().canInteract(command.getAuthor().getGuild().getMember(Bot.getUser(Long.valueOf(Bot.ID))))) {
            Bot.log(String.format("%s does not have adequate privileges to use `%s`", command.getAuthor().getEffectiveName(), command.getClass().getAnnotation(Key.class).value()[0]));
            return false;
        }
        return true;
    }

    void onReaction(ReactionEvent reaction) {
        System.out.println("Reaction Found: " + reaction.getName());
        DynamicMessage samuraiMessage = messageMap.get(reaction.getMessageId());
        if (samuraiMessage.setValidReaction(reaction)) {
            System.out.println("Executing...");
            if (!reactionQueue.offer(commandPool.submit(samuraiMessage)))
                Bot.log("Could not add ReAction to Queue");
        }
    }

    private void takeReaction() {
        try {
            final Future<DynamicMessageResponse> editFuture = reactionQueue.take();
            if (editFuture == null)
                return;
            final DynamicMessageResponse edit = editFuture.get();
            client.getTextChannelById(String.valueOf(edit.getChannelId())).editMessageById(String.valueOf(edit.getMessageId()), edit.getContent()).queue(edit.getConsumer());
            if (edit.isDead()) messageMap.remove(edit.getMessageId());
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
            client.getTextChannelById(String.valueOf(samuraiMessage.getChannelId())).sendMessage(samuraiMessage.getMessage()).queue(samuraiMessage.isPersistent() ? samuraiMessage.getConsumer().andThen(message -> messageMap.put(Long.valueOf(message.getId()), (DynamicMessage) samuraiMessage)) : samuraiMessage.getConsumer());
        } catch (ExecutionException e) {
            Bot.logError(e);
        } catch (InterruptedException e) {
            Bot.log("Command Thread Shutdown");
        }
    }


    private void clearInactive() {
        messageManager.clearInactive();
        guildManager.clearInactive();
    }

    String getPrefix(long id) {
        return guildManager.getPrefix(id);
    }

    void shutdown() {
        Bot.log("Shutting Down");
        executorPool.shutdownNow();
        commandPool.shutdownNow();
        guildManager.shutdown();
    }

    boolean isWatching(long l) {
        return messageManager.isWatching(l);
    }
}
