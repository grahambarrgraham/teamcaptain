package org.rrabarg.teamcaptain.service;

import java.time.Clock;
import java.time.Instant;
import java.util.stream.Stream;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.domain.ReactorMessageKind;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.rrabarg.teamcaptain.domain.TeamCaptainNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.Reactor;
import reactor.event.Event;

@Component
public class OutboundNotificationService {

    @Autowired
    Reactor reactor;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    Provider<Clock> clock;

    public void playerNotification(Competition competition, Match match, Player player, NotificationKind kind) {
        final PlayerNotification notification = new PlayerNotification(competition, match, player, kind, now());

        match.getWorkflowState().setLastNotification(notification);

        reactor.notify(ReactorMessageKind.OutboundNotification,
                new Event<>(notification));

        if (kind.expectsResponse()) {
            notificationRepository.add(notification);
        }
    }

    public Stream<Notification> getPendingNotifications(Match match) {
        return notificationRepository.getPendingNotifications()
                .stream().filter(n -> n.getMatch().equals(match));
    }

    public void teamCaptainNotification(Competition competition, Match match, NotificationKind kind) {
        reactor.notify(ReactorMessageKind.OutboundNotification,
                new Event<>(new TeamCaptainNotification(competition, match, kind, now())));
    }

    private Instant now() {
        return clock.get().instant();
    }

}
