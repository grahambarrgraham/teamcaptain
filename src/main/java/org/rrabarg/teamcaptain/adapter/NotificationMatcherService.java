package org.rrabarg.teamcaptain.adapter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.rrabarg.teamcaptain.domain.PlayerResponse;
import org.rrabarg.teamcaptain.domain.PlayerResponse.Kind;
import org.rrabarg.teamcaptain.service.PlayerNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationMatcherService {

    @Autowired
    PlayerNotificationRepository notificationRepository;

    final MessageKindAdapter messageKindAdapter;

    public NotificationMatcherService(MessageKindAdapter messageKindAdapter) {
        this.messageKindAdapter = messageKindAdapter;
    }

    public PlayerResponse getMatch(InboundMessage message) {
        final List<PlayerNotification> matchingNotifications = getMatchingNotifications(message);
        notificationRepository.removeAll(matchingNotifications);
        final PlayerNotification notification = getLatestNotification(matchingNotifications);
        return new PlayerResponse(
                notification.getMatch(),
                notification.getPlayer(),
                getKind(notification.getKind(), message.getBody()),
                message.getBody());
    }

    private List<PlayerNotification> getMatchingNotifications(InboundMessage message) {
        return notificationRepository.getPendingNotifications()
                .stream().parallel()
                .filter(notification -> matches(message, notification))
                .sorted(comparator(message))
                .collect(Collectors.toList());
    }

    private Comparator<? super PlayerNotification> comparator(InboundMessage data) {
        return (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp());
    }

    private Kind getKind(org.rrabarg.teamcaptain.domain.PlayerNotification.Kind kind, String data) {
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

    private PlayerNotification getLatestNotification(final List<PlayerNotification> matchingNotifications) {
        return matchingNotifications.get(0);
    }

    enum MatchStrength {
        High, Medium, Low, None;

        public boolean isAtLeast(MatchStrength strength) {
            return this.compareTo(strength) <= 0;
        }
    }

    public boolean matches(InboundMessage message, PlayerNotification playerNotification) {

        if (matchNotificationId(message, playerNotification).isAtLeast(MatchStrength.High)) {
            return true;
        }

        if (matchAddress(message, playerNotification).isAtLeast(MatchStrength.Medium) &&
                (matchKind(message, playerNotification).isAtLeast(MatchStrength.Medium))) {
            return true;
        }

        return false;
    }

    private MatchStrength matchKind(InboundMessage email, Notification playerNotification) {
        return MatchStrength.Medium;
    }

    private MatchStrength matchAddress(InboundMessage message, PlayerNotification playerNotification) {
        return getPlayerAddress(playerNotification).equalsIgnoreCase(message.getSourceIdentity()) ? MatchStrength.High
                : MatchStrength.None;
    }

    private String getPlayerAddress(PlayerNotification playerNotification) {
        return messageKindAdapter.getPlayerAddress(playerNotification.getPlayer());
    }

    private MatchStrength matchNotificationId(InboundMessage message, Notification playerNotification) {
        return MatchStrength.None;
    }

}
