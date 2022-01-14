package com.ratelimiter.demo;

import com.ratelimiter.demo.ringbuffer.RingBufferRateLimiterPolicy;
import com.ratelimiter.demo.slidingwindow.SlidingWindowRateLimiterPolicy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RingBufferRateLimiterPolicyTest {
    private RingBufferRateLimiterPolicy policy;
    private static final String CLIENT_NAME = "OneClient";

    @Before
    public void setup() {
        policy = new RingBufferRateLimiterPolicy(TimeUnit.SECONDS, 1, 3);
    }

    @After
    public void teardown() {
        policy.clear();
    }

    @Test
    public void shouldThrottleInTwoSeconds() throws InterruptedException {
        policy.addPolicy(CLIENT_NAME, 5);

        Thread.sleep(500);
        sendRequests(5);
        // Assuming that the consequent requests only took few ms to finish
        // and in the next second surge requests would be throttled given we
        // are using sliding window
        Thread.sleep(500);
        boolean shouldThrottle = policy.shouldThrottle(CLIENT_NAME);
        assertTrue(shouldThrottle);
    }

    @Test
    public void shouldThrottleWhenStillInSlidingWindow() throws InterruptedException {
        policy.addPolicy(CLIENT_NAME, 5);

        sendRequests(5);

        Thread.sleep(2);

        boolean shouldThrottle = policy.shouldThrottle(CLIENT_NAME);
        assertTrue(shouldThrottle);
    }

    @Test
    public void shouldNotThrottleWhenNoInSlidingWindow() throws InterruptedException {
        policy.addPolicy(CLIENT_NAME, 5);

        sendRequests(5);

        Thread.sleep(3000);

        assertFalse(policy.shouldThrottle(CLIENT_NAME));
    }

    private void sendRequests(int numOfReq) {
        for (int i = 0; i < numOfReq; i++) {
            policy.shouldThrottle(CLIENT_NAME);
        }
    }
}
