package samurai;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import samurai.duel.ConnectFour;
import samurai.duel.Game;
import samurai.osu.OsuData;
import samurai.osu.OsuJsonReader;


import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    private static HashMap<String, Game> games;

    private static List<Consumer<MessageReceivedEvent>> commands;
    private static List<String> keys;

    private static OsuData osuData;
    private static Set<String> osuMessages;

    /**
     * constructor
     */
    BotListener() {
        shadePhrases = new ArrayList<>(); //What shade will the tree provide when you can have Samurai[samurai.Bot]

        //Random object for rolls
        rand = new Random();
        games = new HashMap<>();

        osuMessages = new HashSet<>();
        osuData = new OsuData();
        osuMessages = new HashSet<>();

        keys = new ArrayList<>();
        commands = new ArrayList<>();

        commands.add(BotListener::getStat);
        keys.add("!stat");
        commands.add(BotListener::getRoll);
        keys.add("!roll");
        commands.add(BotListener::startDuel);
        keys.add("!samurai.duel");
        commands.add(BotListener::getFlame);
        keys.add("!flame");
        commands.add(BotListener::getOsuUser);
        keys.add("!samurai.osu");
        commands.add(BotListener::getOsuData);
        keys.add("!build");
        commands.add(BotListener::getBeatmap);
        keys.add("!beatmap");
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
        samurai.duel.Game.samurai = self;
        data = new BotData(event.getJDA().getGuilds().get(0).getMembers());
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT) && event.getAuthor()!=self) {
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
        if (event.getAuthor()!=self)
        event.getChannel().sendMessage("Ready for some nudes?!").queue();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (games.containsKey(event.getMessageId())) {
            updateGames(event);
        }
    }

    @Override
    public void onGenericMessageReaction(GenericMessageReactionEvent event) {
        if (osuMessages.contains(event.getMessageId())) {
            updateOsuMessage(event);
        }
    }


    @Override
    public void onDisconnect(DisconnectEvent event) {
        data.saveDataFull();
    }

    @Override
    public void onGenericEvent(Event event) {
        //System.out.println(event.getClass());
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
            games.put(game.message.getId(), game);
        }
    }

    private void updateGames(MessageReactionAddEvent event) {
        Game game = games.get(event.getMessageId());
        int x = game.getReactions().indexOf(event.getReaction().getEmote().getName());
        if (x != -1) {
            if (game.isPlayer(event.getUser())) {
                game.perform(x, event.getUser());

                if (game.hasEnded()) {
                    games.remove(game.message.getId());
                    for (MessageReaction messageReaction : event.getChannel().getMessageById(event.getMessageId()).complete().getReactions()) {
                        if (game.getReactions().contains(messageReaction.getEmote().getName())) {
                            messageReaction.removeReaction().queue();
                        }
                    }
                }
                game.message.editMessage(game.buildBoard()).queue();
                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }

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


    private static void getOsuUser(MessageReceivedEvent event) {
        Message userInfo = OsuJsonReader.getUserInfo(event.getMessage().getRawContent().substring(5));
        if (userInfo == null) {
            event.getMessage().addReaction("\u274c").queue();
        } else {
            event.getChannel().sendMessage(userInfo).queue();
        }

    }

    private static void getOsuData(MessageReceivedEvent event) {
        boolean readAll;
        readAll = !event.getMessage().getRawContent().toLowerCase().contains("partial");
        if (osuData.readOsuDB("src\\dreadmoirais\\data\\samurai.osu!.db", readAll)) {
            event.getMessage().addReaction(readAll ? "\u2705" : "\u2611").queue();
        } else {
            event.getMessage().addReaction(event.getGuild().getEmotesByName("hit_miss", false).get(0)).queue();
        }
    }

    private static void getBeatmap(MessageReceivedEvent event) {
        if (osuData == null) {
            event.getMessage().addReaction(event.getGuild().getEmotesByName("hit_miss", false).get(0)).queue();
            return;
        }
        if (event.getMessage().getRawContent().toLowerCase().contains("all")) {
            osuData.getAllBeatmaps().forEach(message -> event.getChannel().sendMessage(message).queue());
        } else {
            event.getChannel().sendMessage("Getting Beatmap...").queue(message -> {
                message.addReaction("\uD83D\uDDFA").queue();
                message.addReaction("\uD83D\uDC65").queue(success1 -> message.editMessage(osuData.getBeatmap(rand)).queue(success2 -> osuMessages.add(message.getId())));
            });
        }
    }


    private void updateOsuMessage(GenericMessageReactionEvent event) {
        if (event.getReaction().getEmote().getName().equals("\uD83D\uDDFA") || event.getReaction().getEmote().getName().equals("\uD83D\uDC65")) {

            boolean fullMap = false, fullScore = false;

            List<MessageReaction> reactions = event.getChannel().getMessageById(event.getMessageId()).complete().getReactions();
            for (MessageReaction messageReaction : reactions) {

                if (messageReaction.getEmote().getName().equals("\uD83D\uDDFA")) {//map
                    if (messageReaction.getCount() == 1) {
                        fullMap = false;
                    } else if (messageReaction.getCount() > 1) {
                        fullMap = true;
                    }

                } else if (messageReaction.getEmote().getName().equals("\uD83D\uDC65")) {
                    if (messageReaction.getCount() == 1) {
                        fullScore = false;
                    } else if (messageReaction.getCount() > 1) {
                        fullScore = true;
                    }

                }
            }
            String hash = event.getChannel().getMessageById(event.getMessageId()).complete().getEmbeds().get(0).getFooter().getText();
            event.getChannel().editMessageById(event.getMessageId(), osuData.buildBeatmapInfo(hash, fullScore, fullMap)).queue();
        }
    }


    /**
     *
     */
    private static void getFile(MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (!attachments.isEmpty() && attachments.get(0).getFileName().equals("osu.db")) {
            if (osuData == null) {
                event.getMessage().addReaction(event.getGuild().getEmotesByName("hit_miss", false).get(0)).queue();
                return;
            }
            // fixme use with resources
            System.out.println("\nFound Attachment.");
            String path = "src\\dreadmoirais\\data\\osu\\";
            path += event.getMessage().getAuthor().getName() + event.getMessage().getCreationTime().format(DateTimeFormatter.ofPattern("MMMdd_HH-mm-ss")) + ".db";
            path = path.replaceAll(" ", "");
            attachments.get(0).download(new File(path));
            //event.getMessage().addReaction("\u2705").queue();
            event.getChannel().sendMessage(String.format("Scores Found   `%d`", osuData.readScoresDB(path))).queue();
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
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("keywords.txt"))))  {
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
