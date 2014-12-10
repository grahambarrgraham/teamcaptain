package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public final class PlayerNotification extends Notification {
    private final Player player;
    private final Kind kind;

    public PlayerNotification(Match match, Player player, Kind kind, Instant timestamp) {
        super(timestamp, match);
        this.player = player;
        this.kind = kind;
    }

    public enum Kind {

        CanYouPlay(true),
        Reminder(true),
        StandBy(true),
        StandDown(false),
        ConfirmationOfAcceptance(false),
        ConfirmationOfDecline(false),
        ConfirmationOfStandby(false);

        private final boolean expectsResponse;

        Kind(boolean expectsResponse) {
            this.expectsResponse = expectsResponse;
        }

        public boolean expectsResponse() {
            return expectsResponse;
        }
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

    @Override
    public String toString() {
        return player.getKey() + " : " + kind + " for " + match.getTitle() + " with timestamp " + timestamp;
    }
}
