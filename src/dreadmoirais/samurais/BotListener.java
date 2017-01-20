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
import net.dv8tion.jda.core.requests.RestAction;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
<<<<<<< HEAD
import java.util.function.Consumer;
=======

enum game {
    Guess,
    Roshambo
}
>>>>>>> origin/master

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

<<<<<<< HEAD
    private static List<Consumer<MessageReceivedEvent>> basicCommands;
    private static List<Consumer<MessageReceivedEvent>> mentionCommands;
=======
    private static boolean active;
    private static game currentGame;

>>>>>>> origin/master

    /**
     * constructor
     */
    public BotListener() {
        roleResponses = new HashMap<>(); //hashmap for responses to who am i?
        fightWords = new ArrayList<>(); //list of words that will trigger a duel
        shadePhrases = new ArrayList<>(); //What shade will the tree provide when you can have Samurai[Bot]

        //Random object for rolls
        rand = new Random();
<<<<<<< HEAD
        basicCommands = new ArrayList<>();
        basicCommands.add(BotListener::getStat);
        basicCommands.add(BotListener::getIdentity);
        basicCommands.add(BotListener::getRoll);
        basicCommands.add(BotListener::exitProtocol);
        mentionCommands = new ArrayList<>();
        mentionCommands.add(BotListener::getFlame);


=======
        active = false;
        currentGame = null;
>>>>>>> origin/master
    }

    @Override
    public void onReady(ReadyEvent event) {
        //fired when the connection is established

        loadKeywords(); //loads phrases from keywords.txt

        self = event.getJDA().getSelfUser();
        data = new BotData(event.getJDA().getGuilds().get(0).getMembers());

    }


