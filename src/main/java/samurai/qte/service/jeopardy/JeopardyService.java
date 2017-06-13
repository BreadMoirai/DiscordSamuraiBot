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
package samurai.qte.service.jeopardy;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import samurai.database.Database;
import samurai.database.dao.ItemDao;
import samurai.items.DropTable;
import samurai.qte.QuickTimeEventService;
import samurai.qte.QuizMessage;
import samurai.qte.service.jeopardy.JeopardyQuestion;
import samurai.qte.service.jeopardy.JeopardySupplier;

import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class JeopardyService implements QuickTimeEventService {

    @Override
    public QuizMessage provide() {
        final JSONArray array;
        try {
            array = Unirest.get("http://jservice.io/api/random")
                    .queryString("count", 15)
                    .asJson()
                    .getBody().getArray();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
        final int length = array.length();
        final JeopardySupplier js = new JeopardySupplier();
        for (int i = 0; i < length; i++) {
            final JSONObject clue = array.getJSONObject(i);
            if (!clue.isNull("invalid_count")
                    && clue.getInt("invalid_count") > 1) continue;
            if (clue.isNull("category")
                    || clue.isNull("question")
                    || clue.isNull("answer")
                    || clue.isNull("value"))
                continue;
            final JSONObject category = clue.getJSONObject("category");
            final String categoryName = category.getString("title");
            final String question = clue.getString("question");
            final OffsetDateTime airdate = OffsetDateTime.parse(clue.getString("airdate"));
            int value = clue.getInt("value");
            if (airdate.isBefore(OffsetDateTime.of(2001, Month.NOVEMBER.getValue(), 26, 0, 0, 0, 0, ZoneOffset.UTC)))
            value *= 2;
                String originalAnswer = clue.getString("answer");
            if (originalAnswer.contains("\\") || originalAnswer.contains("/") || originalAnswer.contains("(") || originalAnswer.contains(")") || originalAnswer.contains(","))
                continue;
            String answer = originalAnswer;
            if (originalAnswer.startsWith("<i>") && originalAnswer.endsWith("</i>")) {
                answer = originalAnswer.substring(3, originalAnswer.length() - 4);
                originalAnswer = "_" + answer + "_";
            }
            if (answer.startsWith("\"") && answer.endsWith("\"")) {
                answer = answer.substring(1, answer.length() - 1);
            }
            answer = answer.replace("!", "");
            answer = answer.replace("-", " ").trim();

            if (answer.startsWith("a ")) answer = answer.substring(2);
            else if (answer.startsWith("the ")) answer = answer.substring(4);
            else if (answer.startsWith("an ")) answer = answer.substring(3);
            answer = answer.replace("&", "and");
            js.addQuestion(new JeopardyQuestion(question, originalAnswer, answer, categoryName, value, airdate));
        }
        final DropTable dropTable = Database.get().<ItemDao, DropTable>openDao(ItemDao.class, itemDao -> itemDao.getDropTable(10));
        js.setDropTable(dropTable);
        return new QuizMessage(js);
    }
}
