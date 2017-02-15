package samurai;

import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;
import samurai.data.Data;
import samurai.data.SamuraiFile;
import samurai.message.duel.ConnectFour;
import samurai.message.duel.Game;
import samurai.osu.OsuGuild;
import samurai.osu.OsuJsonReader;
import samurai.osu.Score;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by TonTL on 1/28/2017.
 * does work
 */
@SuppressWarnings("ALL")
class SamuraiControllerOLD {

    private static final String AVATAR = "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg";
    private static final String githubCommitApi = "https://api.github.com/repos/DreadMoirai/DiscordSamuraiBot/git/commits/", majorSha = "c6bab666ef7a803025c670aecfa91d68e10c2bd7", minorSha = "f000d959882dec996ac073960509ef24610ce29f";
    private static final String DREADMOIRAI = "232703415048732672";

    OperatingSystemMXBean operatingSystemMXBean;

    private long initializationTime;
    private EventListenerOLD listener;
    private Random random;
    private HashMap<Long, Game> gameMap; //message id
    private HashMap<Long, OsuGuild> osuGuildMap; //guild id
    private HashMap<Long, Integer> trackedUsers; // user id

    private long callsMade;
    private ScheduledExecutorService guildMapCollecter;


    SamuraiControllerOLD(EventListenerOLD eventListenerOLD) {
        callsMade = 0;
        initializationTime = System.currentTimeMillis() - 5000;
        listener = eventListenerOLD;
        random = new Random();
        gameMap = new HashMap<>();
        osuGuildMap = new HashMap<>();
        trackedUsers = new HashMap<>();
        createExecutorGuildMapCheck(10);
    }

    private void createExecutorGuildMapCheck(int minutes) {
        guildMapCollecter = Executors.newSingleThreadScheduledExecutor();
        Runnable collector = () -> {
            System.out.printf("Checking Guilds: %d guilds found.%n", osuGuildMap.size());
            for (Long guildId : osuGuildMap.keySet()) {
                OsuGuild guild = osuGuildMap.get(guildId);
                if (guild.isActive())
                    guild.setInactive();
                else {
                    osuGuildMap.remove(guildId);
                    SamuraiFile.writeScoreData(guildId, guild.getScoreMap());
                }
            }
            System.gc();
        };
        guildMapCollecter.scheduleAtFixedRate(collector, minutes, minutes, TimeUnit.MINUTES);

    }


    void action(String key, MessageReceivedEvent event, String... args) {
        callsMade += 1;
        boolean success = true;
        switch (key) {
            //basic
            case "status":
                getStatus(event);
                break;
            case "fight":
            case "duel":
                getChallenge(event);
                break;
            case "uptime":
                getUptime(event);
                break;
            case "stats":
            case "stat":
                getStat(event);
                break;
            case "help":
                getHelp(event);
                break;
            case "changelog":
            case "execute":
                getCommitJson(event.getChannel(), true);
                getCommitJson(event.getChannel(), false);
                break;
            case "prefix":
                setPrefix(event, args);
                break;
            case "todo":
                getTodo(event, args);
                break;
            //osuCommands
            case "setosu":
                setOsuProfile(event, args);
                break;
            case "getosu":
                getOsuProfile(event, args);
                break;
            case "upload":
            case "scorelist":
                actionOsu(key, event, args);
                break;
            //admin commands
            case "increment":
                addUserStat(event, args);
                break;
            case "reset":
                resetData(event);
                break;
            case "shutdown":
                shutdown(event);
                break;
            default:
                success = false;
        }
        if (success)
            SamuraiFile.modifyUserData(Long.parseLong(event.getGuild().getId()), Long.parseLong(event.getAuthor().getId()), false, 1, "commands used");
    }


