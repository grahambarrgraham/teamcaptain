package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public final class PlayerNotification extends Notification {

    private final Player player;

    public PlayerNotification(Competition competition, Match match, Player player, NotificationKind kind,
            Instant timestamp) {
        super(competition, timestamp, match, kind);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public User getTarget() {
        return player;
    }
}