package org.rrabarg.teamcaptain.service.inmemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.service.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationInMemoryRepository implements NotificationRepository {

    static Logger log = LoggerFactory.getLogger(NotificationInMemoryRepository.class);

    List<Notification> playerNotifications = Collections.synchronizedList(new ArrayList<>());

    @Override
    public List<Notification> getPendingNotifications() {
        return new ArrayList<>(playerNotifications);
    }

    @Override
    public void removeAll(List<Notification> notifications) {
        playerNotifications.removeAll(notifications);
    }

    @Override
    public void add(Notification notification) {
        playerNotifications.add(notification);
    }

    @Override
    public void clear() {
        playerNotifications.clear();
        log.info("Cleared all notifications: " + playerNotifications);

    }
}
