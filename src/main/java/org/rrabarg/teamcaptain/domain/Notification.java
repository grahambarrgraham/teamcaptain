package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public abstract class Notification {

    protected final Match match;
    protected final Instant timestamp;
    protected final Competition competition;

    public Notification(Competition competition, Instant timestamp, Match match) {
        this.competition = competition;
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
        return competition.getPlayerPool();
    }

    public TeamCaptain getTeamCaptain() {
        return competition.getTeamCaptain();
    }

    public abstract ContactDetail getTargetContact();

}
