package org.rrabarg.teamcaptain.service;

import java.util.List;

import org.rrabarg.teamcaptain.domain.PlayerNotification;

public interface PlayerNotificationRepository {

    List<PlayerNotification> getPendingNotifications();

    void removeAll(List<PlayerNotification> notifications);

    void add(PlayerNotification notification);

    void clear();

}