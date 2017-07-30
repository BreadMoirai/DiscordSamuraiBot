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
package net.breadmoirai.samurai.modules.item.model.data;

import com.typesafe.config.ConfigFactory;

import java.awt.*;

public enum ItemRarity {
    COMMON(new Color(211, 227, 224)),
    UNCOMMON(new Color(119, 255, 108)),
    RARE(new Color(58, 137, 252)),
    EPIC(new Color(180, 95, 246)),
    LEGENDARY(new Color(255, 178, 36)),
    SUIGENERIS(new Color(250, 55, 55)),
    EPOCHRIATIC(new Color(255, 141, 195));

    private Color color;
    private String emote;

    ItemRarity(Color color) {
        this.color = color;
        this.emote = ConfigFactory.load("items").getString("rarity." + ordinal());
    }

    public Color getColor() {
        return color;
    }

    public static ItemRarity valueOf(short rarity) {
        switch (rarity) {
            case 1:
                return COMMON;
            case 2:
                return UNCOMMON;
            case 3:
                return RARE;
            case 4:
                return EPIC;
            case 5:
                return LEGENDARY;
            case 6:
                return SUIGENERIS;
            case 7:
                return EPOCHRIATIC;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case COMMON:
                return "Common";
            case UNCOMMON:
                return "Uncommon";
            case RARE:
                return "Rare";
            case EPIC:
                return "Epic";
            case LEGENDARY:
                return "Legendary";
            case SUIGENERIS:
                return "Suigeneris";
            case EPOCHRIATIC:
                return "Epochriatic";
            default:
                return null;
        }
    }

    public String getEmote() {
        return emote;
    }
}
