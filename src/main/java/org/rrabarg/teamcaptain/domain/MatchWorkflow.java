package org.rrabarg.teamcaptain.domain;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private PlayerPool playerPool;

    private SelectionStrategy selectionStrategy;

    public MatchWorkflow pump() {

        if (MatchState.InWindow == match.getMatchState()) {
            notifyFirstPickPlayers();
            match.setMatchState(MatchState.FirstPickPlayersNotified);
        }

        final List<Player> team = match.getAcceptedPlayers(playerPool);
        if (selectionStrategy.isViable(team)) {
            sendConfirmationEmailsToPlayer(team);
            sendAdminAlert(AdminAlert.Kind.MatchFulfilled);

            team.stream().forEach(player -> getState().setPlayerState(player, PlayerState.Confirmed));
            match.setMatchState(MatchState.MatchFulfilled);
        }

        if ((MatchState.FirstPickPlayersNotified == match.getMatchState()) &&
                (getDaysTillMatch() <= selectionStrategy.getDaysTillMatchForReminders())) {
            sendReminders();
        }

        if ((MatchState.FirstPickPlayersNotified == match.getMatchState()) &&
                (getDaysTillMatch() <= selectionStrategy.getDaysTillMatchForStandbys())) {
            sendStandbys();
            sendAdminAlert(AdminAlert.Kind.StandbyPlayersNotified);
        }

        try {
            workflowService.recordWorkflow(this);
        } catch (final IOException e) {
            throw new RuntimeException("Failed pump", e);
        }

        return this;
    }

    public void sendAdminAlert(org.rrabarg.teamcaptain.domain.AdminAlert.Kind kind) {
        notificationService.adminAlert(playerPool, match, kind);
    }

    private void sendConfirmationEmailsToPlayer(List<Player> team) {
        team.stream()
                .peek(player -> log.debug("notifying " + player + " for " + match + " for " + Kind.MatchConfirmation))
                .forEach(
                        player -> sendNotification(player, Kind.MatchConfirmation));
    }

    private void sendStandbys() {
        notificationService
                .getPendingNotifications(match)
                .filter(m -> m.getKind() == Kind.CanYouPlay)
                .map(m -> m.getPlayer())
                .distinct()
                .forEach(
                        player ->
                        sendStandbyRequestIfPossible(player));
    }

    private void sendStandbyRequestIfPossible(Player player) {

        final Substitute nextPick = getNextPickPlayer(player);

        if (!nextPick.sub.isPresent()) {
            log.info("No available player to standby for " + player);
            return;
        }

        sendNotification(nextPick.sub.get(), Kind.StandBy);
    }

    private long getDaysTillMatch() {
        return Duration.between(
                now(),
                match.getStartDateTime().toInstant()).toDays();
    }

    private MatchWorkflow notifyFirstPickPlayers() {

        final Collection<Player> potentialFirstPick = selectionStrategy.firstPick(playerPool);

        final List<Substitute> potentialSubstitutes =
                potentialFirstPick.stream().filter(p -> match.getPlayerState(p) == PlayerState.Declined)
                        .peek(p -> log.info("The first pick player " + p
                                + " had previously declined this match so will be substituted"))
                        .map(s -> getNextPickPlayer(s)).collect(Collectors.toList());

        if (potentialSubstitutes.stream().filter(s -> !s.sub.isPresent()).findAny().isPresent()) {
            sendAdminAlert(AdminAlert.Kind.InsufficientPlayers);
        }

        final Stream<Player> firstPick = potentialFirstPick.stream().filter(
                p -> match.getPlayerState(p) != PlayerState.Declined);

        final Stream<Player> substitutes = potentialSubstitutes.stream().filter(s -> s.sub.isPresent())
                .map(s -> s.sub.get());

        Stream.concat(substitutes, firstPick)
                .peek(player -> log.debug("notifying " + player + " for " + match + " for " + Kind.CanYouPlay))
                .peek(player -> getState().setPlayerState(player, PlayerState.Notified))
                .forEach(player -> sendNotification(player, Kind.CanYouPlay));
        return this;
    }

    class Substitute {
        final Player original;
        final Optional<Player> sub;

        public Substitute(Player original, Optional<Player> sub) {
            this.original = original;
            this.sub = sub;
        }
    }

    private Substitute getNextPickPlayer(Player p) {
        do {
            final Player nextPick = selectionStrategy.nextPick(playerPool, p);
            if ((nextPick != null) && (match.getPlayerState(nextPick) != PlayerState.Declined)) {
                return new Substitute(p, Optional.of(nextPick));
            }

            if (nextPick == null) {
                return new Substitute(p, Optional.empty());
            }
        } while (true);
    }

    private void sendReminders() {
        notificationService.getPendingNotifications(match)
                .filter(m -> isMoreThanADayAgo(m.getTimestamp()))
                .map(m -> m.getPlayer())
                .distinct()
                .forEach(player -> sendNotification(player, Kind.Reminder));
    }

    @Required
    public void setup(PlayerPool pool, Match match, SelectionStrategy strategy) {
        this.match = match;
        this.playerPool = pool;
        this.selectionStrategy = strategy;
    }

    public void iCanPlay(Player player) throws IOException {
        match.setPlayerState(player, PlayerState.Accepted);
        sendNotification(player, Kind.ConfirmationOfAcceptance);
        pump();
    }

    private void iCanStandby(Player player) {
        match.setPlayerState(player, PlayerState.OnStandby);
        sendNotification(player, Kind.ConfirmationOfStandby);
        pump();
    }

    private void iCannotPlay(Player player) {
        match.setPlayerState(player, PlayerState.Declined);
        sendNotification(player, Kind.ConfirmationOfDecline);

        final Substitute sub = getNextPickPlayer(player);

        if (!sub.sub.isPresent()) {
            sendAdminAlert(AdminAlert.Kind.InsufficientPlayers);
            return;
        }

        sendNotification(sub.sub.get(), Kind.CanYouPlay);
        getState().substitute(player, sub.sub.get(), PlayerState.Notified);
        pump();
    }

    public void sendNotification(Player player, Kind confirmationofdecline) {
        notificationService.notify(playerPool, match, player, confirmationofdecline);
    }

    private WorkflowState getState() {
        return match.getWorkflowState();
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