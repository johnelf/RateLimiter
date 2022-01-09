package com.traffic.demo;

public interface RateLimiterPolicy {
    boolean shouldThrottle(CharSequence key);

    void addPolicy(CharSequence key, int limit);

    Rate getRate(CharSequence key);

    void clear();
}
