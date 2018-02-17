/*
 *     Copyright 2017-2018 Ton Ly
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
 */

package com.github.breadmoirai.samurai.util;

/**
 * Taken from <a href="http://www.sanfoundry.com/java-program-wagner-fischer-algorithm/"> sanfoundry.com</a>
 */
public class WagnerFischer {

    private WagnerFischer() {
    }

    public static int getLevenshteinDistance(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();
        int[][] arr = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++)
            arr[i][0] = i;
        for (int i = 1; i <= len2; i++)
            arr[0][i] = i;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int m = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
                arr[i][j] = Math.min(Math.min(arr[i - 1][j] + 1, arr[i][j - 1] + 1), arr[i - 1][j - 1] + m);
            }
        }
        return arr[len1][len2];
    }
}
