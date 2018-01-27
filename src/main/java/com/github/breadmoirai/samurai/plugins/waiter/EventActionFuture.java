package com.github.breadmoirai.samurai.plugins.waiter;

import java.util.concurrent.CompletableFuture;

import java.time.Instant;

interface EventActionFuture {


    boolean isWaiting();


    int getRunCount();

}
