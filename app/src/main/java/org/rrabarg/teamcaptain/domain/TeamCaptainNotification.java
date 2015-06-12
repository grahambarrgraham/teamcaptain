package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public class TeamCaptainNotification extends Notification {

    public TeamCaptainNotification(Competition competition, Match match, NotificationKind kind,
            Instant timestamp) {
        super(competition, timestamp, match, kind);
    }

    @Override
    public TeamCaptain getTeamCaptain() {
        return competition.getTeamCaptain();
    }

    @Override
    public User getTarget() {
        return getTeamCaptain();
    }

}
