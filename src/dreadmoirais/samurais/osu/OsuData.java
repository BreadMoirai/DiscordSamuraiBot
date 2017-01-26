package dreadmoirais.samurais.osu;

import dreadmoirais.samurais.osu.enums.Mod;
import dreadmoirais.samurais.osu.parse.OsuParser;
import dreadmoirais.samurais.osu.parse.ScoresParser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by TonTL on 1/23/2017.
 * holds scores
 */
public class OsuData {

    private int version;
    private HashMap<String, Beatmap> beatmaps;
    private List<String> hashes;

    private List<Emote> emotes;

    public OsuData() {
        hashes = new ArrayList<>();
    }


    public boolean readScoresDB(String filepath) {
        try {
            ScoresParser parser = new ScoresParser(filepath);
            Map<String, List<Score>> scoreMap = parser.parse().getBeatmapScores();
            for (String hash : scoreMap.keySet()) {
                if (beatmaps.containsKey(hash)) {
                    beatmaps.get(hash).appendScores(scoreMap.get(hash));
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public Message getBeatmap(Random r) {
        String hash = hashes.get(r.nextInt(hashes.size()));
        System.out.println(hash);
        return buildBeatmapInfo(hash, false, false);
    }

    public Message buildBeatmapInfo(String hash, boolean fullScore, boolean fullMap) {
        Beatmap beatmap = beatmaps.get(hash);
        if (beatmap == null) {
            return null;
            /**
             * use api to get info
             */
        }
        //System.out.println(beatmap);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(String.format("%s by %s", beatmap.getSong(), beatmap.getArtist()))
                .setColor(Color.PINK)
                .setAuthor("Osu!BeatmapInfo", String.format("https://osu.ppy.sh/b/%s&m=%d", beatmap.getMapID(), beatmap.getGameMode().value()), "http://w.ppy.sh/c/c9/Logo.png")
                .setFooter(beatmap.getHash(), "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg");
        double stars = beatmap.getStarRating().get(beatmap.getGameMode().value()).get(Mod.None.value());
        String diff = beatmap.getRankedStatus().getEmote();
        diff += String.format("[**%s**] ", beatmap.getDifficulty());
        for (int i = 0; i < (int)stars; i++) {
            diff += "⭐";
        }
        if (fullMap) {
            diff += String.format(" (%.4f) mapped by %s", stars, beatmap.getMapper());
            embedBuilder.addField("Details", String.format("**AR**: %.2f    **CS**: %.2f    **HP**: %.2f    **OD**: %.2f", beatmap.getAr(), beatmap.getCs(), beatmap.getHp(), beatmap.getOd()), false);
            embedBuilder.addField("Length", String.format("%d:%02d (%d:%02d)", beatmap.getTotalTime()/60000, beatmap.getTotalTime()/1000%60, beatmap.getDrainTime()/60, beatmap.getDrainTime()%60), true);
        }
        embedBuilder.setDescription(diff);

        //StringBuilder beatmapData = new StringBuilder().append("\u2b50");



        return new MessageBuilder().setEmbed(embedBuilder.build()).build();
    }

    public boolean readOsuDB(String filepath) {
        try {
            beatmaps = new OsuParser(filepath).parse().getBeatmaps();
            hashes.addAll(beatmaps.keySet());

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private boolean saveScores() {
        System.out.println("Saving Scores");
        //try(DataOutputStream out = new DataOutputStream

        return true;
    }

    public void setEmotes(List<Emote> emotes) {
        this.emotes = emotes;
    }
}
