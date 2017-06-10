/*
 *       Copyright 2017 Ton Ly
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package samurai.qte;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import samurai.SamuraiDiscord;
import samurai.command.CommandContext;
import samurai.command.basic.GenericCommand;
import samurai.items.DropTable;
import samurai.items.Item;
import samurai.messages.annotations.GhostMessage;
import samurai.messages.base.DynamicMessage;
import samurai.messages.base.Reloadable;
import samurai.messages.impl.RedPacketDrop;

import java.time.Duration;
import java.util.Random;
import java.util.function.Supplier;

@GhostMessage
public class QuizMessage extends DynamicMessage implements Reloadable, GenericCommandListener {

    private static final long serialVersionUID = 10L;

    private static final int ITEM_DROP_COUNT = 40;
    private static final transient Random RAND = new Random();

    private transient QuickTimeEventController qteController;


    private Supplier<Message> question;
    private String answer;
    private Supplier<Message> complete;
    private DropTable reward;

    public QuizMessage() {
    }

    QuizMessage(JeopardyQuestionSupplier question, String answer, JeopardyAnswerSupplier complete, DropTable reward) {
        this.question = question;
        this.answer = answer;

        this.complete = complete;
        this.reward = reward;
    }


    @Override
    protected Message initialize() {
        return new MessageBuilder(question.get()).append("<@&322810785527234561>").build();
    }

    @Override
    protected void onReady(Message message) {
        //do nothing
    }

    @Override
    public void onGenericCommand(GenericCommand command) {
        final CommandContext context = command.getContext();
        if (context.getKey().equalsIgnoreCase("answer")) {
            if (answer.equalsIgnoreCase(context.getContent())) {
                context.getChannel().sendMessage(complete.get()).queue();
                final Item drop = reward.getDrop();
                context.getAuthorInventory().addItem(drop);
                context.getChannel().sendMessage(String.format("**%s** has been rewarded a %s", context.getAuthor().getEffectiveName(), drop.print())).queue();
                getManager().submit(createDrop());
                unregister();
                if (qteController != null) {
                    qteController.onCompletion();
                }
            } else {
                context.getChannel().addReactionById(context.getMessageId(), context.getClient().getEmoteById(312373404286320640L)).queue();
            }
        } else if (context.getKey().equalsIgnoreCase("question")) {
            context.getChannel().sendMessage(question.get()).queue();
        } else if (context.getKey().equalsIgnoreCase("invalid") && context.getAuthor().canInteract(context.getSelfMember())) {
            unregister();
            context.getChannel().deleteMessageById(getMessageId()).queue();
            if (qteController != null) {
                qteController.onCompletion();
            }
        }
    }

    private RedPacketDrop createDrop() {
        final TIntIntHashMap map = new TIntIntHashMap();
        for (int i = 0; i < ITEM_DROP_COUNT; i++) {
            map.adjustOrPutValue(reward.getDropId(), 1, 1);
        }
        final int size = map.size();
        final int[] drops = new int[size * 2];
        final TIntIntIterator itr = map.iterator();
        final TIntArrayList dropQueueList = new TIntArrayList();
        int i = 0;
        while (itr.hasNext()) {
            itr.advance();
            final int itemId = itr.key();
            drops[i++] = itemId;
            final int count = itr.value();
            drops[i++] = count;
            for (int j = 0; j < count; j++) {
                dropQueueList.add(itemId);
            }
        }
        dropQueueList.shuffle(RAND);
        final RedPacketDrop redPacketDrop = new RedPacketDrop(Duration.ofDays(1), drops, dropQueueList.toArray());
        redPacketDrop.setChannelId(getChannelId());
        return redPacketDrop;
    }

    @Override
    public void reload(SamuraiDiscord samuraiDiscord) {
        qteController = samuraiDiscord.getQuickTimeEventController();
    }

    public void setQteController(QuickTimeEventController qteController) {
        this.qteController = qteController;
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
