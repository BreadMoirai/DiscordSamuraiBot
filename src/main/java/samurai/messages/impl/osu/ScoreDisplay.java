package samurai.messages.impl.osu;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.messages.base.SamuraiMessage;
import samurai.osu.model.Score;

import java.awt.*;
import java.time.*;

public class ScoreDisplay extends SamuraiMessage {


    private final Score score;

    public ScoreDisplay(Score score) {
        this.score = score;
    }

    @Override
    protected Message initialize() {
        final MessageBuilder messageBuilder = new MessageBuilder();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final StringBuilder descriptionBuilder = embedBuilder.getDescriptionBuilder();
        embedBuilder.setColor(Color.PINK);
        embedBuilder.setAuthor(score.getPlayer(), "https://new.ppy.sh/u/" + score.getPlayerId(), "https://a.ppy.sh/" + score.getPlayerId());
        embedBuilder.setTimestamp((LocalDateTime.ofEpochSecond(score.getTimestamp(), 0, ZoneOffset.of("+08:00"))));
        //embedBuilder.setThumbnail("https://b.ppy.sh/thumb/" + score.getSetId() + ".jpg");
        descriptionBuilder.append("**").append(score.getBeatmapId()).append("**")
                .append("\nScore: `").append(score.getScore()).append('`')
                .append("\nGrade: ").append(score.getGrade().getEmote())
                .append("\nAccuracy: `").append(score.getAccuracy()).append('`')
                .append("   Highest Combo: `").append(score.getMaxCombo()).append('`')
                .append("\n 300: `").append(score.getCount300())
                .append("`  100: `").append(score.getCount100())
                .append("`  50: `").append(score.getCount50())
                .append("`  miss: `").append(score.getCount0())
                .append("`");
        return messageBuilder.setEmbed(embedBuilder.build()).build();
    }

    @Override
    protected void onReady(Message message) {

    }
}
