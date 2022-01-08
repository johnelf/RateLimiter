package com.traffic.demo;

import java.util.concurrent.TimeUnit;

public class Rate {
    final int limit;
    long reset;
    public long count;

    Rate(int limit, long RESOLUTION) {
        this.limit = limit;
        this.reset = System.currentTimeMillis() + RESOLUTION * TimeUnit.SECONDS.toMillis(1);;
        this.count = 0;
    }
}
