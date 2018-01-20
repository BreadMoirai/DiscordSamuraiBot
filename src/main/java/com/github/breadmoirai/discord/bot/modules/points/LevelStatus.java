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
package com.github.breadmoirai.discord.bot.modules.points;

public class LevelStatus {

    double points;
    double exp;
    int level;

    public static LevelStatus fromSession(PointSession session) {
        return new LevelStatus(session.getPoints(), session.getExp(), calculateLevel(session.getExp()));
    }

    public LevelStatus(double points, double exp, int level) {
        this.points = points;
        this.exp = exp;
        this.level = level;
    }

    double getExpRequiredForNextLevel() {
        return expAtLevel(level + 1) - exp;
    }

    public double getExpAtThisLevel() {
        return exp - expAtLevel(level);
    }

    public double getTotalExpRequiredForLevel() {
        return expAtLevel(level + 1) - expAtLevel(level);
    }

    public double getExpProgress() {
        return getExpAtThisLevel() / getTotalExpRequiredForLevel();
    }

    public static int calculateLevel(double exp) {
        int lvl = (int) exp / 500000 * 100;
        double expsmall = exp % 500000;
        if (expsmall < 55000) {
            return lvl + preTwenty(expsmall);
        } else {
            return lvl + postTwenty(expsmall);
        }
    }

    private static int postTwenty(double exp) {
        return (int) (exp / 5625 + 11);
    }

    private static int preTwenty(double exp) {
        return (int) Math.pow(0.0159344 * exp, 0.448430493);
    }

    private static double levelexp(int level) {
        level %= 100;
        return level < 20 ? Math.pow(level, 2.26) / 0.0159344 : (level - 11) * 5625;
    }

    public static double expAtLevel(int level) {
        return 500000 * (level / 100) + levelexp(level);
    }

    public double getPoints() {
        return points;
    }

    public double getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }
}
