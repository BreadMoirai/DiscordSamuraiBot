package samurai.osu.tracker;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RestAction;
import samurai.entities.model.Player;
import samurai.osu.api.OsuAPI;
import samurai.osu.enums.GameMode;
import samurai.osu.model.Score;
import samurai.util.MessageUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author TonTL
 * @version 5.x - 3/18/2017
 */
public class OsuSession {

    private final Player playerStart;
    private final List<TextChannel> channels;
    private final ScoreCache scoresFound;

    OsuSession(Player playerStart) {
        this.playerStart = playerStart;
        channels = new ArrayList<>(5);
        scoresFound = new ScoreCache();
    }

    public Player getPlayer() {
        return playerStart;
    }

    public boolean addChannel(TextChannel channel) {
        channel.sendMessage("Kakugo!").queue();
        return !channels.contains(channel) && channels.add(channel);
    }

    void update() {
        final List<Score> userRecent = OsuAPI.getUserRecent(playerStart.getOsuName(), playerStart.getOsuId(), GameMode.OSU, 15);
        channels.forEach(textChannel -> textChannel.sendMessage("Found " + userRecent.size() + " Recent Scores").queue());
        scoresFound.addScores(userRecent)
                .forEach(score -> channels.forEach(textChannel -> textChannel.sendMessage(MessageUtil.build(score)).queue()));
    }



}
