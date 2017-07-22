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
package net.breadmoirai.samurai.modules.items.items;

import com.typesafe.config.ConfigFactory;

import java.awt.*;

public enum ItemRarity {
    TRIVIAL(new Color(211, 227, 224)),
    MUNDANE(new Color(119, 255, 108)),
    UNCOMMON(new Color(58, 137, 252)),
    SALIENT(new Color(180, 95, 246)),
    SUIGENERIS(new Color(255, 178, 36)),
    PREMIER(new Color(250, 55, 55)),
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
                return TRIVIAL;
            case 2:
                return MUNDANE;
            case 3:
                return UNCOMMON;
            case 4:
                return SALIENT;
            case 5:
                return SUIGENERIS;
            case 6:
                return PREMIER;
            case 7:
                return EPOCHRIATIC;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case TRIVIAL:
                return "Trivial";
            case MUNDANE:
                return "Mundane";
            case UNCOMMON:
                return "Uncommon";
            case SALIENT:
                return "Salient";
            case SUIGENERIS:
                return "Sui Generis";
            case PREMIER:
                return "Premier";
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
