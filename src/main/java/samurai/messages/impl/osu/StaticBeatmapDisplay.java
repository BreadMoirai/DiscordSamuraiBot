package samurai.messages.impl.osu;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.entities.model.SGuild;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.enums.Mod;
import samurai.osu.model.Beatmap;
import samurai.osu.model.BeatmapSet;
import samurai.osu.model.Score;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * @author TonTL
 * @version 3/21/2017
 */
public class StaticBeatmapDisplay extends SamuraiMessage {

    private final BeatmapSet set;
    private final boolean fullScore;
    private final boolean fullMap;
    private final String hash;
    private List<Score> scores;

    public StaticBeatmapDisplay(BeatmapSet set, boolean fullScore, boolean fullMap, String hash, SGuild guild) {
        this.set = set;
        this.fullScore = fullScore;
        this.fullMap = fullMap;
        this.hash = hash;
        //todo
        //this.scores = guild.getScoreMap().get(hash);
    }

    public StaticBeatmapDisplay(BeatmapSet set, boolean fullScore, boolean fullMap, String beatmapHash, Score lastScore) {
        this.set = set;
        this.fullScore = fullScore;
        this.fullMap = fullMap;
        hash = beatmapHash;
        this.scores = Collections.singletonList(lastScore);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected Message initialize() {
        Beatmap beatmap = set.getBeatmapByHash(hash);
        if (beatmap == null) {
            System.err.println("Could not find beatmap: StaticBeatmapDisplay");
            return null;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(String.format("%s by %s", set.getSong(), set.getArtist()), null)
                .setColor(Color.PINK)
                .setAuthor("Osu!BeatmapInfo", String.format("https://osu.ppy.sh/b/%s&m=%d", beatmap.getMapID(), beatmap.getGameMode().value()), "http://w.ppy.sh/c/c9/Logo.png")
                .setFooter(String.valueOf(beatmap.getMapID()), "https://cdn.discordapp.com/avatars/270044218167132170/c3b45c87f7b63e7634665a11475beedb.jpg")
                .setThumbnail("https://b.ppy.sh/thumb/" + set.getSetId() + ".jpg");
        double stars = beatmap.getDifficultyRating();
        StringBuilder diff = new StringBuilder().append(set.getRankedStatus().getEmote());
        diff.append(String.format("[**%s**] ", beatmap.getDifficulty()));
        for (int i = 0; i < (int) stars; i++) diff.append("\u2b50");
        if (fullMap) {
            diff.append(String.format(" (%.4f) mapped by %s", stars, set.getMapper()));
            embedBuilder.addField("Details", String.format("**AR**: %.2f    **CS**: %.2f    **HP**: %.2f    **OD**: %.2f", beatmap.getAr(), beatmap.getCs(), beatmap.getHp(), beatmap.getOd()), false);
            embedBuilder.addField("Length", String.format("%d:%02d (%d:%02d)", beatmap.getTotalTime() / 60, beatmap.getTotalTime() % 60, beatmap.getDrainTime() / 60, beatmap.getDrainTime() % 60), true);
        }
        embedBuilder.setDescription(diff.toString());
        if (scores != null) {
            StringBuilder scoreField = new StringBuilder();
            System.out.println("Scores Found: " + scores.size());
            for (Score score : scores) {
                scoreField.append(String.format("**%15s**  %s  %d   (%.2f%%)%n", score.getPlayer(), score.getGrade().getEmote(), score.getScore(), score.getAccuracy() * 100));
                if (fullScore) {
                    scoreField
                            .append(String.format("<:hit_300:273365730047557632>`%d`      <:hit_100:273365765275779072>`%d`      <:hit_50:273365803452334080>`%d`      <:hit_miss:273365818211827714>`%d`%n", score.getCount300(), score.getCount100(), score.getCount50(), score.getCount0()))
                            .append(String.format("%dx/%dx **Mods:**", score.getMaxCombo(), beatmap.getMaxCombo()));
                    for (Mod m : Mod.getMods(score.getModCombo())) {
                        scoreField.append(m.toString()).append(" ");
                    }
                }
                scoreField.append("\n");
            }
            embedBuilder.addField("Scores", scoreField.toString(), false);
        }
        return new MessageBuilder().setEmbed(embedBuilder.build()).build();
    }

    @Override
    protected void onReady(Message message) {
            //todo
    }
}
