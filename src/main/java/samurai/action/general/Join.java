package samurai.action.general;

import samurai.action.Action;
import samurai.annotations.Client;
import samurai.annotations.Key;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

/**
 * @author TonTL
 * @version 4.2
 */
@Key("join")
@Client
public class Join extends Action {

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
