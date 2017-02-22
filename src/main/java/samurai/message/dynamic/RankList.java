package samurai.message.dynamic;

import net.dv8tion.jda.core.entities.Message;
import samurai.data.SamuraiUser;
import samurai.message.DynamicMessage;
import samurai.message.modifier.Reaction;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/21/2017
 */
public class RankList extends DynamicMessage {


    private final int start;
    private final int end;
    private final ArrayList<SamuraiUser> userList;
    private int target;

    public RankList(int start, int end, int target, ArrayList<SamuraiUser> userList) {
        this.start = start;
        this.end = end;
        this.target = target;
        this.userList = userList;
    }

    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    public boolean valid(Reaction action) {
        return false;
    }

    @Override
    protected void execute(Reaction action) {

    }

    @Override
    public Consumer<Message> getConsumer() {
        return null;
    }

    @Override
    protected int getLastStage() {
        return -1;
    }
}
