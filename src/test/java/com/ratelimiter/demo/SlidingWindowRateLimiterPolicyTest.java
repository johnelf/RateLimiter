package com.ratelimiter.demo;

import com.ratelimiter.demo.slidingwindow.SlidingWindowRateLimiterPolicy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class SlidingWindowRateLimiterPolicyTest {

    private SlidingWindowRateLimiterPolicy policy;
    private static final String CLIENT_NAME = "OneClient";

    @Before
    public void setup() {
        policy = new SlidingWindowRateLimiterPolicy(TimeUnit.SECONDS, 1, 10);
    }

    @After
    public void teardown() {
        policy.clear();
    }

    @Test
    public void shouldThrottle() throws InterruptedException {
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

    private void sendRequests(int numOfReq) {
        for (int i = 0; i < numOfReq; i++) {
            policy.shouldThrottle(CLIENT_NAME);
        }
    }
}
