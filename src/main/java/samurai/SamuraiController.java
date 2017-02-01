package samurai;

import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import org.json.JSONObject;
import samurai.data.SamuraiFile;
import samurai.duel.ConnectFour;
import samurai.duel.Game;
import samurai.osu.OsuJsonReader;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;

/**
 * Created by TonTL on 1/28/2017.
 * does work
 */
class SamuraiController {

    private static final String AVATAR = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";
    private static final String githubCommitApi = "https://api.github.com/repos/DreadMoirai/DiscordSamuraiBot/git/commits/", majorSha = "b742da9e2a3a48a0edebe1ba221b298de650db5e";

    OperatingSystemMXBean operatingSystemMXBean;

    private long initializationTime;
    private EventListener listener;
    private Random random;
    private HashMap<Long, Game> gameMap;

    private long callsMade;

    SamuraiController(EventListener eventListener) {
        callsMade = 0;
        initializationTime = System.currentTimeMillis();
        listener = eventListener;
        random = new Random();
        gameMap = new HashMap<>();
    }


    void action(String key, MessageReceivedEvent event, String... args) {
        callsMade += 1;
        boolean success = true;
        switch (key) {
            case "status":
                event.getChannel().sendMessage(buildStatus(event, args)).queue(message -> message.editMessage(new MessageBuilder().setEmbed(new EmbedBuilder(message.getEmbeds().get(0)).setTimestamp(message.getCreationTime()).build()).build()).queue());
                break;
            case "duel":
                sendChallenge(event);
                break;
            case "uptime":
                event.getChannel().sendMessage("Samurai has been online for " + getActiveTime()).queue();
                break;
            case "stat":
                for (Message message : buildUserStats(event, event.getMessage().getMentionedUsers())) {
                    event.getChannel().sendMessage(message).queue();
                }
                break;
            case "setosu":
                event.getMessage().addReaction(setOsu(event, args) ? "✅" : "❌").queue();
                break;
            case "getosu":
                if (args.length == 1) {
                    MessageEmbed osuUserInfo = OsuJsonReader.getUserInfo(args[0]);
                    if (osuUserInfo != null)
                        event.getChannel().sendMessage(osuUserInfo).queue();
                    else
                        event.getMessage().addReaction("❌").queue();
                }
                break;
            case "prefix":
                if (validatePrefix(args)) {
                    SamuraiFile.setPrefix(Long.parseLong(event.getGuild().getId()), args[0]);
                    listener.updatePrefix(Long.parseLong(event.getGuild().getId()), args[0]);
                    event.getMessage().addReaction("✅").queue();
                } else {
                    event.getMessage().addReaction("❌").queue();
                }
                break;
            case "help":
                // wait
                event.getChannel().sendMessage(new SamuraiBuilder("help.txt", SamuraiFile.getPrefix(Long.parseLong(event.getGuild().getId()))).build()).queue();
                break;
            case "increment":
                if (validateAdmin(event) && validateIncrement(event.getMessage().getMentionedUsers(), args))
                    incrementUserData(event, args);
                break;
            case "reset":
                if (validateAdmin(event))
                    for (Guild guild : event.getJDA().getGuilds())
                        SamuraiFile.writeGuild(guild);
                break;
            case "todo":
                if (args.length == 0)
                    event.getChannel().sendMessage(new SamuraiBuilder("todo.txt", null).build()).queue();
                else {
                    SamuraiFile.addTodo(args);
                    event.getMessage().addReaction("✅").queue();
                }
                break;
            case "changelog":
            case "update":
                getCommitJson(event.getChannel());
                break;
            case "shutdown":
                event.getJDA().shutdown();
                break;
            default:
                success = false;
        }
        if (success)
            SamuraiFile.modifyUserData(Long.parseLong(event.getGuild().getId()), Long.parseLong(event.getAuthor().getId()), false, 1, "commands used");
    }

    private boolean setOsu(MessageReceivedEvent event, String[] args) {
        if (args.length != 1) {
            return false;
        }
        MessageEmbed osuUserInfo = OsuJsonReader.getUserInfo(args[0]);
        if (osuUserInfo != null) {
            SamuraiFile.modifyUserData(Long.parseLong(event.getGuild().getId()), Long.parseLong(event.getAuthor().getId()), true, Integer.parseInt(osuUserInfo.getFooter().getText()), "osu id");
            event.getChannel().sendMessage(new MessageBuilder()
                    .append("Successfully linked ")
                    .append(event.getAuthor().getAsMention())
                    .append(" to osu account")
                    .setEmbed(osuUserInfo)
                    .build()).queue();
            return true;
        } else {
            return false;
        }
    }

