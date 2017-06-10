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

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.io.Serializable;
import java.util.function.Supplier;

public class JeopardyQuestionSupplier implements Supplier<Message>, Serializable
{
    private static final long serialVersionUID = 11L;
    private static final Color COLOR = new Color(164, 223, 218);
    private String name;
    private String question;

    public JeopardyQuestionSupplier() {}

    public JeopardyQuestionSupplier(String name, String question) {
        this.name = name;
        this.question = question;
    }

    @Override
    public Message get() {
        return new MessageBuilder().setEmbed(new EmbedBuilder().setTitle("Jeopardy - " + name).setDescription(question).setColor(COLOR).build()).build();
    }
}
