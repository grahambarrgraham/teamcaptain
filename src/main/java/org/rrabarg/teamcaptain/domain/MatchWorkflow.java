package org.rrabarg.teamcaptain.domain;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import javax.inject.Provider;

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

    @Autowired
    Provider<Clock> clockProvider;

    private Match match;

    private PoolOfPlayers poolOfPlayers;

    private SelectionStrategy selectionStrategy;

    public MatchWorkflow pump() {
        if (MatchState.InWindow == match.getMatchState()) {
            notifyFirstPickPlayers();
            match.setMatchState(MatchState.FirstPickPlayersNotified);
        }

        if ((MatchState.FirstPickPlayersNotified == match.getMatchState()) &&
                (getDaysTillMatch() <= selectionStrategy.getDaysTillMatchForReminders())) {
            sendReminders();
        }

        if ((MatchState.FirstPickPlayersNotified == match.getMatchState()) &&
                (getDaysTillMatch() <= selectionStrategy.getDaysTillMatchForStandbys())) {
            sendStandbys();
            notificationService.adminAlert(match, AdminAlert.Kind.StandbyPlayersNotified);
        }

        try {
            workflowService.recordWorkflow(this);
        } catch (final IOException e) {
            throw new RuntimeException("Failed pump", e);
        }

        return this;
    }

    private void sendStandbys() {
        notificationService
                .getPendingNotifications(match)
                .filter(m -> m.getKind() == Kind.CanYouPlay)
                .map(m -> m.getPlayer())
                .distinct()
                .forEach(
                        player ->
                        notificationService.notify(match, selectionStrategy.nextPick(poolOfPlayers, player),
                                Kind.StandBy));
    }

    private long getDaysTillMatch() {
        return Duration.between(
                now(),
                match.getStartDateTime().toInstant()).toDays();
    }

    private MatchWorkflow notifyFirstPickPlayers() {

        selectionStrategy.firstPick(poolOfPlayers)
                .stream()
                .peek(player -> log.debug("notifying " + player + " for " + match + " for " + Kind.CanYouPlay))
                .forEach(
                        player -> notificationService.notify(match, player, Kind.CanYouPlay));
        return this;
    }

    private void sendReminders() {
        notificationService.getPendingNotifications(match)
                .filter(m -> isMoreThanADayAgo(m.getTimestamp()))
                .map(m -> m.getPlayer())
                .distinct()
                .forEach(player -> notificationService.notify(match, player, Kind.Reminder));
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

    private void iCanStandby(Player player) {
        match.setPlayerState(player, PlayerState.OnStandby);
        notificationService.notify(match, player, Kind.ConfirmationOfStandby);
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
            case ICanStandby:
                iCanStandby(playerResponse.getData().getPlayer());
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

    private boolean isMoreThanADayAgo(Instant timestamp) {
        return Duration.between(timestamp, now()).toDays() > 1;
    }

    private Instant now() {
        return clockProvider.get().instant();
    }

}