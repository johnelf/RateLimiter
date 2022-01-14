package com.ratelimiter.demo.ringbuffer;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.ratelimiter.demo.TimeProducer.now;

public class RingBufferLimiter {
    public final int limit;
    private final int numOfWindow;
    private final int windowSize;
    private final TimeUnit unit;
    private final long windowSpan;
    private final long doubleSlidingWindow;

    private ArrayList<Window> ring;
    private int totalCount;
    private int startWindowIndex;

    public RingBufferLimiter(int limit,
                             int numOfWindow,
                             int windowSize,
                             TimeUnit unit) {
        this.limit = limit;
        this.numOfWindow = numOfWindow;
        this.windowSize = windowSize;
        this.unit = unit;
        this.ring = new ArrayList<>(numOfWindow);
        this.windowSpan = windowSpan();
        this.totalCount = 0;
        final long now = now();
        for (int i = 0; i < numOfWindow; i++) {
            Window window = new Window(now + i * windowSpan);
            ring.add(i, window);
        }
        startWindowIndex = 0;
        doubleSlidingWindow = numOfWindow * windowSpan * 2;
    }

    private long windowSpan() {
        return unit.toMillis(windowSize);
    }

    public boolean isWithinWindow(final long now) {
        Window start = ring.get(startWindowIndex);

        assert start != null;

        if (now < start.startTimestamp) {
            throw new InvalidParameterException();
        }

        return now <= (start.startTimestamp + numOfWindow * windowSpan);
    }

    /**
     * @param now, current timestamp in milliseconds
     * @return the total count in current sliding window
     *
     * should only be invoked when input timestamp is within current sliding window
     */
    public int updateCountWithInSlidingWindow(final long now) {
        Window start = ring.get(startWindowIndex);

        int locatedIndex = (int)((now - start.startTimestamp) / windowSpan);

        if (locatedIndex < 0 || locatedIndex > numOfWindow - 1) {
            throw new InvalidParameterException();
        } else {
            Window window = ring.get(locatedIndex);
            ++window.count;
            ++totalCount;
        }

        return totalCount;
    }

    /**
     * @param now, current timestamp in milliseconds
     * @return the total count in current sliding window
     *
     * should only be invoked when input timestamp is beyond current sliding window
     */
    public int updateCountOutOfSlidingWindow(final long now) {
        Window start = ring.get(startWindowIndex);

        long gap = now - start.startTimestamp;
        if (gap <= doubleSlidingWindow) {
            int newStart = (int)((gap / windowSpan) % numOfWindow);
            for (int i = 0; i <= newStart; i++) {
                Window window = ring.get(i);
                totalCount -= window.count;
            }
            startWindowIndex = newStart + 1;
            Window currentWindow = ring.get(newStart);
            ++currentWindow.count;
            ++totalCount;
        } else {
            for (int i = 0; i < numOfWindow - 1; i++) {
                ring.add(i, new Window(now + i * windowSpan));
            }
            totalCount = 0;
            startWindowIndex = 0;
            Window startWindow = ring.get(startWindowIndex);
            ++startWindow.count;
            ++totalCount;
        }

        return totalCount;
    }

    public static class Window {
        public int count;
        public final long startTimestamp;

        public Window(long startTimestamp) {
            this.count = 0;
            this.startTimestamp = startTimestamp;
        }

    }

}
