package org.rrabarg.teamcaptain.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

/**
 * Class which exists for test purposes to fix the clock on an instant.
 */
@Configuration
@Profile("mutableclock")
public class MutableClockFactory {

    Logger log = LoggerFactory.getLogger(MutableClockFactory.class);

    private Clock testClock;

    @Bean
    @Scope("prototype")
    public Clock clock() {
        return testClock == null ? Clock.systemDefaultZone() : testClock;
    }

    public void fixInstant(Instant instant) {
        log.info("Mutable clock factory setting instant to " + instant);
        testClock = Clock.fixed(instant, ZoneId.systemDefault());
    }
}
