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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import samurai.database.Database;
import samurai.database.dao.ItemDao;
import samurai.items.DropTable;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JeopardyService implements QuickTimeEventService {


    private static final Color COLORDONE = new Color(57, 208, 57);

    @Override
    public QuizMessage provide() {
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(new URL("http://jservice.io/api/random?count=1").openStream(), StandardCharsets.UTF_8))) {
            final String result = rd.readLine();
            final JSONArray array = new JSONArray(result);
            final JSONObject clue = array.getJSONObject(0);
            final JSONObject category = clue.getJSONObject("category");
            final String question = clue.getString("question");
            if (question == null) return null;
            String answer = clue.getString("answer").toLowerCase();
            if (answer.startsWith("a ")) answer = answer.substring(2);
            final String finalAnswer = answer;
            final String name = category.getString("title");
            final DropTable dropTable = Database.get().<ItemDao, DropTable>openDao(ItemDao.class, itemDao -> itemDao.getDropTable(10));
            return new QuizMessage(
                    new JeopardyQuestionSupplier(name, question),
                    finalAnswer,
                    new JeopardyAnswerSupplier(name, question, finalAnswer),
                    dropTable);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
