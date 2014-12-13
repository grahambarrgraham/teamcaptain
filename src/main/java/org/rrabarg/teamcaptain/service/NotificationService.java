package org.rrabarg.teamcaptain.service;

import static reactor.event.selector.Selectors.$;

import java.time.Clock;
import java.time.Instant;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Provider;

import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.rrabarg.teamcaptain.domain.AdminAlert;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.MatchWorkflow;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.rrabarg.teamcaptain.domain.PlayerNotification.Kind;
import org.rrabarg.teamcaptain.domain.PlayerResponse;
import org.rrabarg.teamcaptain.domain.PoolOfPlayers;
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
    PlayerNotificationRepository notificationRepository;

    @Autowired
    Provider<Clock> clock;

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.InboundPlayerResponse), this);
    }

    public void notify(PoolOfPlayers poolOfPlayers, Match match, Player player, Kind kind) {
        final PlayerNotification notification = new PlayerNotification(poolOfPlayers, match, player, kind, now());

        reactor.notify(ReactorMessageKind.OutboundPlayerNotification,
                new Event<>(notification));

        if (kind.expectsResponse()) {
            notificationRepository.add(notification);
        }
    }

    public Stream<PlayerNotification> getPendingNotifications(Match match) {
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

    public void adminAlert(PoolOfPlayers pool, Match match, AdminAlert.Kind kind) {
        reactor.notify(ReactorMessageKind.OutboundAdminAlert, new Event<>(new AdminAlert(pool, match, kind, now())));
    }

    private Instant now() {
        return clock.get().instant();
    }

}
