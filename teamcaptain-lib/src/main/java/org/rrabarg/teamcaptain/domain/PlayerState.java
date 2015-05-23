package org.rrabarg.teamcaptain.domain;

import java.time.Instant;

public class PlayerState {

    private PlayerStatus playerStatus;
    private Instant timestampOfLastNotification;
    private NotificationKind kindOfLastNotification;

    public PlayerState(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public Instant getTimestampOfLastNotification() {
        return timestampOfLastNotification;
    }

    public void setTimestampOfLastNotification(Instant timestampOfLastNotification) {
        this.timestampOfLastNotification = timestampOfLastNotification;
    }

    public NotificationKind getKindOfLastNotification() {
        return kindOfLastNotification;
    }

    public void setKindOfLastNotification(NotificationKind kindOfLastNotification) {
        this.kindOfLastNotification = kindOfLastNotification;
    }

    public void setLastNotification(PlayerNotification notification) {
        this.timestampOfLastNotification = notification.getTimestamp();
        this.kindOfLastNotification = notification.getKind();
    }
}