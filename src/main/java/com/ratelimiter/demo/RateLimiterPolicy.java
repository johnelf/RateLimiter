package com.ratelimiter.demo;

public interface RateLimiterPolicy<T> {
    boolean shouldThrottle(CharSequence key);
}
