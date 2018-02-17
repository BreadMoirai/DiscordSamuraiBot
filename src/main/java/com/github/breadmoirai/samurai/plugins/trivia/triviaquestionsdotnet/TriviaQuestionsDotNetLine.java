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

import com.github.breadmoirai.samurai.plugins.trivia.TriviaLine;
import com.github.breadmoirai.samurai.util.WagnerFischer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TriviaQuestionsDotNetLine implements TriviaLine {

    private static int DISTANCE = 2;
    private final String url;
    private String question;
    private String answer;
    private String category;
    private String next;
    private String checkedAnswer;

    public TriviaQuestionsDotNetLine(String url) {
        this.url = url;
    }

    @Override
    public String getQuestion() {
        if (question == null) {
            setFields();
        }
        return question;
    }

    private void setFields() {
        try {
            final Document document = Jsoup.connect(url).get();
            question = document.selectFirst("h1.single-title.entry-title").text();
            answer = document.selectFirst("p.post-answer + p").text();
            next = document.selectFirst("div.next-post-link > a").attr("href");
            category = document.selectFirst("meta[property=\"article:section\"]")
                               .attr("content")
                               .replace(" Trivia Questions", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkAnswer(String s) {
        if (checkedAnswer != null) {
            checkedAnswer = answer.toLowerCase();
            if (checkedAnswer.startsWith("the ")) {
                checkedAnswer = checkedAnswer.substring(4);
            }
            checkedAnswer = checkedAnswer.replaceAll("\\s+", " ")
                                         .replaceAll(",\\s*", " ")
                                         .replaceAll("&", "and");
        }
        return WagnerFischer.getLevenshteinDistance(answer, s) < DISTANCE;
    }

    @Override
    public String getAnswer() {
        return answer;
    }

    @Override
    public double getWeight() {
        return 0.1;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getSource() {
        return "http://www.trivia-questions.net";
    }

    public String getUrl() {
        return url;
    }

    public TriviaQuestionsDotNetLine getNext() {
        return new TriviaQuestionsDotNetLine(next);
    }

}
