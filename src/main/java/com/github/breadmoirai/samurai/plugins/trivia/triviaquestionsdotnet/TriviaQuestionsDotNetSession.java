/*
 *     Copyright 2017-2018 Ton Ly
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
 */

package com.github.breadmoirai.samurai.plugins.trivia.triviaquestionsdotnet;

import com.github.breadmoirai.samurai.plugins.trivia.TriviaSession;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.util.Deque;
import java.util.Optional;

public class TriviaQuestionsDotNetSession implements TriviaSession {

    private final Deque<TriviaQuestionsDotNetLine> questions;
    private final TriviaQuestionsDotNetDatabase database;
    private final int size;
    private TriviaQuestionsDotNetLine current;
    private String selfAvatar;
    private int i;

    public TriviaQuestionsDotNetSession(Deque<TriviaQuestionsDotNetLine> questions,
                                        TriviaQuestionsDotNetDatabase database) {
        this.questions = questions;
        size = questions.size();
        this.database = database;
        i = 0;
    }

    @Override
    public MessageEmbed getQuestion() {
        final EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0, 121, 214))
             .setTitle(String.format("**%s**", current.getQuestion()))
             .setAuthor(current.getCategory())
             .setFooter(String.format("SamuraiGames\u2122 | Trivia-Questions.net | Question %d/%d", i, size),
                        selfAvatar);
        return embed.build();
    }

    @Override
    public boolean checkAnswer(String ans) {
        return current.checkAnswer(ans);
    }

    @Override
    public MessageEmbed getAnswer(boolean success) {
        final EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(success ? new Color(78, 235, 48) : new Color(203, 66, 0))
             .setTitle(String.format("**%s**", current.getQuestion()))
             .setAuthor(current.getCategory())
             .addField("ANSWER", current.getAnswer(), false)
             .setFooter(String.format("SamuraiGames\u2122 | Trivia-Questions.net | Question %d/%d", i, size),
                        selfAvatar);
        return embed.build();
    }

    @Override
    public boolean hasNext() {
        return !questions.isEmpty();
    }

    @Override
    public MessageEmbed nextQuestion() {
        current = questions.pop();
        final String url = current.getUrl();
        final Optional<TriviaQuestionsDotNetLine> line = database.getLine(url);
        if (line.isPresent()) {
            current = line.get();
        } else {
            current.setDatabase(database);
        }
        i++;
        return getQuestion();
    }

    @Override
    public void setIcon(String url) {
        selfAvatar = url;
    }

}
