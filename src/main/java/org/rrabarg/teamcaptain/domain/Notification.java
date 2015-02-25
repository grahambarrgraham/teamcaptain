package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public class Notification {

    protected final Match match;
    protected final Instant timestamp;
    private final PlayerPool playerPool;

    public Notification(PlayerPool playerPool, Instant timestamp, Match match) {
        this.playerPool = playerPool;
        this.timestamp = timestamp;
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public PlayerPool getPlayerPool() {
        return playerPool;
    }

}
