package org.rrabarg.teamcaptain;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Class which exists for test purposes to fix the clock on an instant.
 */
@Configuration
public class TestClockFactory {

    private Clock testClock;

    @Bean
    @Scope("prototype")
    public Clock clock() {
        return testClock == null ? Clock.systemDefaultZone() : testClock;
    }

    public void fixInstant(Instant instant) {
        testClock = Clock.fixed(instant, ZoneId.systemDefault());
    }
}
