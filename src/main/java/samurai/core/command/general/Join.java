package samurai.core.command.general;

import samurai.core.command.Command;
import samurai.core.command.annotations.Client;
import samurai.core.command.annotations.Key;
import samurai.core.entities.FixedMessage;
import samurai.core.entities.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("join")
@Client
public class Join extends Command {

    @Override
    public SamuraiMessage buildMessage() {
        /*
        return new FixedMessage()
                .setMessage(new MessageBuilder()
                        .append("https://discord.gg/")
                        .append(client.getTextChannelById("274732231124320257")
                                .createInvite()
                                .setMaxAge(15L, TimeUnit.MINUTES)
                                .complete()
                                .getCode())
                        .build());
                        */
        return FixedMessage.build("https://discord.gg/yAMdGU9");
    }
}
