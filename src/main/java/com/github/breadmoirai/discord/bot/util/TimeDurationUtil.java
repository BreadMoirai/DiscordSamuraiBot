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
package com.github.breadmoirai.discord.bot.util;

import com.github.breadmoirai.discord.bot.framework.event.Arguments;
import org.jetbrains.annotations.Nullable;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeDurationUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "[[MMMM][MMM][' ']d'th'[' ']][M/d[' ']]" +
                    "[h[':'mm[':'ss]][' ']a[' ']]" +
                    "[z][0][x]");
    private static final Pattern DAY_SUFFIX = Pattern.compile("(?<=[0-9])(ST|ND|RD|TH)");
    private static final Pattern MONTH_DAY = Pattern.compile("[a-zA-Z]+ ([0-9]+?!(ST|ND|RD|TH))");

    /**
     * This will take a ListString and remove pairs of strings that correspond to seconds up to weeks.
     * Ex. When passing a list of {@code ["hello", "1", "5", "days", "3", "hours", "1 minute"]} will return a {@link java.time.Duration Duration} of 5 days, 3 hours. The list will be modified to contain {@code ["hello", "1", "1 minute"]}
     * Supported interpretations of time units are as follows
     * <ul>
     * <li>Seconds</li>
     * <ul>
     * <li>"s"</li>
     * <li>"sec"</li>
     * <li>"secs"</li>
     * <li>"second"</li>
     * <li>"seconds"</li>
     * </ul>
     * <li>Minutes</li>
     * <ul>
     * <li>"m"</li>
     * <li>"min"</li>
     * <li>"mins"</li>
     * <li>"minute"</li>
     * <li>"minutes"</li>
     * </ul>
     * <li>Hours</li>
     * <ul>
     * <li>"h"</li>
     * <li>"hour"</li>
     * <li>"hours"</li>
     * </ul>
     * <li>Days</li>
     * <ul>
     * <li>"d"</li>
     * <li>"day"</li>
     * <li>"days"</li>
     * </ul>
     * <li>Weeks</li>
     * <ul>
     * <li>"wk"</li>
     * <li>"week"</li>
     * <li>"weeks"</li>
     * </ul>
     * </ul>
     *
     * @param args a mutable list of strings
     * @return Duration
     */
    public static Duration getDuration(List<String> args) {
        Duration duration = Duration.ZERO;
        final ListIterator<String> itr = args.listIterator();
        List<String> removal = new ArrayList<>(args.size());
        for (int i = 0; i < args.size() - 1; i++) {
            long value;
            final String s1 = args.get(i);
            if (Arguments.isNumber(s1)) {
                value = Long.parseLong(s1);
            } else {
                continue;
            }
            final String s2 = args.get(++i);
            switch (s2.toLowerCase()) {
                case "s":
                case "sec":
                case "secs":
                case "second":
                case "seconds":
                    duration = duration.plusSeconds(value);
                    removal.add(s1);
                    removal.add(s2);
                    break;
                case "m":
                case "min":
                case "mins":
                case "minute":
                case "minutes":
                    duration = duration.plusMinutes(value);
                    removal.add(s1);
                    removal.add(s2);
                    break;
                case "h":
                case "hour":
                case "hours":
                    duration = duration.plusHours(value);
                    removal.add(s1);
                    removal.add(s2);
                    break;
                case "d":
                case "day":
                case "days":
                    duration = duration.plusDays(value);
                    removal.add(s1);
                    removal.add(s2);
                    break;
                case "wk":
                case "week":
                case "weeks":
                    duration = duration.plusDays(value * 7);
                    removal.add(s1);
                    removal.add(s2);
                    break;
                default:
            }
        }
        args.removeAll(removal);
        return duration;
    }

    /**
     * Format required is {@code Month dayth}
     *
     * @param args
     * @param base
     * @return
     */
    @Nullable
    public static Instant getDate(String args, OffsetDateTime base) {
        if (Character.isLetter(args.charAt(0))) {
            final int endIndex = args.indexOf(' ');
            if (endIndex <= 0) return null;
            args = capitalize(args.substring(0, endIndex)) + args.substring(endIndex).toUpperCase();
        } else {
            args = args.toUpperCase();
        }
        final Matcher monthDay = MONTH_DAY.matcher(args);
        if (monthDay.find()) {
            if (monthDay.start() == 0) {
                args = args.substring(0, monthDay.end() + 1) + "th" + args.substring(monthDay.end() + 1);
            }
        } else
            args = DAY_SUFFIX.matcher(args).replaceAll("th");
        TemporalAccessor time;
        try {
            time = DATE_TIME_FORMATTER.parse(args);
        } catch (DateTimeParseException e) {
            return null;
        }

        boolean hasDate = false;
        final LocalTime localTime;
        if (time.isSupported(ChronoField.NANO_OF_DAY)) {
            localTime = LocalTime.from(time);
        } else {
            localTime = LocalTime.MIDNIGHT;
        }
        LocalDate localDate;
        if (time.isSupported(ChronoField.MONTH_OF_YEAR) && time.isSupported(ChronoField.DAY_OF_MONTH)) {
            localDate = LocalDate.of(base.getYear(), Month.from(time), time.get(ChronoField.DAY_OF_MONTH));
            hasDate = true;
        } else if (time.isSupported(ChronoField.DAY_OF_MONTH)) {
            localDate = LocalDate.of(base.getYear(), base.getMonth(), time.get(ChronoField.DAY_OF_MONTH));
            if (localDate.isBefore(base.toLocalDate()) || (localDate.isEqual(base.toLocalDate()) && localTime.isBefore(base.toLocalTime()))) {
                localDate = localDate.withMonth(localDate.getMonth().plus(1).getValue());
            }
            hasDate = true;
        } else {
            localDate = base.toLocalDate();
        }
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        OffsetDateTime offsetDateTime;
        if (time.isSupported(ChronoField.OFFSET_SECONDS)) {
            offsetDateTime = localDateTime.atOffset(ZoneOffset.from(time));
        } else {
            try {
                offsetDateTime = localDateTime.atZone(ZoneId.from(time)).toOffsetDateTime();
            } catch (DateTimeException e) {
                return Instant.MIN;
            }
        }
        if (offsetDateTime.isBefore(base)) {
            if (hasDate)
                offsetDateTime = offsetDateTime.plusYears(1);
            else offsetDateTime = offsetDateTime.plusDays(1);
        } else if (!hasDate && offsetDateTime.minusDays(1).isAfter(base)) {
            offsetDateTime = offsetDateTime.minusDays(1);
        }
        return offsetDateTime.toInstant();
    }


    /**
     * This method has been copied from <a href="https://commons.apache.org/proper/commons-text/source-repository.html">Apache Commons</a>, <a href="https://git-wip-us.apache.org/repos/asf?p=commons-text.git;a=tree;f=src/main/java/org/apache/commons/text;hb=HEAD">org.apache.commons.text.WordUtils</a>
     *
     * @param str a string
     * @return a capitalized string, Each letter preceded by a space is capitalized
     */
    private static final String capitalize(String str) {
        str = str.toLowerCase();
        final int space = Character.codePointAt(new char[]{' '}, 0);
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;

        boolean capitalizeNext = true;
        for (int index = 0; index < strLen; ) {
            final int codePoint = str.codePointAt(index);

            if (codePoint == space) {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            } else if (capitalizeNext) {
                int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }
}
