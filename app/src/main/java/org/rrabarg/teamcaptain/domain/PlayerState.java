package org.rrabarg.teamcaptain.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerState {

    private volatile PlayerStatus playerStatus;
    private final Map<NotificationKind, Instant> notificationMap = new ConcurrentHashMap<>();

    public PlayerState(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public void setTimestampOfLastNotification(NotificationKind kind, Instant timestampOfLastNotification) {
        notificationMap.put(kind, timestampOfLastNotification);
    }

    public boolean wasPlayerNotifiedAtLeastADayAgo(Instant now, Set<NotificationKind> kinds) {
        return kinds.stream().map(kind -> wasPlayerNotifiedAtLeastADayAgo(now, kind)).allMatch(a -> a == true);
    }

    public boolean wasPlayerNotifiedAtLeastADayAgo(Instant now, NotificationKind kind) {

        if (playerStatus == PlayerStatus.None) {
            return false;
        }

        final Instant timestampOfLastNotification = getTimestampOfLastNotification(kind);

        if ((timestampOfLastNotification == null) || isAtLeastADayAgo(now, timestampOfLastNotification)) {
            return true;
        }

        return false;
    }

    private Instant getTimestampOfLastNotification(NotificationKind kind) {
        return notificationMap.get(kind);
    }

    private boolean isAtLeastADayAgo(Instant now, Instant timestamp) {
        return atLeastXDaysAgo(now, timestamp, 1);
    }

    private boolean atLeastXDaysAgo(Instant now, Instant timestamp, int i) {
        return Duration.between(timestamp, now).toDays() >= i;
    }

    public void setLastNotification(PlayerNotification notification) {
        setTimestampOfLastNotification(notification.getKind(), notification.getTimestamp());
    }

}