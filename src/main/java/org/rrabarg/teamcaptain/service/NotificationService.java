package org.rrabarg.teamcaptain.service;

import static reactor.event.selector.Selectors.$;

import javax.annotation.PostConstruct;

import org.rrabarg.teamcaptain.config.ReactorMessageKind;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.MatchWorkflow;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerNotification;
import org.rrabarg.teamcaptain.domain.PlayerNotification.Kind;
import org.rrabarg.teamcaptain.domain.PlayerResponse;
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

    @PostConstruct
    public void configure() {
        reactor.on($(ReactorMessageKind.InboundPlayerResponse), this);
    }

    public void notify(Match match, Player player, Kind kind) {
        final PlayerNotification notification = new PlayerNotification(match, player, kind);

        reactor.notify(ReactorMessageKind.OutboundPlayerNotification,
                new Event<>(notification));

        if (kind.expectsResponse()) {
            notificationRepository.add(notification);
        }
    }

    @Override
    public void accept(Event<PlayerResponse> playerResponse) {
        final MatchWorkflow workflow = workflowService.getWorkflow(playerResponse.getData().getMatch());
        if (workflow != null) {
            workflow.notify(playerResponse);
        }
    }
}
