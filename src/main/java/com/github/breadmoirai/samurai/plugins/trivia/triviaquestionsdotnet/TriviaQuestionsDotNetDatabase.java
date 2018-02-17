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

import com.github.breadmoirai.samurai.plugins.derby.JdbiExtension;
import org.jdbi.v3.core.Jdbi;

public class TriviaQuestionsDotNetDatabase extends JdbiExtension {

    public TriviaQuestionsDotNetDatabase(Jdbi jdbi) {
        super(jdbi);
        if (tableAbsent("TriviaQuestionsDotNet")) {
            execute("CREATE TABLE TriviaQuestionsDotNet (\n" +
                            "  Url        VARCHAR (384) PRIMARY KEY,\n" +
                            "  Question   VARCHAR (256) NOT NULL,\n" +
                            "  Answer     VARCHAR (128) NOT NULL,\n" +
                            "  Category   VARCHAR (32)  NOT NULL\n" +
                            ")");
            execute("CREATE TABLE TriviaQuestionsDotNetBlackList (\n" +
                            "  Url        VARCHAR (384) PRIMARY KEY\n" +
                            ")");
        }
    }

    public boolean isBlacklisted(String url) {
        return selectInt("SELECT 1 " +
                                 "FROM TriviaQuestionsDotNetBlackList " +
                                 "WHERE Url=?", url).isPresent();
    }

    public void addQuestion(TriviaQuestionsDotNetLine line) {
        execute("MERGE INTO TriviaQuestionsDotNet a \n" +
                        "USING TriviaQuestionsDotNetBlackList b " +
                        "ON a.Url = ? " +
                        "WHEN NOT MATCHED THEN INSERT values (?, ?, ?, ?)",
                line.getUrl(), line.getUrl(), line.getQuestion(), line.getAnswer(), line.getCategory());
    }

}
