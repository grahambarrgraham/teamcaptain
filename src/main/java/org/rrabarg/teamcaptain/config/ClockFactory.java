package org.rrabarg.teamcaptain.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockFactory {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
