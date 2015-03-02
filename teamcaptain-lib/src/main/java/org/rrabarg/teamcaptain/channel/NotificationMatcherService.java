package org.rrabarg.teamcaptain.channel;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.rrabarg.teamcaptain.domain.Channel;
import org.rrabarg.teamcaptain.domain.ContactDetail;
import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.rrabarg.teamcaptain.domain.PlayerResponse;
import org.rrabarg.teamcaptain.domain.PlayerResponse.Kind;
import org.rrabarg.teamcaptain.service.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationMatcherService {

    @Autowired
    NotificationRepository notificationRepository;

    public PlayerResponse getMatch(Message message) {
        final List<Notification> matchingNotifications = getMatchingNotifications(message);
        notificationRepository.removeAll(matchingNotifications);
        final Notification notification = getLatestNotification(matchingNotifications);

        if ((notification != null) && (notification instanceof PlayerNotification)) {
            return new PlayerResponse(
                    notification.getMatch(),
                    ((PlayerNotification) notification).getPlayer(),
                    getKind(notification.getKind(), message.getBody()),
                    message.getBody());
        }
        return null;
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

    private Kind getKind(NotificationKind kind, String data) {
        switch (kind) {
        case CanYouPlay:
        case Reminder:
            if (data.toLowerCase().contains("yes")) {
                return Kind.ICanPlay;
            }
            if (data.toLowerCase().contains("no")) {
                return Kind.ICantPlay;
            }
        case StandBy:
            if (data.toLowerCase().contains("yes")) {
                return Kind.ICanStandby;
            }
            if (data.toLowerCase().contains("no")) {
                return Kind.ICantPlay;
            }
        default:
            return Kind.Information;
        }
    }

    private Notification getLatestNotification(final List<Notification> matchingNotifications) {
        return matchingNotifications.get(0);
    }

    enum MatchStrength {
        High, Medium, Low, None;

        public boolean isAtLeast(MatchStrength strength) {
            return this.compareTo(strength) <= 0;
        }
    }

    public boolean matches(Message message, Notification playerNotification) {

        if (matchNotificationId(message, playerNotification).isAtLeast(MatchStrength.High)) {
            return true;
        }

        if (matchAddress(message, playerNotification).isAtLeast(MatchStrength.Medium) &&
                (matchKind(message, playerNotification).isAtLeast(MatchStrength.Medium))) {
            return true;
        }

        return false;
    }

    private MatchStrength matchKind(Message message, Notification notification) {
        return MatchStrength.Medium;
    }

    private MatchStrength matchAddress(Message message, Notification notification) {
        return getIdentity(notification.getTargetContactDetail(), message.getChannel()).equalsIgnoreCase(
                message.getSourceIdentity()) ? MatchStrength.High
                : MatchStrength.None;
    }

    private String getIdentity(ContactDetail targetContactDetail, Channel channel) {
        return targetContactDetail.getIdentity(channel);
    }

    private MatchStrength matchNotificationId(Message message, Notification playerNotification) {
        return MatchStrength.None;
    }

}
