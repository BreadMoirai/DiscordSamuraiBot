//package samurai.core;
//
//import net.dv8tion.jda.core.entities.User;
//import samurai.Bot;
//import samurai.SamuraiDiscord;
//import samurai.command.Command;
//import samurai.command.annotations.Admin;
//import samurai.command.annotations.Creator;
//import samurai.command.annotations.Key;
//import samurai.command.annotations.Source;
//import samurai.entities.base.SamuraiMessage;
//import samurai.events.listeners.CommandListener;
//import samurai.osu.OsuTracker;
//
//import java.util.Optional;
//import java.util.concurrent.*;
//
//
///**
// * Controller for the SamuraiBot.
// *
// * @author TonTL
// * @version 4.2
// */
//class CommandHandler implements CommandListener {
//
//    private final ExecutorService commandPool;
//    private final ScheduledExecutorService retrival;
//
//    private final BlockingQueue<Future<Optional<SamuraiMessage>>> actionQueue;
//
//
//    CommandHandler(SamuraiDiscord Samurai) {
//
//        commandPool = Executors.newFixedThreadPool(1);
//
//        retrival = Executors.newSingleThreadScheduledExecutor();
//        retrival.scheduleWithFixedDelay(this::pollAction, 1000000, 1, TimeUnit.NANOSECONDS);
//
//        actionQueue = new LinkedBlockingQueue<>();
//    }
//
//    private void pollAction() {
//        try {
//            Future<Optional<SamuraiMessage>> optionalFuture = actionQueue.take();
//            Optional<SamuraiMessage> messageOptional = optionalFuture.get();
//            messageOptional.ifPresent(messageManager::submit);
//        } catch (ExecutionException e) {
//            Bot.logError(e);
//        } catch (InterruptedException e) {
//            Bot.log("Command Thread Shutdown");
//        }
//    }
//
//    @Override
//    public void onCommand(Command command) {
//        command.setGuild(guildManager.getGuild(command.getGuildId()));
//        Bot.CALLS.incrementAndGet();
//
//        if (!checkAnts(command)) {
//            return;
//        }
//        if (!actionQueue.offer(commandPool.submit(command)))
//            Bot.logError(new RejectedExecutionException("Could not add Action to Queue"));
//    }
//
//    private boolean checkAnts(Command command) {
//        if (command.getClass().isAnnotationPresent(Source.class) && command.getGuildId() != Long.parseLong(Bot.SOURCE_GUILD)) {
//            return false;
//        }
//        if (command.getClass().isAnnotationPresent(Creator.class) && !command.getAuthor().isOwner())
//            return false;
//        if (command.getClass().isAnnotationPresent(Admin.class) && !command.getAuthor().canInteract(command.getAuthor().getGuild().getMember(Bot.getUser(Long.valueOf(Bot.ID))))) {
//            Bot.log(String.format("%s does not have adequate privileges to use `%s`", command.getAuthor().getEffectiveName(), command.getClass().getAnnotation(Key.class).value()[0]));
//            return false;
//        }
//        return true;
//    }
//
//
//    String getPrefix(long id) {
//        return guildManager.getPrefix(id);
//    }
//
//    void shutdown() {
//        Bot.log("Shutting Down");
//        executorPool.shutdownNow();
//        commandPool.shutdownNow();
//        messageManager.shutdown();
//        guildManager.shutdown();
//    }
//
//    public void trackUser(User user) {
//        osuTracker.register(guildManager.getUser(user));
//    }
//
//    public void untrackUser(User user) {
//        osuTracker.unregister(guildManager.getUser(user));
//    }
//}
