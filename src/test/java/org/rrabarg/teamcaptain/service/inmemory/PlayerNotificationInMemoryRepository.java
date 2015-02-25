package org.rrabarg.teamcaptain.service.inmemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.rrabarg.teamcaptain.service.PlayerNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerNotificationInMemoryRepository implements PlayerNotificationRepository {

    static Logger log = LoggerFactory.getLogger(PlayerNotificationInMemoryRepository.class);

    // trivial volatile single server implementation
    // considerations for multi-node system and no dedicated storage
    // could be stored with matches, but needs to be resurrected on machine start up
    // there's an option to use a distributed cache as optimisation

    List<PlayerNotification> playerNotifications = Collections.synchronizedList(new ArrayList<>());

    /* (non-Javadoc)
     * @see org.rrabarg.teamcaptain.service.PlayerNotificationRepository#getPendingNotifications()
     */
    @Override
    public List<PlayerNotification> getPendingNotifications() {
        return new ArrayList<PlayerNotification>(playerNotifications);
    }

    /* (non-Javadoc)
     * @see org.rrabarg.teamcaptain.service.PlayerNotificationRepository#removeAll(java.util.List)
     */
    @Override
    public void removeAll(List<PlayerNotification> notifications) {
        log.debug("Removing pending notifications : " + notifications);
        playerNotifications.removeAll(notifications);
        log.debug("Remaining pending notifications : " + playerNotifications);
    }

    /* (non-Javadoc)
     * @see org.rrabarg.teamcaptain.service.PlayerNotificationRepository#add(org.rrabarg.teamcaptain.domain.PlayerNotification)
     */
    @Override
    public void add(PlayerNotification notification) {
        log.info("Added notification: " + notification);
        playerNotifications.add(notification);
        log.debug("Notifications now contains: " + playerNotifications);
    }

    /* (non-Javadoc)
     * @see org.rrabarg.teamcaptain.service.PlayerNotificationRepository#clear()
     */
    @Override
    public void clear() {
        log.info("Clearing all notifications (was): " + playerNotifications);
        playerNotifications.clear();
        log.info("Cleared all notifications: " + playerNotifications);

    }
}
