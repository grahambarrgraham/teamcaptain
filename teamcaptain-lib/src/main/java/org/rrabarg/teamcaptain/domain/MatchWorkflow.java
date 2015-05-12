package org.rrabarg.teamcaptain.domain;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.NotificationStrategy;
import org.rrabarg.teamcaptain.SelectionStrategy;
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

    private Competition competition;

    @Required
    public void setup(Competition competition, Match match) {
        this.competition = competition;
        this.match = match;
    }

    public MatchWorkflow pump() {

        if (getDaysTillMatch() < 0) {
            log.info("match {} is in past so is ignored", match);
            match.setMatchState(MatchState.MatchOutOfWindow);
        }

        if (MatchState.InWindow == match.getMatchState()) {
            notifyFirstPickPlayers();
            match.setMatchState(MatchState.FirstPickPlayersNotified);
        }

        final List<Player> team = match.getAcceptedPlayers(getPlayerPool());
        if (getSelectionStrategy().isViable(team)) {
            sendConfirmationEmailsToPlayer(team);
            sendAdminAlert(NotificationKind.MatchFulfilled);

            team.stream().forEach(player -> getState().setPlayerState(player, PlayerState.Confirmed));
            match.setMatchState(MatchState.MatchFulfilled);
        }

        if ((MatchState.FirstPickPlayersNotified == match.getMatchState()) &&
                (getDaysTillMatch() <= getNotificationStrategy().getDaysTillMatchForStandbys())) {
            sendStandbys();
        }

        if ((MatchState.FirstPickPlayersNotified == match.getMatchState()) &&
                (getDaysTillMatch() <= getNotificationStrategy().getDaysTillMatchForReminders())) {
            sendReminders();
        }

        try {
            workflowService.recordWorkflow(this);
        } catch (final IOException e) {
            throw new RuntimeException("Failed pump", e);
        }

        return this;
    }

    public void sendAdminAlert(NotificationKind kind) {
        notificationService.teamCaptainNotification(competition, match, kind);
    }

    private void sendConfirmationEmailsToPlayer(List<Player> team) {
        team.stream()
                .peek(player -> log.debug("notifying " + player + " for " + match + " for "
                        + NotificationKind.MatchConfirmation))
                .forEach(
                        player -> sendNotification(player, NotificationKind.MatchConfirmation));
    }

    private void sendStandbyRequestIfPossible(Player player) {

        final Substitute nextPick = getNextPickPlayer(player);

        if (!nextPick.sub.isPresent()) {
            log.info("No available player to standby for " + player);
            return;
        }

        final Player theSubstitute = nextPick.sub.get();
        sendNotification(theSubstitute, NotificationKind.StandBy);
        sendAdminAlert(NotificationKind.StandbyPlayersNotified);
        match.setPlayerState(theSubstitute, PlayerState.Notified);
    }

    private void sendStandbys() {
        getPlayersWithAPendingNotification()
                .forEach(player -> sendStandbyRequestIfPossible(player));
    }

    private void sendReminders() {
        getPlayersWithAPendingNotification()
                .forEach(player -> sendNotification(player, NotificationKind.Reminder));
    }

    private MatchWorkflow notifyFirstPickPlayers() {

        final Collection<Player> potentialFirstPick = getSelectionStrategy().firstPick(getPlayerPool());

        final List<Substitute> potentialSubstitutes =
                potentialFirstPick.stream().filter(p -> match.getPlayerState(p) == PlayerState.Declined)
                        .peek(p -> log.info("The first pick player " + p
                                + " had previously declined this match so will be substituted"))
                        .map(s -> getNextPickPlayer(s)).collect(Collectors.toList());

        if (potentialSubstitutes.stream().filter(s -> !s.sub.isPresent()).findAny().isPresent()) {
            sendAdminAlert(NotificationKind.InsufficientPlayers);
        }

        final Stream<Player> firstPick = potentialFirstPick.stream().filter(
                p -> match.getPlayerState(p) != PlayerState.Declined);

        final Stream<Player> substitutes = potentialSubstitutes.stream().filter(s -> s.sub.isPresent())
                .map(s -> s.sub.get());

        Stream.concat(substitutes, firstPick)
                .peek(player -> log.debug("notifying " + player + " for " + match + " for "
                        + NotificationKind.CanYouPlay))
                .peek(player -> getState().setPlayerState(player, PlayerState.Notified))
                .forEach(player -> sendNotification(player, NotificationKind.CanYouPlay));
        return this;
    }

    private Stream<Player> getPlayersWithAPendingNotification() {
        return getMostRecentNotificationAtLeastADayOldPerPlayer()
                .map(notification -> notification.getPlayer())
                .filter(stateIsOneOf(PlayerState.Notified));
    }

    private Stream<PlayerNotification> getMostRecentNotificationAtLeastADayOldPerPlayer() {
        return getMostRecentNotificationForEachPlayer()
                .filter(a -> isAtLeastADayAgo(a.getTimestamp()));
    }

    private Stream<PlayerNotification> getMostRecentNotificationForEachPlayer() {
        return notificationService
                .getPendingNotifications(match)
                .collect(groupingBy(m -> m.getTarget()))
                .entrySet()
                .stream()
                .filter(e -> (e.getKey() instanceof Player))
                .collect(toMap(Map.Entry::getKey,
                        e -> e.getValue().stream().max(Comparator.comparing(Notification::getTimestamp)).get()))
                .values()
                .stream()
                .filter(a -> a instanceof PlayerNotification)
                .map(a -> (PlayerNotification) a);
    }

    Notification earliest(List<Notification> notifications) {
        return notifications.get(0);
    }

    public void iCanPlay(Player player) throws IOException {
        match.setPlayerState(player, PlayerState.Accepted);
        sendNotification(player, NotificationKind.ConfirmationOfAcceptance);
        pump();
    }

    private void iCanStandby(Player player) {
        match.setPlayerState(player, PlayerState.OnStandby);
        sendNotification(player, NotificationKind.ConfirmationOfStandby);
        pump();
    }

    private void iCannotPlay(Player player) {
        match.setPlayerState(player, PlayerState.Declined);
        sendNotification(player, NotificationKind.ConfirmationOfDecline);

        final Substitute sub = getNextPickPlayer(player);

        if (!sub.sub.isPresent()) {
            sendAdminAlert(NotificationKind.InsufficientPlayers);
            return;
        }

        final Player theSubstitute = sub.sub.get();

        sendNotification(theSubstitute, NotificationKind.CanYouPlay);
        match.setPlayerState(theSubstitute, PlayerState.Notified);
        pump();
    }

    public void sendNotification(Player player, NotificationKind notificationKind) {
        notificationService.playerNotification(competition, match, player, notificationKind);
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

    private boolean isAtLeastADayAgo(Instant timestamp) {
        return atLeastXDaysAgo(timestamp, 1);
    }

    private boolean atLeastXDaysAgo(Instant timestamp, int i) {
        return Duration.between(timestamp, now()).toDays() >= i;
    }

    private Instant now() {
        return clockProvider.get().instant();
    }

    private long getDaysTillMatch() {
        return Duration.between(
                now(),
                match.getStartDateTime().toInstant()).toDays();
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
            final Player nextPick = getSelectionStrategy().nextPick(getPlayerPool(), p);
            if ((nextPick != null) && (match.getPlayerState(nextPick) == PlayerState.None)) {
                return new Substitute(p, Optional.of(nextPick));
            }

            if (nextPick == null) {
                return new Substitute(p, Optional.empty());
            }

            p = nextPick;

        } while (true);
    }

    private SelectionStrategy getSelectionStrategy() {
        return competition.getSelectionStrategy();
    }

    private NotificationStrategy getNotificationStrategy() {
        return competition.getNotificationStrategy();
    }

    private PlayerPool getPlayerPool() {
        return competition.getPlayerPool();
    }

    private Predicate<? super Player> stateIsOneOf(PlayerState playerstate, PlayerState... playerstates) {
        return player -> EnumSet.of(playerstate, playerstates).contains(
                getPlayerState(player));
    }

    private PlayerState getPlayerState(Player player) {
        return getState().getPlayerState(player);
    }

}