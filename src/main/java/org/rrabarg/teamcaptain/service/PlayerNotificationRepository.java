package org.rrabarg.teamcaptain.service;

import java.util.ArrayList;
import java.util.List;

import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerNotificationRepository {

    // trivial volatile single server implementation

    // considerations for multi-node system and no dedicated storage

    // could be stored with matches, but needs to be resurrected on machine start up

    // there's an option to use a distributed cache as optimisation

    List<PlayerNotification> playerNotifications = new ArrayList<>();

    public List<PlayerNotification> getPendingNotifications() {
        return new ArrayList<PlayerNotification>(playerNotifications);
    }

    public void removeAll(List<PlayerNotification> notifications) {
        playerNotifications.removeAll(notifications);
    }

    public void add(PlayerNotification notification) {
        playerNotifications.add(notification);
    }
}
