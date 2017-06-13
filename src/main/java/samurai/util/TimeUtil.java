/*
 *       Copyright 2017 Ton Ly
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

package samurai.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class TimeUtil {

    public static String format(Duration d, ChronoUnit precision) {
        if (d.isNegative()) return "the past";
        final long total = d.getSeconds();
        final StringBuilder sb = new StringBuilder();
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        int weeks = 0;
        switch (precision) {
            case SECONDS:
                seconds = (int) (total % 60);
            case MINUTES:
                minutes = (int) (total / 60 % 60);
            case HOURS:
                hours = (int) (total / 3600 % 24);
            case DAYS:
                days = (int) (total / 86400 % 7);
            case WEEKS:
                weeks = (int) (total / 604800);
                break;
            default:
                throw new IllegalArgumentException("precision must be of SECONDS, MINUTES, HOURS, DAYS, or WEEKS");
        }
        if (weeks != 0) {
            sb.append(weeks).append(" weeks&");
        }
        if (days != 0) {
            sb.append(days).append(" days&");
        }
        if (hours != 0) {
            sb.append(hours).append(" hours&");
        }
        if (minutes != 0) {
            sb.append(minutes).append(" minutes&");
        }
        if (seconds != 0) {
            sb.append(seconds).append(" seconds&");
        }
        final int i = sb.lastIndexOf("&");
        sb.replace(i, i+1, ".");
        final int j = sb.lastIndexOf("&");
        if (j == -1) return sb.toString();
        sb.replace(j, j+1, " and ");
        return sb.toString().replace("&", ", ");
    }


}
