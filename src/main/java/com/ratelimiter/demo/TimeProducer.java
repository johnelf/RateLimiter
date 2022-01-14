package com.ratelimiter.demo;

import java.util.function.Function;

public class TimeProducer {

    /**
     * @return current time in milliseconds
     */
    public static long now() {
        return System.currentTimeMillis();
    }
}
