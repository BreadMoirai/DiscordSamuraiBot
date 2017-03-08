package samurai.action.util;

import samurai.action.Action;
import samurai.annotations.Client;
import samurai.annotations.Key;
import samurai.message.FixedMessage;
import samurai.message.SamuraiMessage;

import java.time.OffsetDateTime;

import static java.time.temporal.ChronoUnit.MILLIS;


/**
 * @author TonTL
 * @version 4.0
 * @since 2/21/2017
 */
@Key("ping")
@Client
public class Ping extends Action {

    @Override
    protected SamuraiMessage buildMessage() {
        OffsetDateTime receiveTime = client.getTextChannelById(String.valueOf(channelId)).getMessageById(String.valueOf(messageId)).complete().getCreationTime();
        return FixedMessage.build("Calculating Ping...").setConsumer(message -> message.editMessage(String.format("Pung! %dms", MILLIS.between(receiveTime, message.getCreationTime()))).queue());
    }
}