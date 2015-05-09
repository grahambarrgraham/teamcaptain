package org.rrabarg.teamcaptain.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!mutableclock")
public class SystemClockFactory {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
