package samurai.action.general;

import net.dv8tion.jda.core.MessageBuilder;
import samurai.SamuraiController;
import samurai.action.Action;
import samurai.annotations.Key;
import samurai.message.SamuraiMessage;
import samurai.message.fixed.FixedMessage;

import java.util.concurrent.TimeUnit;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("join")
public class Join extends Action {

    @Override
    public SamuraiMessage buildMessage() {
        return new FixedMessage()
                .setMessage(new MessageBuilder()
                        .append("https://discord.gg/")
                        .append(SamuraiController
                                .getOfficialChannel()
                                .createInvite()
                                .setMaxAge(15L, TimeUnit.MINUTES)
                                .complete()
                                .getCode())
                        .build());
    }
}
