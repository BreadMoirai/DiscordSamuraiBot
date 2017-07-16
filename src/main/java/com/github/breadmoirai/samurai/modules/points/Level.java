package com.github.breadmoirai.samurai.modules.points;

public class Level {

    public int getLevelRewardId(int level) {
        switch (level % 100) {
            case 1: return 111;
            case 2: return 111;
            case 3: return 111;
            case 4: return 111;
            case 5: return 111;
            case 6: return 111;
            case 7: return 111;
            case 8: return 111;
            case 9: return 111;
            case 10: return 111;
            case 11: return 111;
            case 12: return 111;
            case 13: return 111;
            case 14: return 111;
            case 15: return 111;
            case 16: return 111;
            case 17: return 111;
            case 18: return 111;
            case 19: return 111;
            case 20: return 111;
            case 21: return 111;
            case 22: return 111;
            case 23: return 111;
            case 24: return 111;
            case 25: return 111;
            case 26: return 111;
            case 27: return 111;
            case 28: return 111;
            case 29: return 111;
            case 30: return 111;
            case 31: return 111;
            case 32: return 111;
            case 33: return 111;
            case 34: return 111;
            case 35: return 111;
            case 36: return 111;
            case 37: return 111;
            case 38: return 111;
            case 39: return 111;
            case 40: return 111;
            case 41: return 111;
            case 42: return 111;
            case 43: return 111;
            case 44: return 111;
            case 45: return 111;
            case 46: return 111;
            case 47: return 111;
            case 48: return 111;
            case 49: return 111;
            case 50: return 111;
            case 51: return 111;
            case 52: return 111;
            case 53: return 111;
            case 54: return 111;
            case 55: return 111;
            case 56: return 111;
            case 57: return 111;
            case 58: return 111;
            case 59: return 111;
            case 60: return 111;
            case 61: return 111;
            case 62: return 111;
            case 63: return 111;
            case 64: return 111;
            case 65: return 111;
            case 66: return 111;
            case 67: return 111;
            case 68: return 111;
            case 69: return 111;
            case 70: return 111;
            case 71: return 111;
            case 72: return 111;
            case 73: return 111;
            case 74: return 111;
            case 75: return 111;
            case 76: return 111;
            case 77: return 111;
            case 78: return 111;
            case 79: return 111;
            case 80: return 111;
            case 81: return 111;
            case 82: return 111;
            case 83: return 111;
            case 84: return 111;
            case 85: return 111;
            case 86: return 111;
            case 87: return 111;
            case 88: return 111;
            case 89: return 111;
            case 90: return 111;
            case 91: return 111;
            case 92: return 111;
            case 93: return 111;
            case 94: return 111;
            case 95: return 111;
            case 96: return 111;
            case 97: return 111;
            case 98: return 111;
            case 99: return 111;
            case 0: return 111;
            default: return 0;
        }
    }

    public static int calculateLevel(double exp) {
        int lvl = (int) (((long) exp) / 500000 * 100);
        double expsmall = exp % 500000;
        if (expsmall < 50000) {
            return lvl + preTwenty(expsmall);
        } else {
            return lvl + postTwenty(expsmall);
        }
    }

    private static int postTwenty(double x) {
        return (int) (x / 5625 + 100 / 9);
    }

    private static int preTwenty(double x) {
        return (int) Math.pow(0.0159344 * x, 0.448430493);
    }
}
