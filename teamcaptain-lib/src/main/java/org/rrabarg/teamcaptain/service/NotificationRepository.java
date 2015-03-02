package org.rrabarg.teamcaptain.service;

import java.util.List;

import org.rrabarg.teamcaptain.domain.Notification;

public interface NotificationRepository {

    List<Notification> getPendingNotifications();

    void removeAll(List<Notification> notifications);

    void add(Notification notification);

    void clear();

}