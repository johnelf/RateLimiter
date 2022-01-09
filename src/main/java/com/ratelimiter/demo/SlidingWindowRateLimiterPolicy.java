package com.ratelimiter.demo;

public class SlidingWindowRateLimiterPolicy implements RateLimiterPolicy {
    @Override
    public boolean shouldThrottle(CharSequence key) {
        return false;
    }

    @Override
    public void addPolicy(CharSequence key, int limit) {

    }

    @Override
    public Rate getRate(CharSequence key) {
        return null;
    }

    @Override
    public void clear() {

    }
}
