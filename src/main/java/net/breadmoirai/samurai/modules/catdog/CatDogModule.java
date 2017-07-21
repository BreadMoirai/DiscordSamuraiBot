/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package net.breadmoirai.samurai.modules.catdog;


import net.breadmoirai.sbf.core.IModule;
import net.breadmoirai.sbf.core.SamuraiClient;
import net.breadmoirai.sbf.core.impl.CommandEngineBuilder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CatDogModule implements IModule {
    @Override
    public void init(CommandEngineBuilder ceb, SamuraiClient client) {
        ceb.registerCommand(CatDogCommand.class);
    }

    public String getRandomCatUrl() {
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(new URL("https://random.cat/meow").openStream(), StandardCharsets.UTF_8))) {
            return new JSONObject(rd.readLine()).getString("file");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRandomDogUrl() {
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(new URL("https://random.dog/woof").openStream(), StandardCharsets.UTF_8))) {
            return "https://random.dog/" + rd.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
