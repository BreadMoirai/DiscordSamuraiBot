package samurai;

import net.dv8tion.jda.core.JDA;
import samurai.command.Command;
import samurai.entities.base.SamuraiMessage;

/**
 * @author TonTL
 * @version 5.x - 3/13/2017
 */
public class SamuraiDiscord {

    private JDA client;

    public SamuraiDiscord(JDA client) {
        this.client = client;
    }

    public String getPrefix(long l) {
        return null;
    }

    public void onMessage(SamuraiMessage samuraiMessage) {

    }

    public void onCommand(Command c) {

    }

    private
}
