package com.github.breadmoirai.samurai.database;

import org.junit.Test;

import java.util.ArrayList;

public class TESTSTS {

    @Test
    public void test() {
        final ArrayList<String> strings = new ArrayList<>();
        strings.add("one");
        strings.add("two");
        for (String string : strings) {
            strings.add(string);
        }
        System.out.println("strings = " + strings);
    }
}
