package org.rrabarg.teamcaptain.channel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.rrabarg.teamcaptain.domain.Channel;
import org.rrabarg.teamcaptain.domain.ContactDetail;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.User;
import org.rrabarg.teamcaptain.service.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationMatcherService {

    @Autowired
    NotificationRepository notificationRepository;

    public MatcherResult findMatches(Message message) {

        final List<Notification> matchingNotifications = getMatchingNotifications(message);

        // move this to workflow? - life-cycle of notifications should be in one place
        notificationRepository.removeAll(matchingNotifications);

        if (!matchingNotifications.isEmpty()) {

            final Notification notification = getMostRecent(matchingNotifications);

            final MatcherResultItem item = new MatcherResultItem(notification.getTarget(), notification.getMatch(),
                    matchingNotifications);

            return new MatcherResult(Arrays.asList(item));
        }

        return MatcherResult.nomatch();
    }

    private List<Notification> getMatchingNotifications(Message message) {
        return notificationRepository.getPendingNotifications()
                .stream().parallel()
                .filter(notification -> matches(message, notification))
                .sorted(comparator(message))
                .collect(Collectors.toList());
    }

    private Comparator<? super Notification> comparator(Message data) {
        return (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp());
    }

    enum MatcherStrength {
        High, Medium, Low, None;

        public boolean isAtLeast(MatcherStrength strength) {
            return this.compareTo(strength) <= 0;
        }
    }

    public boolean matches(Message message, Notification playerNotification) {

        if (matchNotificationId(message, playerNotification).isAtLeast(MatcherStrength.High)) {
            return true;
        }

        if (matchAddress(message, playerNotification).isAtLeast(MatcherStrength.Medium) &&
                (matchKind(message, playerNotification).isAtLeast(MatcherStrength.Medium))) {
            return true;
        }

        return false;
    }

    private MatcherStrength matchKind(Message message, Notification notification) {
        return MatcherStrength.Medium;
    }

    private MatcherStrength matchAddress(Message message, Notification notification) {
        return getIdentity(notification.getTargetContactDetail(), message.getChannel()).equalsIgnoreCase(
                message.getSourceIdentity()) ? MatcherStrength.High
                : MatcherStrength.None;
    }

    private String getIdentity(ContactDetail targetContactDetail, Channel channel) {
        return targetContactDetail.getIdentity(channel);
    }

    private MatcherStrength matchNotificationId(Message message, Notification playerNotification) {
        return MatcherStrength.None; // to be implemented
    }

    private static Notification getMostRecent(List<Notification> matchingNotifications) {
        return matchingNotifications.get(0);
    }

    public static class MatcherResult {

        private final List<MatcherResultItem> matcherResultItems;

        MatcherResult(List<MatcherResultItem> matcherResultItems) {
            this.matcherResultItems = matcherResultItems;
        }

        public static MatcherResult nomatch() {
            return new MatcherResult(Collections.emptyList());
        }

        public MatcherResultItem getMostLikelyMatch() {
            return matcherResultItems.get(0);
        }

        public boolean isMatchUnambiguous() {
            return matcherResultItems.size() == 1;
        }

        public List<MatcherResultItem> getAllMatcherResults() {
            return matcherResultItems;
        }

        public boolean hasMatches() {
            return !matcherResultItems.isEmpty();
        }
    }

    public class MatcherResultItem {
        private final User user;
        private final Match match;
        private final List<Notification> matchingNotifications;

        MatcherResultItem(User user, Match match) {
            this(user, match, null);
        }

        MatcherResultItem(User user, Match match, List<Notification> notifications) {
            this.user = user;
            this.match = match;
            this.matchingNotifications = notifications;
        }

        public User getUser() {
            return user;
        }

        public Match getMatch() {
            return match;
        }

        public List<Notification> getNotification() {
            return matchingNotifications;
        }

        public NotificationKind getKind() {
            if (getLatestNotification() != null) {
                return getLatestNotification().getKind();
            }
            return null;
        }

        private Notification getLatestNotification() {
            return matchingNotifications.get(0);
        }

    }

}
