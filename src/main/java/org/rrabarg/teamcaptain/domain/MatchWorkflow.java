package org.rrabarg.teamcaptain.domain;

import java.io.IOException;

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
        poolOfPlayers
                .getPlayers()
                .stream()
                .peek(player -> log.debug("notifying " + player + " for " + match + " for " + Kind.CanYouPlay))
                .forEach(
                        player -> notificationService.notify(match, player, Kind.CanYouPlay));
        return this;
    }

    @Required
    public void setup(PoolOfPlayers pool, Match match) {
        this.match = match;
        this.poolOfPlayers = pool;
    }

    public void iCanPlay(Player player) throws IOException {
        match.setPlayerState(player, PlayerState.Accepted);
        pump();
    }

    public Match getMatch() {
        return this.match;
    }

    public void update(PoolOfPlayers pool, Match match) {
        this.poolOfPlayers = pool;
        this.match = match;
    }

    public void notify(Event<PlayerResponse> playerResponse) {
        try {
            switch (playerResponse.getData().getKind()) {
            case ICanPlay:
                iCanPlay(playerResponse.getData().getPlayer());
            default:
                break;
            }
        } catch (final Exception e) {
            throw new RuntimeException("Failed to update workflow state " + this + " with " + playerResponse);
        }
    }
}