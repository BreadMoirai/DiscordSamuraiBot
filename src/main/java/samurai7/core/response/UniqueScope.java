/*    Copyright 2017 Ton Ly
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package samurai7.core.response;

/**
 * <ul>
 *     <li>{@link samurai7.core.response.UniqueScope#AUTHOR_CHANNEL AUTHOR_CHANNEL}</li>
 *     <li>{@link samurai7.core.response.UniqueScope#AUTHOR_GUILD AUTHOR_GUILD}</li>
 *     <li>{@link samurai7.core.response.UniqueScope#CHANNEL CHANNEL}</li>
 *     <li>{@link samurai7.core.response.UniqueScope#GUILD GUILD}</li>
 * </ul>
 */
public enum UniqueScope {
    /**
     * One per author per channel
     */
    AUTHOR_CHANNEL,
    /**
     * One per author per guild
     */
    AUTHOR_GUILD,
    /**
     * One per channel
     */
    CHANNEL,
    /**
     * One per guild
     */
    GUILD}
