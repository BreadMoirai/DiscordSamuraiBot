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
package com.github.breadmoirai.samurai.plugins.games.connect4.strategy;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author TonTL
 * @version 4/16/2017
 */
public class MiniMaxStrategy implements ConnectFourStrategy {

    private final int xBound;
    private final int yBound;
    private final int depth;
    private final int xCenter;

    public MiniMaxStrategy(int xBound, int yBound, int depth) {
        this.xBound = xBound;
        this.yBound = yBound;
        this.depth = depth + 1;
        this.xCenter = xBound / 2;
    }

    @Contract(pure = true)
    @Override
    public int makeMove(char[][] board) {
        int bestCol = -1;
        double value = Integer.MIN_VALUE;
        for (int x = 0; x < board.length; x++) {
            int y = height(board[x]);
            if (y == -1) continue;
            final double i = minimax(x, y, 'b', board, 1) - (2 * (Math.abs(xCenter - x) + 1));
            //System.out.println("col: " + (x + 1) + " = " + i);
            if (i >= value) {
                bestCol = x;
                value = i;
            }
        }
        //System.out.println("Chose col: " + (bestCol + 1) + "\n");
        return bestCol;
    }

    @Contract(pure = true)
    private int height(char[] column) {
        for (int i = 0; i < column.length; i++) {
            if (column[i] == '\u0000') return i;
        }
        return -1;
    }

    @Contract(pure = true)
    private double minimax(int x, int y, char p, char[][] board, int depth) {
        char opp = p == 'b' ? 'a' : 'b';
        if (!canPlay(x, y, board)) {
            return 0;
        }
        if (depth == this.depth) {
            return 50;
        }
        if (isWinningMove(x, y, p, board)) {
            return p == 'b' ? 1000.0 / depth : -1000.0 / depth;
        } else if (isWinningMove(x, y, opp, board)) {
            return p == 'b' ? 500.0 / depth : -500.0 / depth;
        } else {
            final char[][] testBoard = testMove(x, y, p, board);
            return IntStream.range(0, xBound).parallel().mapToDouble(testX -> minimax(testX, height(testBoard[testX]), opp, testBoard, depth + 1)).average().orElse(0.0);
        }
    }

    @Contract(pure = true)
    private int moveCount(char[][] board) {
        return Arrays.stream(board).flatMapToInt(chars -> {
            int[] ints = new int[chars.length];
            for (int i = 0; i < chars.length; i++) {
                ints[i] = chars[i];
            }
            return Arrays.stream(ints);
        }).filter(value -> value != '\u0000').map(operand -> 1).sum();
    }

    @Contract(pure = true)
    private boolean isWinningMove(int x, int y, char p, char[][] board) {
        return hasEnded(testMove(x, y, p, board));
    }

    @Contract(pure = true)
    private boolean canPlay(int x, int y, char[][] board) {
        if (x >= xBound || y >= yBound || x < 0 || y < 0) {
            return false;
        }
        return board[x][y] == '\u0000';
    }

    private char[][] testMove(int x, int y, char p, char[][] board) {
        final char[][] chars = Arrays.stream(board).map(char[]::clone).toArray(char[][]::new);
        chars[x][y] = p;
        return chars;
    }


    @Contract(pure = true)
    private boolean hasEnded(char[][] board) {
        for (int x = 0; x < xBound; x++) {
            for (int y = 0; y < yBound; y++) {

                char token = board[x][y];
                if (token != '\u0000') {

                    //checks horizontal right
                    if (x < xBound - 3) {
                        for (int i = 1; i < 4; i++) {
                            if (board[x + i][y] != token) {
                                break;
                            } else if (i == 3) {
                                return true;
                            }
                        }
                    }

                    //checks diagonal up right
                    if (x < xBound - 3 && y < yBound - 3) {
                        for (int i = 1; i < 4; i++) {
                            if (board[x + i][y + i] != token) {
                                break;
                            } else if (i == 3) {
                                return true;
                            }
                        }
                    }

                    //checks vertical up
                    if (y < yBound - 3) {
                        for (int i = 1; i < 4; i++) {
                            if (board[x][y + i] != token) {
                                break;
                            } else if (i == 3) {
                                return true;
                            }
                        }
                    }

                    //checks diagonal up left
                    if (x > 2 && y < yBound - 3) {
                        for (int i = 1; i < 4; i++) {
                            if (board[x - i][y + i] != token) {
                                break;
                            } else if (i == 3) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}