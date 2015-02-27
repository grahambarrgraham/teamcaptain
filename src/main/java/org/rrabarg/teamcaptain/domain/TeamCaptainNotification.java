package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public class TeamCaptainNotification extends Notification {

    private final Kind kind;

    public TeamCaptainNotification(Competition competition, Match match, Kind kind,
            Instant timestamp) {
        super(competition, timestamp, match);
        this.kind = kind;
    }

    public enum Kind {

        StandbyPlayersNotified, MatchFulfilled, InsufficientPlayers;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public TeamCaptain getTeamCaptain() {
        return competition.getTeamCaptain();
    }

    @Override
    public ContactDetail getTargetContact() {
        return getTeamCaptain().getContactDetail();
    }

}
