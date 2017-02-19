package samurai;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Message;
import org.reflections.Reflections;
import samurai.action.Action;
import samurai.annotations.*;
import samurai.data.SamuraiGuild;
import samurai.message.SamuraiMessage;
import samurai.message.dynamic.DynamicMessage;
import samurai.message.fixed.FixedMessage;
import samurai.message.modifier.MessageEdit;
import samurai.message.modifier.Reaction;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Controller for the SamuraiBot.
 *
 * @author TonTL
 * @version 4.2
 */
public class SamuraiController {
    public static final AtomicInteger callsMade = new AtomicInteger(0);

    private static Channel officialChannel;
    private final ExecutorService commandPool;
    private final BlockingQueue<Future<Optional<SamuraiMessage>>> actionQueue;
    private final BlockingQueue<Future<MessageEdit>> reactionQueue;
    private final ConcurrentHashMap<Long, DynamicMessage> messageMap;
    private final HashMap<String, Class<? extends Action>> actionMap;
    private final ConcurrentHashMap<Long, SamuraiGuild> osuGuildMap;
    private JDA client;
    private SamuraiListener listener;
    //private boolean running;


    SamuraiController(SamuraiListener listener) {
        this.listener = listener;
        commandPool = Executors.newCachedThreadPool();
        actionQueue = new LinkedBlockingQueue<>();
        reactionQueue = new LinkedBlockingQueue<>();
        messageMap = new ConcurrentHashMap<>();
        actionMap = new HashMap<>();
        osuGuildMap = new ConcurrentHashMap<>();
        //running = true;
        initActions();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(this::takeReaction, 1000, 1, TimeUnit.MILLISECONDS);
        executorService.scheduleWithFixedDelay(this::takeAction, 1000, 1, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(this::clearInactive, 60, 15, TimeUnit.MINUTES);
    }

    public static Channel getOfficialChannel() {
        return officialChannel;
    }

    static void setOfficialChannel(Channel officialChannel) {
        SamuraiController.officialChannel = officialChannel;
    }

    void execute(Action action) {
        callsMade.incrementAndGet();
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
        if (action.getClass().isAnnotationPresent(Admin.class) && !action.getAuthor().canInteract(client.getGuildById(String.valueOf(action.getGuildId())).getSelfMember())) {
            Bot.log(String.format("%s does not have adequate privileges to use `%s`", action.getAuthor().getEffectiveName(), action.getClass().getAnnotation(Key.class).value()));
            return false;
        }
        if (action.getClass().isAnnotationPresent(Listener.class))
            action.setListener(listener);
        if (action.getClass().isAnnotationPresent(Client.class)) action.setClient(client);
        if (action.getClass().isAnnotationPresent(Guild.class)) {
            Long guildId = action.getGuildId();
            if (!osuGuildMap.containsKey(guildId))
                osuGuildMap.put(guildId, new SamuraiGuild(listener.getPrefix(guildId), client.getGuildById(String.valueOf(guildId))));
            action.setGuild(osuGuildMap.get(guildId));
        }
        if (action.getClass().isAnnotationPresent(ActionKeySet.class)) {
            action.setKeySet(actionMap.keySet());
        }
        return true;
    }

    void execute(Reaction reaction) {
        System.out.println("Reaction Found: " + reaction.getName());
        DynamicMessage samuraiMessage = messageMap.get(reaction.getMessageId());
        if (samuraiMessage.setValidReaction(reaction)) {
            System.out.println("Executing...");
            if (!reactionQueue.offer(commandPool.submit(samuraiMessage)))
                new RejectedExecutionException("Could not add Action to Queue").printStackTrace();
        }
    }

    private void takeReaction() {
        try {
            final Future<MessageEdit> editFuture = reactionQueue.poll(5, TimeUnit.MILLISECONDS);
            if (editFuture == null)
                return;
            final MessageEdit edit = editFuture.get();
            client.getTextChannelById(String.valueOf(edit.getChannelId())).editMessageById(String.valueOf(edit.getMessageId()), edit.getContent()).queue(edit.getConsumer());
        } catch (InterruptedException | ExecutionException e) {
            Bot.logError(e);
        }
    }

    private void takeAction() {
        try {
            Future<Optional<SamuraiMessage>> smOption = actionQueue.poll(10, TimeUnit.MILLISECONDS);
            if (smOption == null || !smOption.get().isPresent()) return;
            SamuraiMessage samuraiMessage = smOption.get().get();
            if (samuraiMessage instanceof DynamicMessage) {
                DynamicMessage dynamicMessage = (DynamicMessage) samuraiMessage;
                //I might make another message sent queue with .submit();
                Message message = client.getTextChannelById(String.valueOf(dynamicMessage.getChannelId())).sendMessage(dynamicMessage.getMessage()).complete();
                dynamicMessage.setMessageId(Long.parseLong(message.getId()));
                dynamicMessage.getConsumer().accept(message);
                messageMap.putIfAbsent(dynamicMessage.getMessageId(), dynamicMessage);
            } else if (samuraiMessage instanceof FixedMessage) {
                if (((FixedMessage) samuraiMessage).getConsumer().isPresent())
                    client.getTextChannelById(String.valueOf(samuraiMessage.getChannelId())).sendMessage(samuraiMessage.getMessage()).queue(((FixedMessage) samuraiMessage).getConsumer().get());
                else
                    client.getTextChannelById(String.valueOf(samuraiMessage.getChannelId())).sendMessage(samuraiMessage.getMessage()).queue();
            }
        } catch (InterruptedException | ExecutionException e) {
            Bot.logError(e);
        }
    }

    private void clearInactive() {
        messageMap.forEachValue(1000L, value -> {
            if (value.isExpired()) {
                messageMap.remove(value.getMessageId());
            }
        });
    }

    boolean isWatching(long messageId) {
        return messageMap.containsKey(messageId);
    }

    void setJDA(JDA jda) {
        this.client = jda;
    }

    Action getAction(String key) {
        if (actionMap.containsKey(key))
            try {
                return actionMap.get(key).newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                Bot.logError(e);
            }
        return null;
    }

    private void initActions() {
        Reflections reflections = new Reflections("samurai.action");
        Set<Class<? extends Action>> classes = reflections.getSubTypesOf(Action.class);
        for (Class<? extends Action> action : classes) {
            Key actionKey = action.getAnnotation(Key.class);
            if (actionKey == null || Objects.equals(actionKey.value(), "")) {
                System.err.printf("No key found for %s%n", action.getName());
                continue;
            }
            actionMap.put(actionKey.value(), action);
            System.out.printf("%-10s mapped to %s%n", String.format("\"%s\"", actionKey.value()), action.getName());
        }
    }


}
