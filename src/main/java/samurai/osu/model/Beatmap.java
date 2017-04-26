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
package samurai.osu.model;

import samurai.osu.enums.GameMode;

/**
 * @author TonTL
 * @version 4.x - 3/6/2017
 */
public class Beatmap {

    private String difficulty, hash;
    private float ar, cs, hp, od;
    private int mapID, totalTime, drainTime, maxCombo;
    private GameMode gameMode;
    private double difficultyRating;

    public String getDifficulty() {
        return difficulty;
    }

    public Beatmap setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public Beatmap setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public float getAr() {
        return ar;
    }

    public Beatmap setAr(float ar) {
        this.ar = ar;
        return this;
    }

    public float getCs() {
        return cs;
    }

    public Beatmap setCs(float cs) {
        this.cs = cs;
        return this;
    }

    public float getHp() {
        return hp;
    }

    public Beatmap setHp(float hp) {
        this.hp = hp;
        return this;
    }

    public float getOd() {
        return od;
    }

    public Beatmap setOd(float od) {
        this.od = od;
        return this;
    }

    public int getMapID() {
        return mapID;
    }

    public Beatmap setMapID(int mapID) {
        this.mapID = mapID;
        return this;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public Beatmap setTotalTime(int totalTime) {
        this.totalTime = totalTime;
        return this;
    }

    public int getDrainTime() {
        return drainTime;
    }

    public Beatmap setDrainTime(int drainTime) {
        this.drainTime = drainTime;
        return this;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Beatmap setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        return this;
    }

    public double getDifficultyRating() {
        return difficultyRating;
    }

    public Beatmap setDifficultyRating(double difficultyRating) {
        this.difficultyRating = difficultyRating;
        return this;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public Beatmap setMaxCombo(int maxCombo) {
        this.maxCombo = maxCombo;
        return this;
    }
}