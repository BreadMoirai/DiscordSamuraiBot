/*
 *       Copyright 2017 Ton Ly (BreadMoirai)
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
package samurai.qte.service.jeopardy;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import samurai.command.CommandContext;
import samurai.items.DropTable;
import samurai.messages.base.SamuraiMessage;
import samurai.qte.QuickTimeEventSupplier;

import java.awt.*;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;

public class JeopardySupplier implements QuickTimeEventSupplier, Serializable {

    private static final long serialVersionUID = 10L;
    private static final int THRESHOLD = 1500;

    private static final Color COLOR1 = new Color(164, 223, 218);
    private static final Color COLOR2 = new Color(57, 208, 57);

    private Queue<JeopardyQuestion> questions;
    private DropTable dropTable;
    private int totalValue;
    private int current;
    private int count;
    private transient int temp;

    public JeopardySupplier() {
        questions = new ArrayDeque<>();
        current = 1;
        count = 1;
    }


    @Override
    public MessageEmbed getQuestion() {
        final JeopardyQuestion peek = questions.peek();
        return new EmbedBuilder().setTitle("Jeopardy: " + peek.getCategory()).setDescription(peek.getQuestion()).setColor(COLOR1).setFooter(String.format("Question %d/%d | Value %d", current, count, peek.getValue()), null).setTimestamp(peek.getAirdate()).build();
    }

    @Override
    public MessageEmbed getAnswer() {
        final JeopardyQuestion peek = questions.peek();
        return new EmbedBuilder().setTitle("Jeopardy: " + peek.getCategory()).setDescription(peek.getQuestion()).addField("Answer", peek.getAnswer(), true).setColor(COLOR2).setTimestamp(peek.getAirdate()).build();
    }

    @Override
    public boolean checkAnswer(CommandContext context) {
        String response = context.getContent().toLowerCase();
        if (response.startsWith("the ")) response = response.substring(4);
        if (response.startsWith("a ")) response = response.substring(2);
        if (response.startsWith("an ")) response = response.substring(3);
        response = response.replace("&", "and");
        if (questions.peek().checkAnswer(response)) {
            current++;
            context.getChannel().sendMessage(getAnswer()).queue();
            final JeopardyQuestion q = questions.poll();
            final int value = q.getValue();
            totalValue += value;
            context.getAuthorPoints().offsetPoints(value);
            context.getChannel().sendMessage("**" + context.getAuthor().getEffectiveName() + "** has been awarded **" + value + "** points.").queue();
            return true;
        }
        return false;
    }

    @Override
    public boolean canProvide() {
        return totalValue < THRESHOLD && !questions.isEmpty();
    }

    @Override
    public void markInvalid() {
        questions.poll();
    }

    @Override
    public SamuraiMessage getReward() {
        return dropTable.createDrop(10, Duration.ofDays(1));
    }

    public void addQuestion(JeopardyQuestion jeopardyQuestion) {
        temp += jeopardyQuestion.getValue();
        if (temp < THRESHOLD) {
            count++;
        }
        questions.add(jeopardyQuestion);
    }

    public void setDropTable(DropTable dropTable) {
        this.dropTable = dropTable;
    }
}
