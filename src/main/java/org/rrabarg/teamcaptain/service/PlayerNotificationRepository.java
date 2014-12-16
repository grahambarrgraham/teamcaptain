package org.rrabarg.teamcaptain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerNotificationRepository {

    static Logger log = LoggerFactory.getLogger(PlayerNotificationRepository.class);

    // trivial volatile single server implementation
    // considerations for multi-node system and no dedicated storage
    // could be stored with matches, but needs to be resurrected on machine start up
    // there's an option to use a distributed cache as optimisation

    List<PlayerNotification> playerNotifications = Collections.synchronizedList(new ArrayList<>());

    public List<PlayerNotification> getPendingNotifications() {
        return new ArrayList<PlayerNotification>(playerNotifications);
    }

    public void removeAll(List<PlayerNotification> notifications) {
        log.debug("Removing pending notifications : " + notifications);
        playerNotifications.removeAll(notifications);
        log.debug("Remaining pending notifications : " + playerNotifications);
    }

    public void add(PlayerNotification notification) {
        log.info("Added notification: " + notification);
        playerNotifications.add(notification);
        log.debug("Notifications now contains: " + playerNotifications);
    }

    public void clear() {
        log.info("Clearing all notifications (was): " + playerNotifications);
        playerNotifications.clear();
        log.info("Cleared all notifications: " + playerNotifications);

    }
}
