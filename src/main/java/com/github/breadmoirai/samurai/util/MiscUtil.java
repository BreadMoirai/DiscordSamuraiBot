package com.github.breadmoirai.samurai.util;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscUtil {
    private static final Pattern URL = Pattern.compile("(?:<)?((?:http(s)?://.)?(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b(?:[-a-zA-Z0-9@:%_+.~#?&/=]*))(?:>)?");

    private MiscUtil() {

    }

    @Nullable
    public static String getAsUrl(String content) {
        final Matcher matcher = URL.matcher(content);
        if (matcher.matches()) {
            return matcher.group(1);
        } else return null;
    }
}
