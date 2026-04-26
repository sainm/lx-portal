package com.lx.portal.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Service;

@Service
public class LoginFailureService {
    private final Map<String, FailureCounter> failures = new ConcurrentHashMap<>();

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        failures.compute(username, (key, old) -> {
            FailureCounter counter = old == null ? new FailureCounter(0, Instant.now()) : old;
            return new FailureCounter(counter.count() + 1, Instant.now());
        });
    }

    public int failureCount(String username) {
        FailureCounter counter = failures.get(username);
        return counter == null ? 0 : counter.count();
    }

    record FailureCounter(int count, Instant lastFailureAt) {
    }
}

