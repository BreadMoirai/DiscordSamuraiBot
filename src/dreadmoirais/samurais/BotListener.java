package dreadmoirais.samurais;

import dreadmoirais.samurais.duel.ConnectFour;
import dreadmoirais.samurais.duel.Game;
import dreadmoirais.samurais.osu.OsuJsonReader;


import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by TonTL on 1/14/2017.
 * Handles events
 * API: http://home.dv8tion.net:8080/job/JDA/Promoted%20Build/javadoc/
 */
public class BotListener extends ListenerAdapter {

    //private static final String BOT_ID = "270044218167132170";//18

    private static User self; //bot user

    private static BotData data; //userData

    private static ArrayList<String> shadePhrases;

    private static Random rand;

    private static List<dreadmoirais.samurais.duel.Game> games;

    private static List<Consumer<MessageReceivedEvent>> commands;
    private static List<String> keys;

    /**
     * constructor
     */
    public BotListener() {
        shadePhrases = new ArrayList<>(); //What shade will the tree provide when you can have Samurai[Bot]

        //Random object for rolls
        rand = new Random();

        games = new ArrayList<>();

        keys = new ArrayList<>();
        commands = new ArrayList<>();

        commands.add(BotListener::getStat);
        keys.add("!stat");
        commands.add(BotListener::getRoll);
        keys.add("!roll");
        commands.add(BotListener::startDuel);
        keys.add("!duel");
        commands.add(BotListener::getFlame);
        keys.add("!flame");
        commands.add(BotListener::getOsu);
        keys.add("!osu");
        commands.add(BotListener::getFile);
        keys.add("!upload");
        commands.add(BotListener::saveFull);
        keys.add("!save");
        commands.add(BotListener::exitProtocol);
        keys.add("!shutdown");
    }


    @Override
    public void onReady(ReadyEvent event) {
        //fired when the connection is established

        loadKeywords(); //loads phrases from keywords.txt

        self = event.getJDA().getSelfUser();
        dreadmoirais.samurais.duel.Game.samurai = self;
        data = new BotData(event.getJDA().getGuilds().get(0).getMembers());
    }


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        System.out.println(event.getReaction().getEmote().getName());
        if (games.size() != 0) {
            updateGames(event);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //fires when a message is sent in the channel
        if (event.isFromType(ChannelType.TEXT)) {
            getCommand(event);
        }
    }

    private void getCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getRawContent().toLowerCase();
        for (int i = 0; i < keys.size(); i++) {
            if (message.contains(keys.get(i)))
                commands.get(i).accept(event);
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {

    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        data.saveDataFull();
    }


    //Basic Commands
    private static void getStat(MessageReceivedEvent event) {
        if (event.getMessage().getMentionedUsers().size() == 0) {
            event.getChannel().sendMessage(data.users.get(event.getAuthor().getId()).buildEmbed()).queue();
        } else {
            for (User u : event.getMessage().getMentionedUsers()) {
                event.getChannel().sendMessage(data.users.get(u.getId()).buildEmbed()).queue();
            }
        }
    }


    private static void getRoll(MessageReceivedEvent event) {
        String message = event.getMessage().getRawContent().toLowerCase();

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
        event.getChannel().sendMessage(
                new MessageBuilder()
                        .append(event.getAuthor().getAsMention())
                        .append(" rolled ")
                        .append(x)
                        .build())
                .queue();

    }


    private static void getFlame(MessageReceivedEvent event) {
        List<User> victims = new ArrayList<>(event.getMessage().getMentionedUsers());

        if (event.getMessage().mentionsEveryone())
            for (Member m : event.getGuild().getMembers())
                victims.add(m.getUser());
        else
            for (Role r : event.getMessage().getMentionedRoles())
                for (Member m : event.getGuild().getMembers())
                    if (m.getRoles().contains(r))
                        victims.add(m.getUser());


        if (victims.isEmpty()) {
            event.getMessage().addReaction("\uD83D\uDE15").queue();
        } else {
            for (User victim : victims) {
                if (!victim.equals(self)) {
                    MessageBuilder messageBuilder = new MessageBuilder();
                    int x = rand.nextInt(shadePhrases.size());
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
                    data.incrementStat(victim.getId(), "Times Flamed");
                }
            }


        }
    }


    private static void getOsu(MessageReceivedEvent event) {
        Message userInfo = OsuJsonReader.getUserInfo(event.getMessage().getRawContent().substring(5));
        if (userInfo==null) {
            event.getMessage().addReaction("\u274c").queue();
        } else {
            event.getChannel().sendMessage(userInfo).queue();
        }

    }


    private static void startDuel(MessageReceivedEvent event) {
        if (event.getMessage().getMentionedUsers().size() == 1) {
            Game game = new ConnectFour(event.getAuthor(), event.getMessage().getMentionedUsers().get(0), rand.nextBoolean());

            game.setData(data.users);
            game.message = event.getChannel().sendMessage(game.buildTitle().build()).complete();

            for (String reaction : game.getReactions()) {
                if (reaction.equals("8\u20e3")) {
                    game.message.addReaction(reaction).queue(success -> game.message.editMessage(game.buildBoard()).queue());
                } else {
                    game.message.addReaction(reaction).queue();
                }
            }
            games.add(game);
        }
    }

    private void updateGames(MessageReactionAddEvent event) {
        for (Game game : games) {
            int x = game.getReactions().indexOf(event.getReaction().getEmote().getName());
            if (x != -1) {
                if (game.message.getId().equals(event.getMessageId()) && game.isPlayer(event.getUser())) {
                    game.perform(x, event.getUser());
                    if (game.hasEnded()) {
                        games.remove(game);
                        for (MessageReaction messageReaction : event.getChannel().getMessageById(event.getMessageId()).complete().getReactions()) {
                            if (game.getReactions().contains(messageReaction.getEmote().getName())) {
                                messageReaction.removeReaction().queue();
                            }
                        }
                    }
                    game.message.editMessage(game.buildBoard()).queue();
                    event.getReaction().removeReaction(event.getUser()).queue();
                    break;
                }

            }
        }
    }


    /**
     * INCOMPLETE
     */
    private static void getFile(MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (!attachments.isEmpty()) {
            System.out.println("\nFound Attachment.");
            String path = "src\\dreadmoirais\\data\\scores\\";
            if (attachments.get(0).getFileName().equals("scores.db")) {
                path += event.getAuthor().getId();
            }
            attachments.get(0).download(new File(path));
            event.getMessage().addReaction("\u2705").queue();
        } else {
            event.getMessage().addReaction("\uD83D\uDE12").queue();
        }
    }

    private static void saveFull(MessageReceivedEvent event) {
        data.saveDataFull();
        event.getMessage().addReaction("\u2705").queue();
    }

    private static void exitProtocol(MessageReceivedEvent event) {
        data.saveDataFull();
        event.getMessage().addReaction("\uD83D\uDC4B").queue();
        event.getJDA().shutdown();
    }


    //miscMethods
    private void loadKeywords() {
        try (BufferedReader br = new BufferedReader(new FileReader("src\\dreadmoirais\\data\\keywords.txt"))) {
            String line = br.readLine();
            System.out.println("Reading " + line);
            while ((line = br.readLine()) != null) {
                shadePhrases.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