    //basic methods
    private void getStatus(MessageReceivedEvent event) {
        SamuraiBuilder sb = new SamuraiBuilder(event.getJDA(), Long.parseLong(event.getGuild().getId()));
        event.getChannel().sendMessage(new MessageBuilder().setEmbed(sb.build()).build()).queue(message -> message.editMessage(new MessageBuilder().setEmbed(new EmbedBuilder(message.getEmbeds().get(0)).setTimestamp(message.getCreationTime()).build()).build()).queue());
    }

    private void getChallenge(MessageReceivedEvent event) {
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
                            message.editMessage(gameMap.get(Long.parseLong(message.getId())).getMessage()).queue();
                        });
                }
            });
        } else {
            event.getMessage().addReaction("❌").queue();
        }
    }

    private void getUptime(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Samurai has been online for " + getActiveTime()).queue();
    }

    private void getStat(MessageReceivedEvent event) {
        List<User> mentions = event.getMessage().getMentionedUsers();
        if (mentions.size() > 0)
            for (User user : mentions)
                event.getChannel().sendMessage(new MessageBuilder().setEmbed(new SamuraiBuilder(event.getGuild().getMember(user)).build()).build()).queue();
        else
            event.getChannel().sendMessage(new MessageBuilder().setEmbed(new SamuraiBuilder(event.getGuild().getMember(event.getAuthor())).build()).build()).queue();
    }

    private void getHelp(MessageReceivedEvent event) {
        event.getChannel().sendMessage(new SamuraiBuilder("help.txt", SamuraiFile.getPrefix(Long.parseLong(event.getGuild().getId()))).build()).queue();
    }

    private void getCommitJson(MessageChannel channel, boolean major) {
        try (InputStream is = new URL(githubCommitApi + (major ? majorSha : minorSha)).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            JSONObject json = new JSONObject(sb.toString());
            channel.sendMessage(new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                            .setTitle("Changelog: " + json.getJSONObject("author").getString("date").substring(0, 10), json.getString("html_url"))
                            .setDescription(json.getString("message"))
                            .setFooter("Committed by " + json.getJSONObject("author").getString("name"), null)
                            .build())
                    .build()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPrefix(MessageReceivedEvent event, String[] args) {
        if (args != null && args.length == 1 && args[0].length() <= 4) {
            SamuraiFile.setPrefix(Long.parseLong(event.getGuild().getId()), args[0]);
            listener.updatePrefix(Long.parseLong(event.getGuild().getId()), args[0]);
            event.getMessage().addReaction("✅").queue();
        } else {
            event.getMessage().addReaction("❌").queue();
        }
    }

    private void getTodo(MessageReceivedEvent event, String[] args) {
        if (args.length == 0)
            event.getChannel().sendMessage(new SamuraiBuilder("todo.txt", null).build()).queue();
        else {
            SamuraiFile.addTodo(args);
            event.getMessage().addReaction("✅").queue();
        }
    }

    //osu commands
    private void actionOsu(String key, MessageReceivedEvent event, String[] args) {
        long guildId = Long.parseLong(event.getGuild().getId());
        if (!osuGuildMap.containsKey(guildId))
            if (SamuraiFile.hasScores(guildId))
                try {
                    osuGuildMap.put(guildId, new OsuGuild(SamuraiFile.getScores(guildId)));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            else {
                osuGuildMap.put(guildId, new OsuGuild());
            }

        switch (key) {
            case "upload":
                getFile(event, args);
                break;
            case "scorelist":
                getScoreList(event);
                break;
        }
    }

    private void setOsuProfile(MessageReceivedEvent event, String[] args) {
        if (args.length == 1) {
            MessageEmbed osuUserInfo = OsuJsonReader.getUserInfo(args[0]);
            if (osuUserInfo != null) {
                SamuraiFile.modifyUserData(Long.parseLong(event.getGuild().getId()), Long.parseLong(event.getAuthor().getId()), true, Integer.parseInt(osuUserInfo.getFooter().getText()), "osu id");
                event.getChannel().sendMessage(new MessageBuilder()
                        .append("Successfully linked ")
                        .append(event.getAuthor().getAsMention())
                        .append(" to osu account")
                        .setEmbed(osuUserInfo)
                        .build()).queue();
                event.getMessage().addReaction("✅").queue();
                return;
            }
        }
        event.getMessage().addReaction("❌").queue();
    }

    private void getOsuProfile(MessageReceivedEvent event, String[] args) {
        long guildId = Long.parseLong(event.getGuild().getId());
        if (args.length == 1) {
            MessageEmbed osuUserInfo = OsuJsonReader.getUserInfo("&type=string" + "&u=" + args[0]);
            if (osuUserInfo != null)
                event.getChannel().sendMessage(osuUserInfo).queue();
            else
                event.getMessage().addReaction("❌").queue();
        } else if (event.getMessage().getMentionedUsers().size() > 0) {
            for (User user : event.getMessage().getMentionedUsers()) {
                //noinspection ConstantConditions
                int osuId = SamuraiFile.getUserData(guildId, Long.parseLong(user.getId()), "osu id").get(0).value;
                if (osuId != 0) {
                    event.getChannel().sendMessage(OsuJsonReader.getUserInfo("&type=id" + "&u=" + osuId)).queue();
                } else {
                    event.getChannel().sendMessage(user.getName() + " has not set their osuID. Try using `!setOsu [Osu!username]`").queue();
                }
            }
        } else {
            //noinspection ConstantConditions
            int osuId = SamuraiFile.getUserData(guildId, Long.parseLong(event.getAuthor().getId()), "osu id").get(0).value;
            event.getChannel().sendMessage(OsuJsonReader.getUserInfo("&type=id" + "&u=" + osuId)).queue();
        }

    }

    private void getFile(MessageReceivedEvent event, String[] args) {

        String path;
        List<Message.Attachment> attaches = event.getMessage().getAttachments();
        if (attaches.size() == 1 && attaches.get(0).getFileName().equals("scores.db")) {
            path = SamuraiFile.downloadFile(attaches.get(0));

            /*
            if (args.length == 1 && args[0].equalsIgnoreCase("replace"))
                replace = true;
            else if ((args.length == 1 && args[0].equalsIgnoreCase("append")) || args.length == 0)
                replace = false;
            else
                return;
            */


            // wait
            try {
                long guildId = Long.parseLong(event.getGuild().getId());
                HashMap<String, LinkedList<Score>> scores = SamuraiFile.getScores(path);
                int scoresAdded = osuGuildMap.get(guildId).mergeScoreMap(scores);
                int scoresRead = 0;
                for (LinkedList<Score> scoreList : scores.values()) scoresRead += scoreList.size();
                event.getChannel().sendMessage(String.format("%,d scores read. %,d scores added.", scoresRead, scoresAdded)).queue();
            } catch (IOException e) {
                e.printStackTrace();
                event.getMessage().addReaction("❌").queue();
            }

        }
    }

    //admin commands
    private void addUserStat(MessageReceivedEvent event, String[] args) {
        List<User> mentionedUsers = event.getMessage().getMentionedUsers();
        if (mentionedUsers.size() == 0 || args.length < 2 || !event.getAuthor().getId().equalsIgnoreCase(DREADMOIRAI)) {
            event.getMessage().addReaction("❌").queue();
            return;
        }
        try {
            Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            event.getMessage().addReaction("❌").queue();
            return;
        }
        List<String> dataNames = SamuraiFile.getDataNames();
        for (int i = 1; i < args.length; i++) {
            args[i] = args[i].replace('_', ' ');
            if (!dataNames.contains(args[i])) {
                event.getMessage().addReaction("❌").queue();
                return;
            }
        }
        // end argument validation
        for (User u : mentionedUsers) {
            SamuraiFile.modifyUserData(Long.parseLong(event.getGuild().getId()), Long.parseLong(u.getId()), false, Integer.parseInt(args[0]), Arrays.copyOfRange(args, 1, args.length));
        }
        event.getMessage().addReaction("✅").queue();
    }

    private void resetData(MessageReceivedEvent event) {
        if (event.getAuthor().getId().equalsIgnoreCase(DREADMOIRAI)) {
            for (Guild guild : event.getJDA().getGuilds())
                SamuraiFile.writeGuildData(guild);
            event.getMessage().addReaction("✅").queue();
        }
    }

    private void shutdown(MessageReceivedEvent event) {
        if (event.getAuthor().getId().equalsIgnoreCase(DREADMOIRAI))
            event.getMessage().addReaction("\uD83D\uDC4B").queue();
        guildMapCollecter.shutdown();
        for (Long guildId : osuGuildMap.keySet()) {
            SamuraiFile.writeScoreData(guildId, osuGuildMap.get(guildId).getScoreMap());
        }
        event.getJDA().shutdown();
    }


    //debugging
    private void getScoreList(MessageReceivedEvent event) {
        long guildId = Long.parseLong(event.getGuild().getId());
        if (osuGuildMap.get(guildId).getScoreMap().isEmpty()) {
            event.getChannel().sendMessage("No scores found. Try using `!upload` as a comment with a `scores.db` file found in your osu installation folder.").queue();
            return;
        }
        MessageBuilder messageBuilder = new MessageBuilder().append("```\n");
        HashMap<String, AtomicInteger> nameScore = new HashMap<>();
        for (LinkedList<Score> scoreList : osuGuildMap.get(guildId).getScoreMap().values()) {
            for (Score score : scoreList) {
                if (!nameScore.containsKey(score.getPlayer()))
                    nameScore.put(score.getPlayer(), new AtomicInteger(0));
                nameScore.get(score.getPlayer()).incrementAndGet();
            }
        }
        for (String name : nameScore.keySet()) {
            messageBuilder.append(String.format("%-30s", name.replace(" ", "_")).replace(" ", ".").replace("_", " ")).append(nameScore.get(name)).append("\n");
        }
        event.getChannel().sendMessage(messageBuilder.append("```").build()).queue();
    }


    private String getActiveTime() {
        long timeDifference = System.currentTimeMillis() - initializationTime;
        int seconds = (int) ((timeDifference / 1000) % 60);
        int minutes = (int) ((timeDifference / 60000) % 60);
        int hours = (int) ((timeDifference / 3600000) % 24);
        int days = (int) (timeDifference / 86400000);
        if (days > 0) {
            return String.format("%d days, %d hours, %d minutes, %d seconds.", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%d hours, %d minutes, %d seconds.", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d minutes, %d seconds.", minutes, seconds);
        } else {
            return String.format("%d seconds.", seconds);
        }
    }

    /*
    void updateGame(MessageReactionAddEvent event, Long gameId) {
        List<String> connectfour_reactions = ConnectFour.CONNECTFOUR_REACTIONS;
        String emoji = event.getReaction().getEmote().getName();
        if (emoji.equals("⚔") && !gameMap.keySet().contains(gameId)) {
            //wait
            event.getChannel().getMessageById(String.valueOf(gameId)).queue(message -> message.clearReactions().queue(done -> {
                message.editMessage("Creating " + message.getMentionedUsers().get(0).getAsMention() + "'s game...").queue();
                for (int i = 0, connectfour_reactionsSize = connectfour_reactions.size(); i < connectfour_reactionsSize; i++) {
                    if (i != connectfour_reactionsSize - 1)
                        message.addReaction(connectfour_reactions.get(i)).queue();
                    else
                        message.addReaction(connectfour_reactions.get(i)).queue(success -> {
                            gameMap.put(gameId, new ConnectFour(message.getMentionedUsers().get(0), event.getUser(), random.nextBoolean()));
                            message.editMessage(gameMap.get(gameId).getMessage()).queue();
                        });
                }
            }));
        } else if (connectfour_reactions.contains(emoji)) {
            Game game = gameMap.get(gameId);
            if (game.isNext(event.getUser())) {
                Message message = event.getChannel().getMessageById(String.valueOf(gameId)).complete();
                game.perform(connectfour_reactions.indexOf(emoji), event.getUser());
                event.getReaction().removeReaction(event.getUser()).queue();
                message.editMessage(game.getMessage()).queue();

                if (game.hasEnded()) {
                    message.editMessage(game.getMessage()).queue();
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
    */


    class SamuraiBuilder extends EmbedBuilder {

        SamuraiBuilder(String file, String token) {
            setAuthor("Samurai - " + file, null, AVATAR);
            List<String> textFile = SamuraiFile.readTextFile(file);
            StringBuilder stringBuilder = new StringBuilder();
            for (String line : textFile) {
                if (token != null)
                    stringBuilder.append(line.replace("[prefix]", token)).append("\n");
                else
                    stringBuilder.append(line).append("\n");
            }
            setDescription(stringBuilder.toString());
        }

        SamuraiBuilder(JDA jda, long guildId) {
            Runtime thisInstance = Runtime.getRuntime();
            int mb = 1024 * 1024;
            setTitle("Status: " + jda.getPresence().getStatus().name(), null);
            setColor(Color.GREEN);
            setFooter("Samurai\u2122", AVATAR);
            addField("Local Status", String.format("**%-19s**`%s", "current state:", osuGuildMap.containsKey(guildId) ? String.format("Active`\n**%-20s**`%d`\n**%-18s**`%d`", "score count:", osuGuildMap.get(guildId).getScoreCount(), "tracked users:", trackedUsers.size()) : "Inactive`"), false);
            addField("Guild Count", String.format("**%-16s**`%d`\n**%-14s**`%d`", "total:", jda.getGuilds().size(), "active:", osuGuildMap.size()), true);
            addField("User Count", String.format("`%d`", jda.getUsers().size() - 1), true);
            addField("Time Active", getActiveTime(), false);
            addField("Messages", String.format("**%-15s**`%d`\n**%-20s**`%d`\n**%-19s**`%.2f`", "received:", callsMade, "sent:", listener.messagesSent, "cpm:", 60.0 * callsMade / ((System.currentTimeMillis() - initializationTime) / 1000)), true);
            addField("Osu!API", String.format("**%-16s**`%d`\n**%-17s**`%d`", "calls made:", OsuJsonReader.count, "calls/min:", OsuJsonReader.count / ((System.currentTimeMillis() - initializationTime) / 6000)), true);
            addField("CpuLoad", String.format("`%.3f%%`/`%.3f%%`", operatingSystemMXBean.getProcessCpuLoad() * 100.00, operatingSystemMXBean.getSystemCpuLoad() * 100.00), true);
            addField("Memory", String.format("**used:\t**`%d MB`\n**total:\t**`%d MB`\n**max:\t**`%d MB`", (thisInstance.totalMemory() - thisInstance.freeMemory()) / mb, thisInstance.totalMemory() / mb, thisInstance.maxMemory() / mb), true);
            addField("Threads", String.format("**%-14s**`%d`\n**%-16s**`%d`", "active:", 6, "total:", 12), true);
        }

        SamuraiBuilder(Member member) {
            setAuthor(member.getEffectiveName(), null, member.getUser().getAvatarUrl());
            setColor(member.getColor());
            // wait execute
            final List<Data> userData = SamuraiFile.getUserData(Long.parseLong(member.getGuild().getId()), Long.parseLong(member.getUser().getId()));
            assert userData != null;
            StringBuilder stringBuilder = new StringBuilder();
            for (Data field : userData)
                stringBuilder.append("**").append(field.name).append("**    `").append(String.valueOf(field.value)).append("`\n");
            setDescription(stringBuilder.toString());
            setFooter("SamuraiStats\u2122", AVATAR);
        }
    }


}
