package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public class AdminAlert extends Notification {

    private final Kind kind;

    public AdminAlert(Match match, Kind kind, Instant timestamp) {
        super(timestamp, match);
        this.kind = kind;
    }

    public enum Kind {

        StandbyPlayersNotified;
    }

    public Kind getKind() {
        return kind;
    }

}
