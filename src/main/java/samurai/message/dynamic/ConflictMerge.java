package samurai.message.dynamic;

import net.dv8tion.jda.core.entities.Message;
import samurai.data.SamuraiUser;
import samurai.message.DirectMessageArgs;
import samurai.message.DynamicMessage;
import samurai.message.modifier.Reaction;
import samurai.osu.Score;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/23/2017
 */
public class ConflictMerge extends DynamicMessage implements DirectMessageArgs {


    private final HashMap<String, LinkedList<Score>> uploadMap;
    private final SamuraiUser uploader;
    private HashMap<String, LinkedList<Score>> mergeMap;

    public ConflictMerge(HashMap<String, LinkedList<Score>> uploadMap, HashMap<String, LinkedList<Score>> mergeMap, SamuraiUser uploader) {
        this.uploadMap = uploadMap;
        this.mergeMap = mergeMap;
        this.uploader = uploader;
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
    public Consumer<Message> createConsumer() {
        return null;
    }

    @Override
    protected int getLastStage() {
        return 0;
    }
}