    private void sendChallenge(MessageReceivedEvent event) {
        if (event.getMessage().getMentionedUsers().size() == 0) {
            event.getChannel().sendMessage("Who is willing to engage " + event.getAuthor().getAsMention() + " in a battle of life and death?").queue(success -> {
                // wait
                listener.addGame(Long.parseLong(success.getId()));
                success.addReaction("⚔").queue();
            });
        } else if (event.getMessage().getMentionedUsers().size() == 1) {
            event.getChannel().sendMessage("Creating game...").queue(message -> {
                List<String> connectfour_reactions = ConnectFour.CONNECTFOUR_REACTIONS;
                for (int i = 0, connectfour_reactionsSize = connectfour_reactions.size(); i < connectfour_reactionsSize; i++) {
                    if (i != 7)
                        message.addReaction(connectfour_reactions.get(i)).queue();
                    else
                        message.addReaction(connectfour_reactions.get(i)).queue(success -> {
                            // wait
                            gameMap.put(Long.parseLong(message.getId()), new ConnectFour(event.getAuthor(), event.getMessage().getMentionedUsers().get(0), random.nextBoolean()));
                            listener.addGame(Long.parseLong(message.getId()));
                            message.editMessage(gameMap.get(Long.parseLong(message.getId())).buildBoard()).queue();
                        });
                }
            });
        } else {
            event.getMessage().addReaction("❌").queue();
        }
    }

    void updateGame(MessageReactionAddEvent event, Long gameId) {
        List<String> connectfour_reactions = ConnectFour.CONNECTFOUR_REACTIONS;
        String emoji = event.getReaction().getEmote().getName();
        if (emoji.equals("⚔") && !gameMap.keySet().contains(gameId)) {
            //wait
            event.getChannel().getMessageById(String.valueOf(gameId)).queue(message -> {
                message.clearReactions().queue(done -> {
                    message.editMessage("Creating " + message.getMentionedUsers().get(0).getAsMention() + "'s game...").queue();
                    for (int i = 0, connectfour_reactionsSize = connectfour_reactions.size(); i < connectfour_reactionsSize; i++) {
                        if (i != connectfour_reactionsSize - 1)
                            message.addReaction(connectfour_reactions.get(i)).queue();
                        else
                            message.addReaction(connectfour_reactions.get(i)).queue(success -> {
                                gameMap.put(gameId, new ConnectFour(message.getMentionedUsers().get(0), event.getUser(), random.nextBoolean()));
                                message.editMessage(gameMap.get(gameId).buildBoard()).queue();
                            });
                    }
                });
            });
        } else if (connectfour_reactions.contains(emoji)) {
            Game game = gameMap.get(gameId);
            if (game.isNext(event.getUser())) {
                Message message = event.getChannel().getMessageById(String.valueOf(gameId)).complete();
                game.perform(connectfour_reactions.indexOf(emoji), event.getUser());
                event.getReaction().removeReaction(event.getUser()).queue();
                message.editMessage(game.buildBoard()).queue();

                if (game.hasEnded()) {
                    message.editMessage(game.buildBoard()).queue();
                    gameMap.remove(gameId);
                    listener.removeGame(gameId);
                    User winner = game.getWinner();
                    if (!winner.isBot()) {
                        // wait
                        SamuraiFile.modifyUserData(Long.parseLong(message.getGuild().getId()), Long.parseLong(winner.getId()), false, 1, "duels fought", "duels won");
                        for (User loser : game.getLosers()) {
                            SamuraiFile.modifyUserData(Long.parseLong(message.getGuild().getId()), Long.parseLong(loser.getId()), false, 1, "duels fought");
                        }
                    }
                }
            }

        }
    }

    private void incrementUserData(MessageReceivedEvent event, String[] args) {
        List<User> mentionedUsers = event.getMessage().getMentionedUsers();
        if (mentionedUsers.size() == 0) {
            SamuraiFile.modifyUserData(Long.parseLong(event.getGuild().getId()), Long.parseLong(event.getAuthor().getId()), false, Integer.parseInt(args[0]), Arrays.copyOfRange(args, 1, args.length));
            return;
        }
        for (User u : mentionedUsers) {
            try {
                SamuraiFile.modifyUserData(Long.parseLong(event.getGuild().getId()), Long.parseLong(u.getId()), false, Integer.parseInt(args[0]), Arrays.copyOfRange(args, 1, args.length));
            } catch (IllegalArgumentException e) {
                event.getMessage().addReaction("❌").queue();
                break;
            }
        }
        event.getMessage().addReaction("✅").queue();
    }


    private boolean validateAdmin(MessageReceivedEvent event) {
        return event.getAuthor().getId().equalsIgnoreCase("232703415048732672");
    }

    private boolean validateIncrement(List<User> mentions, String[] args) {
        if (mentions == null || args.length < 2)
            return false;
        try {
            Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return false;
        }
        List<String> dataNames = SamuraiFile.getDataNames();
        for (int i = 1; i < args.length; i++) {
            args[i] = args[i].replace('_', ' ');
            if (!dataNames.contains(args[i]))
                return false;
        }
        return true;
    }

