package com.ratelimiter.demo.slidingwindow;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class SlidingRate {
    public LinkedList<Window> rates;
    public int limit;
    private TimeUnit unit;
    private int windowSize;
    private int totalRates;
    private int numOfWindow;

    public SlidingRate(int numOfWindow,
                       int limit,
                       int windowSize,
                       final TimeUnit unit) {
        this.rates = new LinkedList<>();
        long startTimestamp = System.currentTimeMillis();
        for (int i = 0; i < numOfWindow; i++) {
            rates.addLast(new Window(startTimestamp + i * unit.toMillis(windowSize)));
        }
        this.numOfWindow = numOfWindow;
        this.unit = unit;
        this.windowSize = windowSize;
        this.limit = limit;
        this.totalRates = 0;
    }

    private void initRates(int windowSize, TimeUnit unit, long startTimestamp) {
        long windowEndTime = getWindowEndTime();
        long curIndex = (startTimestamp - windowEndTime) / unit.toMillis(windowSize);
        for (int i = 0; i <= curIndex; i++) {
            rates.addLast(new Window(startTimestamp + i * unit.toMillis(windowSize)));
        }

        Iterator<Window> iterator = rates.iterator();
        for (int i = 0; i <= curIndex; i++) {
            if (iterator.hasNext()) {
                Window window = iterator.next();
                iterator.remove();
                totalRates -= window.count;
            }
        }
    }

    public long getWindowStartTime() {
        return this.rates.getFirst().startTime;
    }

    public long getWindowEndTime() {
        return this.rates.getLast().startTime + unit.toMillis(windowSize);
    }

    public void incTotalRates() {
        this.totalRates++;
    }

    public int getTotalRates() {
        return this.totalRates;
    }

    public Window locateWindow(long now) {
        final long windowStartTime = this.getWindowStartTime();
        final long windowEndTime = this.getWindowEndTime();
        if (now >= windowStartTime && now < windowEndTime) {
            int offset = (int)((now - windowStartTime) / unit.toMillis(windowSize));
            Iterator<Window> iterator = this.rates.iterator();
            Window window = iterator.next();
            for (int i = 0; i < offset; i++) {
                window = iterator.next();
                iterator.remove();
            }
            for (int i = 0; i < offset; i++) {
                Window last = this.rates.getLast();
                this.rates.addLast(new Window(last.startTime + unit.toMillis(windowSize)));
            }
            return window;
        } else if (now > windowEndTime) {
            initRates(windowSize, unit, now);
            return locateWindow(now);
        } else {
            // Invalid use cases
            return null;
        }
    }

    static class Window {
        int count;
        final long startTime;

        Window(final long startTime) {
            this.count = 0;
            this.startTime = startTime;
        }
    }

}
