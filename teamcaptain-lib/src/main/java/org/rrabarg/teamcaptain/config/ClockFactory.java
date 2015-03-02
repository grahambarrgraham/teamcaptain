package org.rrabarg.teamcaptain.config;

import java.time.Clock;

public class ClockFactory {

    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
