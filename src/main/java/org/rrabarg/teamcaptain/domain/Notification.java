package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public class Notification {

    protected final Match match;
    protected final Instant timestamp;
    private final PoolOfPlayers poolOfPlayers;

    public Notification(PoolOfPlayers poolOfPlayers, Instant timestamp, Match match) {
        this.poolOfPlayers = poolOfPlayers;
        this.timestamp = timestamp;
        this.match = match;

    }

    public Match getMatch() {
        return match;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public PoolOfPlayers getPoolOfPlayers() {
        return poolOfPlayers;
    }

}
