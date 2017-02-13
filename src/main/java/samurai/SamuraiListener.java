package samurai;

import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import samurai.action.Action;
import samurai.action.generic.HelpAction;
import samurai.data.SamuraiFile;
import samurai.duel.Game;
import samurai.message.SamuraiMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * Listener for SamuraiBot
 * This class sends events to ↓
 *
 * @author TonTL
 * @version 4.0
 * @see SamuraiController
 * @since 2/12/2017.
 */
@SuppressWarnings("Duplicates")
class SamuraiListener extends ListenerAdapter {
    static int messagesSent;
    private final HashMap<Long, String> prefix;
    private final SamuraiController samurai;
    private final OperatingSystemMXBean operatingSystemMXBean;
    private final ExecutorService commandPool;
    private final ConcurrentLinkedQueue<Future<SamuraiMessage>> commandQueue;
    private final ConcurrentHashMap<Long, SamuraiMessage> messageMap;
    private User self;
    private boolean running;

    SamuraiListener(OperatingSystemMXBean operatingSystemMXBean) {
        this.operatingSystemMXBean = operatingSystemMXBean;
        prefix = new HashMap<>();
        samurai = new SamuraiController();
        messagesSent = 0;
        commandPool = Executors.newCachedThreadPool();
        commandQueue = new ConcurrentLinkedQueue<>();
        messageMap = new ConcurrentHashMap<>();
        running = true;
        Executors.newSingleThreadExecutor().execute(() -> {
            while (running) {
                clearQueue();
            }
        });
    }

    private void clearQueue() {
        if (commandQueue.isEmpty()) {
            try {
                commandQueue.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            while (!commandQueue.isEmpty()) {
                try {
                    SamuraiMessage msg = commandQueue.poll().get();
                    if (msg != null) messageMap.put(msg.getMessageId(), msg);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        for (Guild g : event.getJDA().getGuilds()) {
            long guildId = Long.parseLong(g.getId());
            if (!SamuraiFile.hasFile(guildId)) {
                SamuraiFile.writeGuildData(g);
                prefix.put(guildId, "!");
            } else {
                // wait update
                prefix.put(guildId, SamuraiFile.getPrefix(guildId));
            }
        }
        Game.samurai = event.getJDA().getSelfUser();
        self = event.getJDA().getSelfUser();
        System.out.println("Ready!" + prefix.toString());

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor() == self) {
            messagesSent++;
            return;
        }
        if (event.getAuthor().isBot()) return;

        String token = prefix.get(Long.parseLong(event.getGuild().getId()));
        String content = event.getMessage().getRawContent().toLowerCase().trim();

        //if content begins with token ex. "!"
        if (!content.startsWith(token) || content.length() <= token.length() + 3) return;
        else if (content.equalsIgnoreCase("<@270044218167132170>"))
            new HelpAction().setChannel(event.getChannel()).call();

        content = content.substring(token.length());
        String key;
        if (!content.contains(" ")) {
            key = content;
            content = null;
        } else {
            key = content.substring(0, content.indexOf(" "));
            content = content.substring(key.length() + 1);
        }
        Action action = Action.getAction(key);
        if (action == null) return;

        if (content != null) {
            String[] argArray = content.substring(content.indexOf(" ") + 1).split("[ ]+");
            List<String> args = new ArrayList<>();
            for (String argument : argArray) {
                if (!argument.startsWith("<@") && !argument.equals("@everyone") && !argument.equals("@here") && argument.length() != 0)
                    args.add(argument);
            }
            if (!args.isEmpty())
                action.setArgs(args);
        }

        action.setAuthor(event.getMember());
        {
            Message message = event.getMessage();
            List<User> mentionedUsers = message.getMentionedUsers();
            if (!mentionedUsers.isEmpty()) {
                action.setMentions(mentionedUsers);
            }
        }
        action.setGuildId(Long.valueOf(event.getGuild().getId()));
        action.setChannel(event.getChannel());

        commandQueue.add(commandPool.submit(action));
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        running = false;

    }
}
