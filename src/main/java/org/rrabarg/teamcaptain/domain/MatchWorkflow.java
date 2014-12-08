package org.rrabarg.teamcaptain.domain;

import java.io.IOException;

import org.rrabarg.teamcaptain.SelectionStrategy;
import org.rrabarg.teamcaptain.domain.PlayerNotification.Kind;
import org.rrabarg.teamcaptain.service.NotificationService;
import org.rrabarg.teamcaptain.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import reactor.event.Event;

@Component
@Scope(value = "prototype")
public class MatchWorkflow {

    private static Logger log = LoggerFactory.getLogger(MatchWorkflow.class);

    @Autowired
    NotificationService notificationService;

    @Autowired
    WorkflowService workflowService;

    private Match match;

    private PoolOfPlayers poolOfPlayers;

    private SelectionStrategy selectionStrategy;

    public MatchWorkflow pump() {
        if (MatchState.InWindow == match.getMatchState()) {
            notifyFirstPickPlayers();
            match.setMatchState(MatchState.FirstPickPlayersNotified);
        }

        try {
            workflowService.recordWorkflow(this);
        } catch (final IOException e) {
            throw new RuntimeException("Failed pump", e);
        }

        return this;
    }

    private MatchWorkflow notifyFirstPickPlayers() {

        selectionStrategy.firstPick(poolOfPlayers)
                .stream()
                .peek(player -> log.debug("notifying " + player + " for " + match + " for " + Kind.CanYouPlay))
                .forEach(
                        player -> notificationService.notify(match, player, Kind.CanYouPlay));
        return this;
    }

    @Required
    public void setup(PoolOfPlayers pool, Match match, SelectionStrategy strategy) {
        this.match = match;
        this.poolOfPlayers = pool;
        this.selectionStrategy = strategy;
    }

    public void iCanPlay(Player player) throws IOException {
        match.setPlayerState(player, PlayerState.Accepted);
        notificationService.notify(match, player, Kind.ConfirmationOfAcceptance);
        pump();
    }

    private void iCannotPlay(Player player) {
        match.setPlayerState(player, PlayerState.Declined);
        notificationService.notify(match, player, Kind.ConfirmationOfDecline);
        notificationService.notify(match, selectionStrategy.nextPick(poolOfPlayers, player), Kind.CanYouPlay);
        pump();
    }

    public Match getMatch() {
        return this.match;
    }

    public void notify(Event<PlayerResponse> playerResponse) {
        try {
            switch (playerResponse.getData().getKind()) {
            case ICanPlay:
                iCanPlay(playerResponse.getData().getPlayer());
                break;
            case ICantPlay:
                iCannotPlay(playerResponse.getData().getPlayer());
                break;
            default:
                break;
            }
        } catch (final Exception e) {
            throw new RuntimeException("Failed to update workflow state " + this + " with " + playerResponse);
        }
    }
}