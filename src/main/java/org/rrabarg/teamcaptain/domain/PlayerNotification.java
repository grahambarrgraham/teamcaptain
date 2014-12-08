package org.rrabarg.teamcaptain.domain;

import java.time.Clock;
import java.time.Instant;

public final class PlayerNotification {
    private final Match match;
    private final Player player;
    private final Kind kind;
    private final Instant timestamp;

    public PlayerNotification(Match match, Player player, Kind kind) {
        this.match = match;
        this.player = player;
        this.kind = kind;
        this.timestamp = Clock.systemDefaultZone().instant();
    }

    public enum Kind {

        CanYouPlay(true),
        Reminder(true),
        StandBy(true),
        StandDown(false),
        ConfirmationOfAcceptance(false),
        ConfirmationOfDecline(false);

        private final boolean expectsResponse;

        Kind(boolean expectsResponse) {
            this.expectsResponse = expectsResponse;
        }

        public boolean expectsResponse() {
            return expectsResponse;
        }
    }

    public Match getMatch() {
        return match;
    }

    public Player getPlayer() {
        return player;
    }

    public Kind getKind() {
        return kind;
    }

    public String getOrganiserFirstName() {
        return "Graham";
    };

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return player.getKey() + " : " + kind + " for " + match.getTitle();
    }
}
