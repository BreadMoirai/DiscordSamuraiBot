package samurai.command.manage;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import samurai.command.Command;
import samurai.command.CommandContext;
import samurai.command.CommandFactory;
import samurai.command.PrimitiveContext;
import samurai.command.annotations.Key;
import samurai.messages.base.SamuraiMessage;
import samurai.messages.impl.FixedMessage;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Key("schedule")
public class Schedule extends Command {

    private static final DateTimeFormatter DATE_TIME_FORMATTER;
    private static final Pattern DAY_SUFFIX = Pattern.compile("(st|nd|rd)");
    private static final Pattern MONTH_DAY = Pattern.compile("[a-zA-Z]+ [0-9]+");

    static {
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
                "[[MMMM][MMM][' ']d'th'[' ']]" +
                        "[h[':'mm[':'ss]][' ']a[' ']]");
    }


    @Override
    protected SamuraiMessage execute(CommandContext context) {
        final String[] lines = context.lines();
        if (lines.length == 1) return FixedMessage.build("Nothing was scheduled...");
        final List<TextChannel> mentionedChannels = context.getMentionedChannels();
        String args = lines[0];
        TextChannel targetChannel = context.getChannel();
        for (TextChannel mentionedChannel : mentionedChannels) {
            if (args.contains(mentionedChannel.getAsMention())) {
                targetChannel = mentionedChannel;
                args = args.replace(mentionedChannel.getAsMention(), "");
                break;
            }
        }
        if (!context.hasContent()) return null;
        final List<String> argList = CommandFactory.parseArgs(args);
        OffsetDateTime time = getDate(context.getContent(), context.getTime());
        if (time == null) {
            final Duration duration = getDuration(argList);
            if (duration.equals(Duration.ZERO)) {
                return FixedMessage.build("Scheduled Time could not be determined");
            }
            time = context.getTime().plus(duration);
        }
        final StringJoiner sj = new StringJoiner("\n");
        final PrimitiveContext prime = context.getSerializable();
        for (int i = 1; i < lines.length; i++) {
            context.getCommandScheduler().scheduleCommand(lines[i], prime, targetChannel.getIdLong(), time);
            sj.add(lines[i]);
        }
        return FixedMessage.build(new EmbedBuilder().setFooter("Task scheduled for", null).setTimestamp(time).setDescription(sj.toString()).setColor(context.getAuthor().getColor()).build());
    }

    public static Duration getDuration(List<String> args) {
        Duration duration = Duration.ZERO;
        final ListIterator<String> itr = args.listIterator();
        List<String> removal = new ArrayList<>(args.size());
        for (int i = 0; i < args.size() - 1; i++) {
            long value;
            final String s1 = args.get(i);
            if (CommandContext.isNumber(s1)) {
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
    public static OffsetDateTime getDate(String args, OffsetDateTime base) {
        args = WordUtils.capitalizeFully(args, '\u0000');
        args = args.replace("pm", "PM");
        args = args.replace("am", "AM");
        final Matcher monthDay = MONTH_DAY.matcher(args);
        if (monthDay.find()) {
            if (monthDay.start() == 0) {
                args = args.substring(0, monthDay.end()) + "th" + args.substring(monthDay.end());
            }
        }
        else
            args = DAY_SUFFIX.matcher(args).replaceAll("th");
        TemporalAccessor time;
        try {
            time = DATE_TIME_FORMATTER.parse(args);
        } catch (DateTimeParseException e) {
            return null;
        }
        base = base.with(new MyTemporalAdjuster(time));
        return base;
    }

    static class MyTemporalAdjuster implements TemporalAdjuster {

        private final TemporalAccessor into;

        MyTemporalAdjuster(TemporalAccessor into) {
            this.into = into;
        }

        @Override
        public Temporal adjustInto(Temporal temporal) {
            if (into.isSupported(ChronoField.SECOND_OF_DAY)) {
                temporal = temporal.with(ChronoField.NANO_OF_DAY, into.getLong(ChronoField.NANO_OF_DAY));
            } else {
                temporal = temporal.with(ChronoField.NANO_OF_DAY, 0);
            }
            if (into.isSupported(ChronoField.MONTH_OF_YEAR) && into.isSupported(ChronoField.DAY_OF_MONTH)) {
                temporal = temporal.with(ChronoField.MONTH_OF_YEAR, into.get(ChronoField.MONTH_OF_YEAR));
                temporal = temporal.with(ChronoField.DAY_OF_MONTH, into.get(ChronoField.DAY_OF_MONTH));
            } else {
                if (ChronoUnit.SECONDS.between(OffsetDateTime.now(), temporal) < 0) {
                    temporal = temporal.with(ChronoField.DAY_OF_YEAR, temporal.get(ChronoField.DAY_OF_YEAR) + 1);
                }
            }
            if (ChronoUnit.SECONDS.between(OffsetDateTime.now(), temporal) < 0) {
                temporal = temporal.with(ChronoField.YEAR, temporal.get(ChronoField.YEAR) + 1);
            }
            return temporal;
        }
    }
}