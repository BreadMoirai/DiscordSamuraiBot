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
package samurai.items;

public enum ItemType {
    VANITY, CONSUMABLE, CRATE, SEED, EGG, LAND, CRAFTING;


    @Override
    public String toString() {
        switch (this) {
            case VANITY: return "Vanity";
            case CONSUMABLE: return "Consumable";
            case CRATE: return "Crate";
            case SEED: return "Seed";
            case EGG: return "Egg";
            case LAND: return "Land";
            case CRAFTING: return "Crafting";
            default: return null;
        }
    }
}