    private boolean validatePrefix(String[] args) {
        return args != null && args.length == 1 && args[0].length() <= 4;
    }

    private String getActiveTime() {
        long timeDifference = System.currentTimeMillis() - initializationTime;
        int seconds = (int) ((timeDifference / 1000) % 60);
        int minutes = (int) ((timeDifference / 60000) % 60);
        int hours = (int) ((timeDifference / 3600000) % 24);
        int days = (int) (timeDifference / 86400000);
        if (days > 0) {
            return String.format("%d days, %d hours, %d minutes, and %d seconds.", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%d hours, %d minutes, and %d seconds.", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d minutes, and %d seconds.", minutes, seconds);
        } else {
            return String.format("%d seconds.", seconds);
        }
    }

    private Message buildStatus(MessageReceivedEvent event, String[] args) {
        SamuraiBuilder sb = new SamuraiBuilder(event.getJDA());
        return new MessageBuilder().setEmbed(sb.build()).build();
    }

    private List<Message> buildUserStats(MessageReceivedEvent event, List<User> mentions) {
        List<Message> userStats = new LinkedList<>();
        SamuraiBuilder samuraiBuilder = new SamuraiBuilder(event.getMember());
        MessageBuilder messageBuilder = new MessageBuilder();
        if (mentions.size() > 0) {
            for (User user : mentions) {
                samuraiBuilder = new SamuraiBuilder(event.getGuild().getMember(user));
                userStats.add(messageBuilder.setEmbed(samuraiBuilder.build()).build());
            }
        } else {
            userStats.add(messageBuilder.setEmbed(samuraiBuilder.build()).build());
        }
        return userStats;
    }

    private void getCommitJson(MessageChannel channel) {
        try (InputStream is = new URL(githubCommitApi + majorSha).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            JSONObject json = new JSONObject(sb.toString());
            channel.sendMessage(new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                            .setTitle("Changelog: " + json.getJSONObject("author").getString("date").substring(0, 10))
                            .setDescription(json.getString("message"))
                            .setUrl(json.getString("html_url"))
                            .setFooter("Committed by " + json.getJSONObject("author").getString("name"), null)
                            .build())
                    .build()).queue();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class SamuraiBuilder extends EmbedBuilder {

        SamuraiBuilder(String file, String prefix) {
            setAuthor("Samurai - " + file, null, AVATAR);
            List<String> textFile = SamuraiFile.readTextFile(file);
            StringBuilder stringBuilder = new StringBuilder();
            for (String line : textFile) {
                if (prefix != null)
                    stringBuilder.append(line.replaceAll("[prefix]", prefix)).append("\n");
                else
                    stringBuilder.append(line).append("\n");
            }
            setDescription(stringBuilder.toString());
        }

        SamuraiBuilder(JDA jda) {
            Runtime thisInstance = Runtime.getRuntime();
            int mb = 1024 * 1024;
            setTitle("Status: " + jda.getPresence().getStatus().name());
            setColor(Color.GREEN);
            setFooter("Samurai\u2122", AVATAR);
            addField("Guild Count", String.valueOf(jda.getGuilds().size()), true);
            addField("User Count", String.valueOf(jda.getUsers().size() - 1), true);
            addField("Time Active", getActiveTime(), true);
            addField("Messages", String.format("**received:  **%d**\nsent:          **%d\n**cpm:          **%.2f", callsMade, listener.messagesSent, 60.0 * callsMade / ((System.currentTimeMillis() - initializationTime) / 1000)), true);
            addField("CpuLoad", String.format("`%.3f%%`/`%.3f%%`", operatingSystemMXBean.getProcessCpuLoad() * 100.00, operatingSystemMXBean.getSystemCpuLoad() * 100.00), true);
            addField("Memory", String.format("**Used:  **`%d MB`\n**Total: **`%d MB`\n**Max:   **`%d MB`", (thisInstance.totalMemory() - thisInstance.freeMemory()) / mb, thisInstance.totalMemory() / mb, thisInstance.maxMemory() / mb), true);
        }

        SamuraiBuilder(Member member) {
            setAuthor(member.getEffectiveName(), null, member.getUser().getAvatarUrl());
            setColor(member.getColor());
            // wait update
            final List<SamuraiFile.Data> userData = SamuraiFile.getUserData(Long.parseLong(member.getGuild().getId()), Long.parseLong(member.getUser().getId()));
            assert userData != null;
            StringBuilder stringBuilder = new StringBuilder();
            for (SamuraiFile.Data field : userData)
                stringBuilder.append("**").append(field.name).append("**    `").append(String.valueOf(field.value)).append("`\n");
            setDescription(stringBuilder.toString());
            setFooter("SamuraiStats\u2122", AVATAR);
        }
    }


}
