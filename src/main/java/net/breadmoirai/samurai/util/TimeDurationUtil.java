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
package net.breadmoirai.samurai.util;

import net.breadmoirai.sbf.core.CommandEvent;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import java.time.*;
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

    public static Duration getDuration(List<String> args) {
        Duration duration = Duration.ZERO;
        final ListIterator<String> itr = args.listIterator();
        List<String> removal = new ArrayList<>(args.size());
        for (int i = 0; i < args.size() - 1; i++) {
            long value;
            final String s1 = args.get(i);
            if (CommandEvent.isNumber(s1)) {
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

    @Nullable
    public static Instant getDate(String args, OffsetDateTime base) {
        if (Character.isLetter(args.charAt(0))) {
            final int endIndex = args.indexOf(' ');
            if (endIndex <= 0) return null;
            args = WordUtils.capitalizeFully(args.substring(0, endIndex)) + args.substring(endIndex).toUpperCase();
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
}
