package samurai.util.wrappers;

import net.dv8tion.jda.core.entities.Message;

/**
 * @author TonTL
 * @version 3/14/2017
 */
public class SamuraiWrapper {

    public static MessageWrapper wrap(Message m) {
        return new MessageWrapper(m);
    }

}
