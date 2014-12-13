package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public class AdminAlert extends Notification {

    private final Kind kind;

    public AdminAlert(PoolOfPlayers pool, Match match, Kind kind, Instant timestamp) {
        super(pool, timestamp, match);
        this.kind = kind;
    }

    public enum Kind {

        StandbyPlayersNotified, MatchFulfilled;
    }

    public Kind getKind() {
        return kind;
    }

}
