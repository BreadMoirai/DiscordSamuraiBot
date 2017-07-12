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

import java.io.Serializable;
import java.time.OffsetDateTime;

public class JeopardyQuestion implements Serializable {

    private String question;
    private String answer;
    private String answerMatch;
    private String category;
    private int value;
    private OffsetDateTime airdate;

    public JeopardyQuestion() {
    }

    public JeopardyQuestion(String question, String answer, String answerMatch, String category, int value, OffsetDateTime airdate) {
        this.question = question;
        this.answer = answer;
        this.answerMatch = answerMatch;
        this.category = category;
        this.value = value;
        this.airdate = airdate;
    }

    public boolean checkAnswer(String response) {
        return response.equalsIgnoreCase(answerMatch);
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getCategory() {
        return category;
    }

    public int getValue() {
        return value;
    }

    public OffsetDateTime getAirdate() {
        return airdate;
    }
}
