package org.rrabarg.teamcaptain.service;

import static reactor.event.selector.Selectors.$;

import java.time.Clock;
import java.time.Instant;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Provider;

import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.MatchWorkflow;
import org.rrabarg.teamcaptain.domain.Notification;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.rrabarg.teamcaptain.domain.PlayerResponse;
import org.rrabarg.teamcaptain.domain.TeamCaptainNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;

@Component
public class NotificationService implements Consumer<Event<PlayerResponse>> {

    @Autowired
    Reactor reactor;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    Provider<Clock> clock;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.InboundNotification), this);
    }

    public void playerNotification(Competition competition, Match match, Player player, NotificationKind kind) {
        final Notification notification = new PlayerNotification(competition, match, player, kind, now());

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

    @Override
    public void accept(Event<PlayerResponse> playerResponse) {
        final MatchWorkflow workflow = workflowService.getWorkflow(playerResponse.getData().getMatch());
        if (workflow != null) {
            workflow.notify(playerResponse);
        }
    }

    public void teamCaptainNotification(Competition competition, Match match, NotificationKind kind) {
        reactor.notify(ReactorMessageKind.OutboundNotification,
                new Event<>(new TeamCaptainNotification(competition, match, kind, now())));
    }

    private Instant now() {
        return clock.get().instant();
    }

}
