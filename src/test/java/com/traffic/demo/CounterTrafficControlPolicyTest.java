package com.traffic.demo;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class CounterTrafficControlPolicyTest {

    @Test
    public void counterTrafficControlPolicyShouldNotThrottle() {
        TrafficControlPolicy policy = new CounterTrafficControlPolicy();
        final String clientName = "clientName";
        policy.addPolicy(clientName, 100);
        Rate rate = policy.getRate(clientName);
        rate.count = 90;

        assertFalse(policy.shouldThrottle(clientName));
    }
}
