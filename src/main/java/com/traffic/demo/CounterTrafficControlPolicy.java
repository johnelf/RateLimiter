package com.traffic.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CounterTrafficControlPolicy implements TrafficControlPolicy {

    private Map<CharSequence, Rate> controlMap;
    private final int RESOLUTION = 1; // per second

    public CounterTrafficControlPolicy() {
        controlMap = new HashMap<>();
    }

    public void addPolicy(CharSequence key, int limit) {
        controlMap.put(key, new Rate(limit));
    }

    @Override
    public Rate getRate(CharSequence key) {
        return controlMap.get(key);
    }

    @Override
    public boolean shouldThrottle(CharSequence key) {
        Rate rate = controlMap.get(key);

        if (rate == null) {
            return false; // No limit for this client
        }

        if (rate.limit == 0) {
            return false; // No limit for this client
        }

        double n;
        long now = System.currentTimeMillis();
        synchronized (rate) {
            if (rate.reset < now) {
                rate.reset = now + RESOLUTION / TimeUnit.SECONDS.toMillis(1);
                rate.count = 0;
            }

            n = (double)++rate.count;
            n /= RESOLUTION;
        }

        return n > rate.limit;
    }
}