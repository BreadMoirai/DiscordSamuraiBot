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
package samurai.audio;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIContext;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import com.typesafe.config.ConfigFactory;

import java.util.List;

public class AiAPI {

    private static final String API_KEY;

    private static final AIDataService service;

    static {
        API_KEY = ConfigFactory.load().getString("api.ai");
        final AIConfiguration aiConfiguration = new AIConfiguration(API_KEY);
        service = new AIDataService(aiConfiguration);
    }

    public static AIResponse query(String query, List<AIContext> aiContexts) {
        try {
            service.addActiveContext(aiContexts);
            return service.request(new AIRequest(query));
        } catch (AIServiceException e) {
            e.printStackTrace();
            return null;
        }
    }
}
