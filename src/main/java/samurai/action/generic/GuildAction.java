package samurai.action.generic;

import samurai.SamuraiController;
import samurai.action.Action;
import samurai.persistent.SamuraiMessage;

import java.util.concurrent.TimeUnit;

/**
 * @author TonTL
 * @since 4.0
 */
public class GuildAction extends Action {

    @Override
    public SamuraiMessage call() {
        SamuraiController.getOfficialChannel().createInvite().setMaxAge(15L, TimeUnit.MINUTES).queue(invite -> channel.sendMessage("https://discord.gg/" + invite.getCode()).queue());
        return null;
    }
}
