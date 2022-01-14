package com.ratelimiter.demo.ringbuffer;

import com.ratelimiter.demo.RateLimiterPolicy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.ratelimiter.demo.TimeProducer.now;

public class RingBufferRateLimiterPolicy implements RateLimiterPolicy {

    private ConcurrentHashMap<CharSequence, RingBufferLimiter> hashMap;
    private final TimeUnit unit;
    private final int windowSize;
    private final int numOfWindow;

    public RingBufferRateLimiterPolicy(TimeUnit unit,
                                       int windowSize,
                                       int numOfWindow) {
        this.hashMap = new ConcurrentHashMap<>();
        this.unit = unit;
        this.windowSize = windowSize;
        this.numOfWindow = numOfWindow;
    }

    public void addPolicy(CharSequence key, int limit) {
        RingBufferLimiter limiter = new RingBufferLimiter(limit, numOfWindow, windowSize, unit);
        hashMap.put(key, limiter);
    }

    @Override
    public boolean shouldThrottle(CharSequence key) {
        final long now = now();
        RingBufferLimiter limiter = hashMap.get(key);

        if (limiter == null) {
            return false;
        }

        if (limiter.limit == 0) {
            return false;
        }

        synchronized (limiter) {
            boolean withinWindow = limiter.isWithinWindow(now);
            int totalCount = withinWindow ? limiter.updateCountWithInSlidingWindow(now)
                    : limiter.updateCountOutOfSlidingWindow(now);
            return totalCount > limiter.limit;
        }
    }


    public void clear() {
        hashMap.clear();
    }
}
