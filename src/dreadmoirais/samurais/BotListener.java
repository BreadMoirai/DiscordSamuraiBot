package dreadmoirais.samurais;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;


enum game {
    ConnectFour,
    Roshambo
}


/**
 * Created by TonTL on 1/14/2017.
 * Handles events
 * API: http://home.dv8tion.net:8080/job/JDA/Promoted%20Build/javadoc/
 */
public class BotListener extends ListenerAdapter {

    private static final String BOT_ID = "270044218167132170";//18

    private static User self; //bot user

    private static BotData data; //userData

    private static HashMap<String, String> roleResponses;
    private static ArrayList<String> fightWords;
    private static ArrayList<String> shadePhrases;

    private static Random rand;


    private static List<Consumer<MessageReceivedEvent>> basicCommands;
    private static List<Consumer<MessageReceivedEvent>> mentionCommands;

    private static boolean active;
    private static game currentGame;

    /**
     * constructor
     */
    public BotListener() {
        roleResponses = new HashMap<>(); //hashmap for responses to who am i?
        fightWords = new ArrayList<>(); //list of words that will trigger a duel
        shadePhrases = new ArrayList<>(); //What shade will the tree provide when you can have Samurai[Bot]

        //Random object for rolls
        rand = new Random();

        basicCommands = new ArrayList<>();
        basicCommands.add(BotListener::getStat);
        basicCommands.add(BotListener::getIdentity);
        basicCommands.add(BotListener::getRoll);
        basicCommands.add(BotListener::exitProtocol);
        mentionCommands = new ArrayList<>();
        mentionCommands.add(BotListener::getFlame);
        mentionCommands.add(BotListener::getFile);



        active = false;
        currentGame = null;
    }

    @Override
    public void onReady(ReadyEvent event) {
        //fired when the connection is established

        loadKeywords(); //loads phrases from keywords.txt

        self = event.getJDA().getSelfUser();
        data = new BotData(event.getJDA().getGuilds().get(0).getMembers());

    }


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        System.out.println(event.getReaction().getEmote().getName());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //fires when a message is sent in the channel
        if (event.isFromType(ChannelType.TEXT)) {
            if (event.getMessage().getMentionedUsers().contains(self)) {
                for (Consumer<MessageReceivedEvent> command : mentionCommands)
                    command.accept(event);
            } else {
                for (Consumer<MessageReceivedEvent> command : basicCommands)
                    command.accept(event);
            }
        }
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        super.onDisconnect(event);
        data.saveDataFull();
    }

    //Basic Commands
    private static void getStat(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().contains("!stat")) {
            if (event.getMessage().getMentionedUsers().size() == 0) {
                event.getChannel().sendMessage(data.users.get(event.getAuthor().getId()).buildEmbed()).queue();
            } else {
                for (User u : event.getMessage().getMentionedUsers()) {
                    event.getChannel().sendMessage(data.users.get(u.getId()).buildEmbed()).queue();
                }
            }
        }
    }

    private static void getIdentity(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().contains(("who am i?"))) {
            event.getChannel().sendMessage(new MessageBuilder()
                    .append(event.getAuthor().getAsMention())
                    .append(" ").append(roleResponses.get(event.getMember().getRoles().toString()))
                    .build()).queue();
        }
    }

    private static void getRoll(MessageReceivedEvent event) {
        String message = event.getMessage().getContent().toLowerCase();
        if (message.contains("!roll")) {
            int x = 1;
            if (message.length() < 7) {
                x += rand.nextInt(100);
            } else {
                try {
                    x += rand.nextInt(Integer.parseInt(message.trim().substring(6)));
                } catch (NumberFormatException e) {
                    x += rand.nextInt(100);
                    event.getMessage().addReaction("\uD83D\uDE15").queue();
                }
            }
            event.getChannel().sendMessage(new MessageBuilder()
                    .append(event.getAuthor().getAsMention())
                    .append(" rolled ")
                    .append(x)
                    .build()).queue();
        }
    }

    private static void exitProtocol(MessageReceivedEvent event) {
        if (event.getMessage().getContent().equalsIgnoreCase("!shutdown")) {
            event.getChannel().sendMessage("sad boop").queue();
            data.saveDataFull();
            event.getJDA().shutdown();
        }
    }

    //MentionCommands

    private static void getFlame(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().contains("flame")) {
            int upperBound = shadePhrases.size();
            List<User> victims = new ArrayList<>(event.getMessage().getMentionedUsers());
            for (Role r : event.getMessage().getMentionedRoles()) {
                event.getGuild().getMembers().forEach(member -> {
                    if (member.getRoles().contains(r)) {
                        victims.add(member.getUser());
                    }
                });
            }
            if (victims.isEmpty()) {
                event.getMessage().addReaction("\uD83D\uDE15").queue();
            } else {
                for (User victim : victims) {
                    if (!victim.equals(self)) {
                        MessageBuilder messageBuilder = new MessageBuilder();
                        int x = rand.nextInt(upperBound);
                        messageBuilder.append(shadePhrases.get(x))
                                .replaceAll("[victim]", victim.getAsMention());
                        if (x == 0) {
                            messageBuilder.append(victim.getName().substring(victim.getName().length() / 2));
                            event.getChannel().sendMessage(messageBuilder.build()).queue();
                        } else if (x == 1) {
                            messageBuilder.setTTS(true);
                            event.getChannel().sendMessage(messageBuilder.build()).queue(message -> message.editMessage(victim.getAsMention()).queue());
                        } else {
                            event.getChannel().sendMessage(messageBuilder.build()).queue();
                        }
                        data.addFlame(victim.getId());
                    }
                }
            }

        }
    }


    private static void getFile(MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (attachments.size() > 0) {
            System.out.println("\nFound Attachment.");
            for (Message.Attachment a : attachments) {
                event.getChannel().sendMessage(String.format("Attachment Found.\n%s :%s bytes\n%s", a.getFileName(), a.getSize(), event.getMessage().getMentionedUsers().get(0).getName())).queue();
            }
        }
    }


    /**
     * private boolean duel(MessageReceivedEvent event) {
     * String message = event.getMessage().getRawContent();
     * List<User> mentions = event.getMessage().getMentionedUsers();
     * for (String key : fightWords) {
     * if (message.contains(key)) {
     * active = true;
     * int a = rand.nextInt(1);
     * <p>
     * switch(a) {
     * case 0:
     * currentGame = game.Guess;
     * return true;
     * default:
     * return false;
     * }
     * }
     * }
     * return false;
     * }
     **/


    //miscMethods
    private void loadKeywords() {
        try (BufferedReader br = new BufferedReader(new FileReader("src\\dreadmoirais\\data\\keywords.txt"))) {
            String line = br.readLine();
            System.out.println("Reading " + line);
            while (!(line = br.readLine()).equals("")) {
                roleResponses.put(line, br.readLine());
            }
            line = br.readLine();
            System.out.println("Reading " + line);
            while (!(line = br.readLine()).equals("")) {
                fightWords.add(line);
            }
            line = br.readLine();
            System.out.println("Reading " + line);
            while ((line = br.readLine()) != null) {
                shadePhrases.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