<<<<<<< HEAD
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

    //Basic Commands
    private static void getStat(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().contains("!stat")) {
            if (event.getMessage().getMentionedUsers().size() == 0) {
                event.getChannel().sendMessage(new InfoPanel(data.users.get(event.getAuthor().getId()))).queue();
            } else {
                for (User u : event.getMessage().getMentionedUsers()) {
                    event.getChannel().sendMessage(new InfoPanel(data.users.get(u.getId()))).queue();
                }
=======
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //fires when a message is sent in the channel

        if (event.isFromType(ChannelType.PRIVATE)) {  //If this message was not sent to a Guild TextChannel
            event.getChannel().sendMessage("Boi why you dm me? Get the fuck outta here.");
        }

        if (active) {
            switch(currentGame) {
                case Guess:
                    break;
                case Roshambo:
                    break;
            }

        }
        if (event.getMessage().getMentionedUsers().contains(self)) {
            flame(event);
            //supports flame

        } else if (filetransfer(event)) {
            //supports file

        } else if (simpleResponse(event)) {
            //!stat
            //who am i?
            //!roll
        } else if (event.getMessage().getMentionedUsers().size()==1) {
            duel(event);

        } else if (exitProtocol(event)) {
            //!shutdown
            //!kys
        }

    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        super.onDisconnect(event);
        data.saveDataFull();
    }

    private boolean flame(MessageReceivedEvent event) {//checks for @samurai
        List<User> mentions = event.getMessage().getMentionedUsers();

        if (event.getMessage().getRawContent().toLowerCase().contains("flame") && mentions.size() == 2) {
            int killNumber = rand.nextInt(shadePhrases.size());
            String killPhrase = shadePhrases.get(killNumber);
            User target = mentions.get((1 - mentions.indexOf(event.getJDA().getSelfUser())));
            MessageBuilder mb = new MessageBuilder()
                    .append(killPhrase)
                    .replaceAll("[victim]", target.getAsMention());
            if (killNumber == 0) {//"[Member] more like stupid[(us)er]
                mb.append(target.getName().substring(target.getName().length() / 2));
            }
            if (killNumber == 1) {
                mb.setTTS(true);
                event.getChannel().sendMessage(mb.build()).queue( message -> {
                    message.editMessage(target.getAsMention()).queue();
                });

            } else {
                event.getChannel().sendMessage(mb.build()).queue();
>>>>>>> origin/master
            }

            data.addFlame(target.getId());
            return true;
        }
    }

<<<<<<< HEAD
    private static void getIdentity(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().contains(("who am i?"))) {
            event.getChannel().sendMessage(new MessageBuilder()
                    .append(event.getAuthor().getAsMention())
                    .append(" ").append(roleResponses.get(event.getMember().getRoles().toString()))
                    .build()).queue();
=======
    private boolean filetransfer(MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (attachments.size() > 0) {
            System.out.println("\nFound Attachment.");
            for (Message.Attachment a : attachments) {
                event.getChannel().sendMessage(String.format("Attachment Found.\n%s :%s bytes\n%s", a.getFileName(), a.getSize(), event.getMessage().getMentionedUsers().get(0).getName())).queue();
            }
            return true;
>>>>>>> origin/master
        }
    }

<<<<<<< HEAD
    private static void getRoll(MessageReceivedEvent event) {
        String message = event.getMessage().getContent().toLowerCase();
        if (message.contains("!roll")) {
            int x = 1;
            if (message.length() < 7) {
                x += rand.nextInt(100);
            } else {
=======
    private boolean simpleResponse(MessageReceivedEvent event) {//basic responses
        String messageReceived = event.getMessage().getRawContent().toLowerCase();
        String messageSent = "";
        if (messageReceived.contains("!stat")) {
            getStat(event);
        } else if (messageReceived.equals("who am i?")) {//who am i? response
            messageSent = " " + roleResponses.get(event.getMember().getRoles().toString());
        } else if (messageReceived.contains("!roll")) {//!roll response
            if (messageReceived.length() > 6) {
>>>>>>> origin/master
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

<<<<<<< HEAD
    //MentionCommands
    private static void getFlame(MessageReceivedEvent event) {
        if (event.getMessage().getContent().toLowerCase().contains("flame")) {
            int upperBound = shadePhrases.size();
            MessageBuilder mb = new MessageBuilder();
            List<User> victims = new ArrayList<User>(event.getMessage().getMentionedUsers());
            for (Role r : event.getMessage().getMentionedRoles()) {
                event.getGuild().getMembers().stream().forEach(member -> {
                    if (member.getRoles().contains(r)) {
                        victims.add(member.getUser());
                    }
                });
            }
            for (User victim: victims) {
                if (!victim.equals(self)) {
                    mb.append(" ")
                            .append(shadePhrases.get(rand.nextInt(upperBound)))
                            .append("\n")
                            .replaceAll("[victim]", victim.getAsMention());
                    data.addFlame(victim.getId());
                }
            }
            if (mb.isEmpty()) {
                event.getMessage().addReaction("\uD83D\uDE15").queue();
            } else {
                event.getChannel().sendMessage(mb.build()).queue();
=======
    private void getStat(MessageReceivedEvent event) {
        if (event.getMessage().getMentionedUsers().size() == 0) {
            event.getChannel().sendMessage(data.users.get(event.getAuthor().getId()).buildEmbed()).queue();
        } else {
            for (User u : event.getMessage().getMentionedUsers()) {
                event.getChannel().sendMessage(data.users.get(u.getId()).buildEmbed()).queue();
>>>>>>> origin/master
            }
        }
    }

<<<<<<< HEAD
    private boolean filetransfer(MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (attachments.size() > 0) {
            System.out.println("\nFound Attachment.");
            for (Message.Attachment a : attachments) {
                event.getChannel().sendMessage(String.format("Attachment Found.\n%s :%s bytes\n%s", a.getFileName(), a.getSize(), event.getMessage().getMentionedUsers().get(0).getName())).queue();
            }
=======
    private boolean exitProtocol(MessageReceivedEvent event) {
        boolean shutdown = false;
        if (event.getMessage().getRawContent().equalsIgnoreCase("!kys")) {
            event.getChannel().sendMessage("ok :(").queue();
            shutdown = true;
        } else if (event.getMessage().getRawContent().equalsIgnoreCase("!shutdown")) {
            event.getChannel().sendMessage("sad boop").queue();
            shutdown = true;
        }
        if (shutdown) {
            //save data?
            data.saveDataFull();
            event.getJDA().shutdown();
>>>>>>> origin/master
            return true;
        }

        return false;
    }


    private boolean duel(MessageReceivedEvent event) {
        String message = event.getMessage().getRawContent();
<<<<<<< HEAD
        List<User> mentions = event.getMessage().getMentionedUsers();
        if (event.getMessage().getRawContent() == null)
            return false;
=======

        for (String key : fightWords) {
            if (message.contains(key)) {
                active = true;
                int a = rand.nextInt(1);

                switch(a) {
                    case 0:
                        currentGame = game.Guess;
                        return true;
                    default:
                        return false;
                }
            }
        }
>>>>>>> origin/master
        return false;
    }

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
