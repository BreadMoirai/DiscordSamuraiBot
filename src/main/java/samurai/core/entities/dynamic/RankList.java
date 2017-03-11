package samurai.core.entities.dynamic;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.core.Bot;
import samurai.core.entities.DynamicMessage;
import samurai.core.events.ReactionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author TonTL
 * @version 4.0
 * @since 2/21/2017
 */
public class RankList extends DynamicMessage {

    private static final List<String> REACTIONS = Collections.unmodifiableList(Arrays.asList("⬅", "\u23F9", "➡"));
    private static final int PAGE_SIZE = 10;

    private final int start;
    private final int end;
    private final ArrayList<String> nameList;
    private boolean stop;

    public RankList(int start, int end, ArrayList<String> nameList) {
        this.start = start;
        this.end = end;
        this.nameList = nameList;
        if (end - start == nameList.size()) {
            setStage(-1);
        } else {
            setStage(0);
        }
        stop = false;
    }

    @Override
    public Message getMessage() {
        StringBuilder sb = new StringBuilder().append("```md");
        if (getStage() == -1)
            for (int i = start; i < end; i++)
                sb.append("\n").append(nameList.get(i));
        else if (getStage() == 0) {
            sb.append("\n< Retrieving User List >");
        } else {
            int i;
            for (i = (getStage() - 1) * PAGE_SIZE; i < (getStage() * PAGE_SIZE > nameList.size() ? nameList.size() : getStage() * PAGE_SIZE); i++) {
                sb.append("\n").append(nameList.get(i));
            }
//            if (getStage() - i < 5) {
//                for (; i < nameList.size(); i++)
//                    sb.append("\n").append(nameList.get(i));
//            }
        }
        sb.append("\n```");
        MessageBuilder mb = new MessageBuilder();
        if (getStage() > 0) mb.append("Page ").append(getStage()).append(" of ").append(getLastStage() - 1);
        return mb.append(sb).build();
    }

    @Override
    public boolean valid(ReactionEvent action) {
        if (getStage() <= 0 || getStage() == getLastStage()) return false;
        if (!REACTIONS.contains(action.getName())) {
            return false;
        } else {
            int i = REACTIONS.indexOf(action.getName());
            if (i == 0 && getStage() == 1) {
                return false;
            } else if (i == 2 && getStage() == getLastStage() - 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void execute(ReactionEvent action) {
        switch (REACTIONS.indexOf(action.getName())) {
            case 0:
            case 2:
                nextStage();
                break;
            case 1:
                stop = true;
                break;
            default:
                Bot.log("Invalid Reaction Executed \n\tat: " + action.getChannelId() + " \n\tby: " + action.getUserId());
        }
    }

    @Override
    public Consumer<Message> createConsumer() {
        if (getStage() == 0) return newMenuConsumer(REACTIONS);
        else if (stop) return message -> {
            setStage(getLastStage());
            message.clearReactions().queue();
        };
        else if (getStage() == getLastStage()) return message -> message.clearReactions().queue();
        else return newEditConsumer();
    }

    @Override
    protected int getLastStage() {
        if (end - start == nameList.size()) {
            return 0;
        } else {
            return nameList.size() / PAGE_SIZE + 1;
        }
    }

}
