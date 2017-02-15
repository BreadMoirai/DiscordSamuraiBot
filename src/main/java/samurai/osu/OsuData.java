package samurai.osu;


import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.data.SamuraiFile;
import samurai.osu.enums.Mod;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by TonTL on 1/23/2017.
 * holds osu
 */
public class OsuData {

    private int version;
    private HashMap<String, Beatmap> beatmaps;
    private List<String> hashes;

    public OsuData() {
        beatmaps = new HashMap<>();
        hashes = new ArrayList<>();
    }


    public int readScoresDB(String filepath) {
        HashMap<String, LinkedList<Score>> scoreMap;
        try {

            scoreMap = SamuraiFile.getScores(filepath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        for (String hash : scoreMap.keySet()) {
            if (scoreMap.get(hash).size() > 0) {
                if (beatmaps.containsKey(hash)) {
                    beatmaps.get(hash).appendScores(scoreMap.get(hash));
                } else {
                    beatmaps.put(hash, new Beatmap().setEmpty(true).setScores(scoreMap.get(hash)));
                    hashes.add(hash);
                }
            } else {
                System.out.println("Empty!");
            }
        }
        return scoreMap.size();


    }

    public Message getBeatmap(Random r) {
        String hash = hashes.get(r.nextInt(hashes.size()));
        System.out.println(hash);
        return buildBeatmapInfo(hash, false, false);
    }

    public List<Message> getAllBeatmaps() {
        ArrayList<Message> beatmapInfoArray = new ArrayList<>();
        for (String hash : beatmaps.keySet()) {
            beatmapInfoArray.add(buildBeatmapInfo(hash, false ,false));
        }
        return beatmapInfoArray;
    }

    public Message buildBeatmapInfo(String hash, boolean fullScore, boolean fullMap) {
        System.out.println("Building Beatmap: " + fullScore + " | " + fullMap);
        Beatmap beatmap = beatmaps.get(hash);
        if (beatmap.isEmpty()) {
            List<Score> mapScores = beatmap.getScores();
            beatmap = OsuJsonReader.getBeatmapInfo(hash);
            beatmap.setScores(mapScores);
        }
        //System.out.println(beatmap);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(String.format("%s by %s", beatmap.getSong(), beatmap.getArtist()), null)
                .setColor(Color.PINK)
                .setAuthor("Osu!BeatmapInfo", String.format("https://samurai.osu.ppy.sh/b/%s&m=%d", beatmap.getMapID(), beatmap.getGameMode().value()), "http://w.ppy.sh/c/c9/Logo.png")
                .setFooter(beatmap.getHash(), "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg");
        double stars = beatmap.getDifficultyRating();
        StringBuilder diff = new StringBuilder().append(beatmap.getRankedStatus().getEmote());
        diff.append(String.format("[**%s**] ", beatmap.getDifficulty()));
        for (int i = 0; i < (int) stars; i++) {
            diff.append("⭐");
        }
        if (fullMap) {
            diff.append(String.format(" (%.4f) mapped by %s", stars, beatmap.getMapper()));
            embedBuilder.addField("Details", String.format("**AR**: %.2f    **CS**: %.2f    **HP**: %.2f    **OD**: %.2f", beatmap.getAr(), beatmap.getCs(), beatmap.getHp(), beatmap.getOd()), false);
            embedBuilder.addField("Length", String.format("%d:%02d (%d:%02d)", beatmap.getTotalTime() / 60000, beatmap.getTotalTime() / 1000 % 60, beatmap.getDrainTime() / 60, beatmap.getDrainTime() % 60), true);
        }
        embedBuilder.setDescription(diff.toString());
        StringBuilder scoreField = new StringBuilder();
        System.out.println("Scores Found: " + beatmap.getScores().size());
        for (Score score : beatmap.getScores()) {
            //System.out.println(score);
            scoreField.append(String.format("**%15s**  %s  %d   (%.2f%%)%n", score.getPlayer(), score.getGrade().getEmote(), score.getScore(), score.getAccuracy() * 100));
            if (fullScore) {
                scoreField
                        .append(String.format("<:hit_300:273365730047557632>`%d`      <:hit_100:273365765275779072>`%d`      <:hit_50:273365803452334080>`%d`      <:hit_miss:273365818211827714>`%d`%n", score.getCount300(), score.getCount100(), score.getCount50(), score.getCount0()))
                        .append(String.format("Max Combo (%dx)%s **Mods:**", score.getMaxCombo(), (score.isPerfectCombo() ? "\u2705" : "")));
                for (Mod m : Mod.getMods(score.getModCombo())) {
                    scoreField.append(m.toString()).append(" ");
                }
            }
            scoreField.append(scoreField.substring(0, scoreField.length())).append("%n");
        }
        //System.out.println(scoreField);
        embedBuilder.addField("Scores", scoreField.toString(), false);
        //StringBuilder beatmapData = new StringBuilder().append("\u2b50");


        return new MessageBuilder().setEmbed(embedBuilder.build()).build();
    }

//    public boolean readOsuDB(String filepath, boolean readAll) {
//        HashMap<String, Beatmap> beatmapTemp;
//        try {
//            beatmapTemp = new OsuParser(filepath).parse().getBeatmaps();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//        if (readAll) {
//            beatmaps = beatmapTemp;
//            hashes.clear();
//            hashes.addAll(beatmaps.keySet());
//            System.out.println("Added Beatmaps (full)");
//            return true;
//        } else {
//            if (hashes.isEmpty()) {
//                return false;
//            }
//            for (String hash : beatmapTemp.keySet()) {
//                if (hashes.contains(hash)) {
//                    List<Score> beatmapScores = beatmaps.get(hash).getScores();
//                    //System.out.println(beatmapScores.get(0));
//                    beatmaps.put(hash, beatmapTemp.get(hash).setScores(beatmapScores));
//                    //System.out.println(beatmaps.get(hash).getScores().get(0));
//                }
//            }
//            System.out.println("Added Beatmaps (partial)");
//
//            for (Beatmap b : beatmaps.values()) {
//                for (Score s : b.getScores()) {
//                    System.out.println(s);
//                }
//            }
//            return true;
//        }
//    }


    private boolean saveScores() {
        System.out.println("Saving Scores");
        //try(DataOutputStream out = new DataOutputStream

        return true;
    }

}
