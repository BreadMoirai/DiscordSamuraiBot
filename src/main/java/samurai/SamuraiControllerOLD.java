//
//package samurai;
//
//import com.sun.management.OperatingSystemMXBean;
//import net.dv8tion.jda.core.EmbedBuilder;
//import net.dv8tion.jda.core.JDA;
//import net.dv8tion.jda.core.MessageBuilder;
//import net.dv8tion.jda.core.entities.*;
//import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
//import samurai.data.Data;
//import samurai.data.SamuraiFile;
//import samurai.message.duel.ConnectFour;
//import samurai.message.duel.Game;
//import samurai.data.SamuraiGuild;
//import samurai.osu.OsuJsonReader;
//import samurai.osu.Score;
//
//import java.awt.*;
//import java.io.IOException;
//import java.util.*;
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
//
///**
// * Created by TonTL on 1/28/2017.
// * does NOT work
// */
//
//@SuppressWarnings("ALL")
//class SamuraiControllerOLD {
//
//
//
//    //osu commands
//    private void actionOsu(String key, MessageReceivedEvent event, String[] args) {
//        long guildId = Long.parseLong(event.getGuild().getId());
//        if (!osuGuildMap.containsKey(guildId))
//            if (SamuraiFile.hasScores(guildId))
//                try {
//                    osuGuildMap.put(guildId, new SamuraiGuild(SamuraiFile.getScores(guildId)));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return;
//                }
//            else {
//                osuGuildMap.put(guildId, new SamuraiGuild());
//            }
//
//        switch (key) {
//            case "upload":
//                getFile(event, args);
//                break;
//            case "scorelist":
//                getScoreList(event);
//                break;
//        }
//    }
//
//    private void setOsuProfile(MessageReceivedEvent event, String[] args) {
//        if (args.length == 1) {
//            MessageEmbed osuUserInfo = OsuJsonReader.getUserInfo(args[0]);
//            if (osuUserInfo != null) {
//                SamuraiFile.modifyUserData(Long.parseLong(event.getGuild().getId()), Long.parseLong(event.getAuthor().getId()), true, Integer.parseInt(osuUserInfo.getFooter().getText()), "osu id");
//                event.getChannel().sendMessage(new MessageBuilder()
//                        .append("Successfully linked ")
//                        .append(event.getAuthor().getAsMention())
//                        .append(" to osu account")
//                        .setEmbed(osuUserInfo)
//                        .build()).queue();
//                event.getMessage().addReaction("✅").queue();
//                return;
//            }
//        }
//        event.getMessage().addReaction("❌").queue();
//    }
//
//    private void getOsuProfile(MessageReceivedEvent event, String[] args) {
//        long guildId = Long.parseLong(event.getGuild().getId());
//        if (args.length == 1) {
//            MessageEmbed osuUserInfo = OsuJsonReader.getUserInfo("&type=string" + "&u=" + args[0]);
//            if (osuUserInfo != null)
//                event.getChannel().sendMessage(osuUserInfo).queue();
//            else
//                event.getMessage().addReaction("❌").queue();
//        } else if (event.getMessage().getMentionedUsers().size() > 0) {
//            for (User user : event.getMessage().getMentionedUsers()) {
//                //noinspection ConstantConditions
//                int osuId = SamuraiFile.getUserData(guildId, Long.parseLong(user.getId()), "osu id").get(0).value;
//                if (osuId != 0) {
//                    event.getChannel().sendMessage(OsuJsonReader.getUserInfo("&type=id" + "&u=" + osuId)).queue();
//                } else {
//                    event.getChannel().sendMessage(user.getName() + " has not set their osuID. Try using `!setOsu [Osu!username]`").queue();
//                }
//            }
//        } else {
//            //noinspection ConstantConditions
//            int osuId = SamuraiFile.getUserData(guildId, Long.parseLong(event.getAuthor().getId()), "osu id").get(0).value;
//            event.getChannel().sendMessage(OsuJsonReader.getUserInfo("&type=id" + "&u=" + osuId)).queue();
//        }
//
//    }
//
//    private void getFile(MessageReceivedEvent event, String[] args) {
//
//        String path;
//        List<Message.Attachment> attaches = event.getMessage().getAttachments();
//        if (attaches.size() == 1 && attaches.get(0).getFileName().equals("scores.db")) {
//            path = SamuraiFile.downloadFile(attaches.get(0));
//
//
//
//            if (args.length == 1 && args[0].equalsIgnoreCase("replace"))
//                replace = true;
//            else if ((args.length == 1 && args[0].equalsIgnoreCase("append")) || args.length == 0)
//                replace = false;
//            else
//                return;
//
//
//
//            // wait
//            try {
//                long guildId = Long.parseLong(event.getGuild().getId());
//                HashMap<String, LinkedList<Score>> scores = SamuraiFile.getScores(path);
//                int scoresAdded = osuGuildMap.get(guildId).mergeScoreMap(scores);
//                int scoresRead = 0;
//                for (LinkedList<Score> scoreList : scores.values()) scoresRead += scoreList.size();
//                event.getChannel().sendMessage(String.format("%,d scores read. %,d scores added.", scoresRead, scoresAdded)).queue();
//            } catch (IOException e) {
//                e.printStackTrace();
//                event.getMessage().addReaction("❌").queue();
//            }
//
//        }
//    }
//
//    private void resetData(MessageReceivedEvent event) {
//        if (event.getAuthor().getId().equalsIgnoreCase(DREADMOIRAI)) {
//            for (Guild guild : event.getJDA().getGuilds())
//                SamuraiFile.writeGuildData(guild);
//            event.getMessage().addReaction("✅").queue();
//        }
//    }
//
//    private void shutdown(MessageReceivedEvent event) {
//        if (event.getAuthor().getId().equalsIgnoreCase(DREADMOIRAI))
//            event.getMessage().addReaction("\uD83D\uDC4B").queue();
//        guildMapCollecter.shutdown();
//        for (Long guildId : osuGuildMap.keySet()) {
//            SamuraiFile.writeScoreData(guildId, osuGuildMap.get(guildId).getScoreMap());
//        }
//        event.getJDA().shutdown();
//    }
//
//
//    //debugging
//    private void getScoreList(MessageReceivedEvent event) {
//        long guildId = Long.parseLong(event.getGuild().getId());
//        if (osuGuildMap.get(guildId).getScoreMap().isEmpty()) {
//            event.getChannel().sendMessage("No scores found. Try using `!upload` as a comment with a `scores.db` file found in your osu installation folder.").queue();
//            return;
//        }
//        MessageBuilder messageBuilder = new MessageBuilder().append("```\n");
//        HashMap<String, AtomicInteger> nameScore = new HashMap<>();
//        for (LinkedList<Score> scoreList : osuGuildMap.get(guildId).getScoreMap().values()) {
//            for (Score score : scoreList) {
//                if (!nameScore.containsKey(score.getPlayer()))
//                    nameScore.put(score.getPlayer(), new AtomicInteger(0));
//                nameScore.get(score.getPlayer()).incrementAndGet();
//            }
//        }
//        for (String name : nameScore.keySet()) {
//            messageBuilder.append(String.format("%-30s", name.replace(" ", "_")).replace(" ", ".").replace("_", " ")).append(nameScore.get(name)).append("\n");
//        }
//        event.getChannel().sendMessage(messageBuilder.append("```").build()).queue();
//    }
//