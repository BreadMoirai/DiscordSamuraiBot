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
package samurai;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JeopardyService {

    public void getClue() {
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(new URL("http://jservice.io/api/random?count=1").openStream(), StandardCharsets.UTF_8))) {
            final String result = rd.readLine();
            final JSONObject jsonObject = new JSONObject(result);
            final int value = jsonObject.getInt("value");
            jsonObject.getJSONObject("category").getString()
        } catch (IOException | JSONException e) {
            return ;
        }
    }

}
