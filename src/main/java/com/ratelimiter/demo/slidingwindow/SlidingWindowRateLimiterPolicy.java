package com.ratelimiter.demo.slidingwindow;

import com.ratelimiter.demo.RateLimiterPolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SlidingWindowRateLimiterPolicy implements RateLimiterPolicy {

    private Map<CharSequence, SlidingRate> slidingWindowMap;
    private final TimeUnit unit;
    private final int windowSize;
    private final int numOfWindow;

    public SlidingWindowRateLimiterPolicy(TimeUnit unit,
                                          int windowSize,
                                          int numOfWindow) {
        this.slidingWindowMap = new HashMap<>();
        this.unit = unit;
        this.windowSize = windowSize;
        this.numOfWindow = numOfWindow;
    }

    @Override
    public boolean shouldThrottle(CharSequence key) {
        SlidingRate slidingRate = slidingWindowMap.get(key);

        if (slidingRate == null) {
            return false;
        }

        long now = System.currentTimeMillis();
        int totalRates;
        synchronized (slidingRate) {
            SlidingRate.Window window = slidingRate.locateWindow(now);
            if (window == null) {
                return true;
            } else {
                ++window.count;
                slidingRate.incTotalRates();

                totalRates = slidingRate.getTotalRates();
            }
        }

        return totalRates > slidingRate.limit;
    }

    public void addPolicy(CharSequence key, int limit) {
        SlidingRate rate = new SlidingRate(numOfWindow, limit, windowSize, unit);
        slidingWindowMap.put(key, rate);
    }

    public void clear() {
        slidingWindowMap.clear();
    }
}
