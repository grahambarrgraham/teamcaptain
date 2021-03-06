package org.rrabarg.teamcaptain.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.rrabarg.teamcaptain.domain.Email;
import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.rrabarg.teamcaptain.domain.PlayerResponse;
import org.rrabarg.teamcaptain.domain.PlayerResponse.Kind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationMatcherService {

    @Autowired
    PlayerNotificationRepository notificationRepository;

    public PlayerResponse getMatch(Email email) {
        final List<PlayerNotification> matchingNotifications = getMatchingNotifications(email);
        notificationRepository.removeAll(matchingNotifications);
        final PlayerNotification notification = getLatestNotification(matchingNotifications);
        return new PlayerResponse(
                notification.getMatch(),
                notification.getPlayer(),
                getKind(notification.getKind(), email),
                email.getBody());
    }

    private List<PlayerNotification> getMatchingNotifications(Email email) {
        return notificationRepository.getPendingNotifications()
                .stream().parallel()
                .filter(notification -> matches(email, notification))
                .sorted(comparator(email))
                .collect(Collectors.toList());
    }

    private Comparator<? super PlayerNotification> comparator(Email data) {
        return (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp());
    }

    private Kind getKind(org.rrabarg.teamcaptain.domain.PlayerNotification.Kind kind, Email data) {
        switch (kind) {
        case CanYouPlay:
        case Reminder:
            if (data.getBody().toLowerCase().contains("yes")) {
                return Kind.ICanPlay;
            }
            if (data.getBody().toLowerCase().contains("no")) {
                return Kind.ICantPlay;
            }
        case StandBy:
            if (data.getBody().toLowerCase().contains("yes")) {
                return Kind.ICanStandby;
            }
            if (data.getBody().toLowerCase().contains("no")) {
                return Kind.ICantPlay;
            }
        default:
            return Kind.Information;
        }
    }

    private PlayerNotification getLatestNotification(final List<PlayerNotification> matchingNotifications) {
        return matchingNotifications.get(0);
    }

    enum MatchStrength {
        High, Medium, Low, None;

        public boolean isAtLeast(MatchStrength strength) {
            return this.compareTo(strength) <= 0;
        }
    }

    public boolean matches(Email email, PlayerNotification playerNotification) {

        if (matchNotificationId(email, playerNotification).isAtLeast(MatchStrength.High)) {
            return true;
        }

        if (matchAddress(email, playerNotification).isAtLeast(MatchStrength.Medium) &&
                (matchKind(email, playerNotification).isAtLeast(MatchStrength.Medium))) {
            return true;
        }

        return false;
    }

    private MatchStrength matchKind(Email email, Notification playerNotification) {
        return MatchStrength.Medium;
    }

    private MatchStrength matchAddress(Email email, PlayerNotification playerNotification) {
        return playerNotification.getPlayer().getEmailAddress().equalsIgnoreCase(email.getFromAddress()) ? MatchStrength.High
                : MatchStrength.None;
    }

    private MatchStrength matchNotificationId(Email email, Notification playerNotification) {
        return MatchStrength.None;
    }

}
