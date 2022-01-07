package com.traffic.demo;

public interface TrafficControlPolicy {
    boolean shouldThrottle(CharSequence key);

    void addPolicy(CharSequence key, int limit);

    Rate getRate(CharSequence key);
}
