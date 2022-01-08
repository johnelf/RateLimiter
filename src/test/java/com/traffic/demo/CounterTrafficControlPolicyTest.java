package com.traffic.demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CounterTrafficControlPolicyTest {

    private TrafficControlPolicy policy;
    private static final String CLIENT_NAME = "clientName";

    @Before
    public void setup() {
        policy = new CounterTrafficControlPolicy();
    }

    @After
    public void teardown() {
        policy.clear();
    }

    @Test
    public void counterTrafficControlPolicyShouldNotThrottle() {
        policy.addPolicy(CLIENT_NAME, 100);
        Rate rate = policy.getRate(CLIENT_NAME);
        rate.count = 90;

        assertFalse(policy.shouldThrottle(CLIENT_NAME));
    }

    @Test
    public void shouldThrottle() {
        policy.addPolicy(CLIENT_NAME, 2);
        // Perform two operations before test
        policy.shouldThrottle(CLIENT_NAME);
        policy.shouldThrottle(CLIENT_NAME);

        ExecutorService es = Executors.newFixedThreadPool(3);
        List<Future<Boolean>> taskList = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            taskList.add(es.submit(() -> {
                boolean isThrottled = policy.shouldThrottle(CLIENT_NAME);
                System.out.println("isThrottled: " + isThrottled + " for " + CLIENT_NAME);

                return isThrottled;
            }));
        }

        for (int i = 0; i < 3; i++) {
            try {
                Boolean isThrottled = taskList.get(i).get();
                assertTrue(isThrottled);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void shouldNotThrottleWhenInNextWindow() throws InterruptedException {
        policy.addPolicy(CLIENT_NAME, 2);
        policy.shouldThrottle(CLIENT_NAME);
        policy.shouldThrottle(CLIENT_NAME);
        boolean shouldBeThrottled = policy.shouldThrottle(CLIENT_NAME);
        assertTrue(shouldBeThrottled);

        Thread.sleep(1000);
        boolean isThrottled = policy.shouldThrottle(CLIENT_NAME);
        assertFalse(isThrottled);
    }

}
