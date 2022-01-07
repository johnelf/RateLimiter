package com.traffic.demo;

public class Rate {
    public final int limit;
    public long reset;
    public long count;

    public Rate(int limit) {
        this.limit = limit;
        this.reset = System.currentTimeMillis();
        this.count = 0;
    }
}
