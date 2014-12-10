package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public class Notification {

    protected final Match match;
    protected final Instant timestamp;

    public Notification(Instant timestamp, Match match) {
        this.timestamp = timestamp;
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

}
