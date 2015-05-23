package org.rrabarg.teamcaptain.domain;

import static org.rrabarg.teamcaptain.domain.NotificationKind.CanYouPlay;
import static org.rrabarg.teamcaptain.domain.NotificationKind.ConfirmationOfAcceptance;
import static org.rrabarg.teamcaptain.domain.NotificationKind.ConfirmationOfDecline;
import static org.rrabarg.teamcaptain.domain.NotificationKind.ConfirmationOfStandby;
import static org.rrabarg.teamcaptain.domain.NotificationKind.InsufficientPlayers;
import static org.rrabarg.teamcaptain.domain.NotificationKind.MatchConfirmation;
import static org.rrabarg.teamcaptain.domain.NotificationKind.MatchFulfilled;
import static org.rrabarg.teamcaptain.domain.NotificationKind.MatchStatusUpdate;
import static org.rrabarg.teamcaptain.domain.NotificationKind.Reminder;
import static org.rrabarg.teamcaptain.domain.NotificationKind.StandBy;
import static org.rrabarg.teamcaptain.domain.NotificationKind.StandDown;
import static org.rrabarg.teamcaptain.domain.NotificationKind.StandbyPlayersNotified;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.NotificationStrategy;
import org.rrabarg.teamcaptain.SelectionStrategy;
import org.rrabarg.teamcaptain.service.OutboundNotificationService;
import org.rrabarg.teamcaptain.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class MatchWorkflow {

    private static Logger log = LoggerFactory.getLogger(MatchWorkflow.class);

    @Autowired
    OutboundNotificationService notificationService;

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
            match.setMatchStatus(MatchStatus.MatchOutOfWindow);
        }

        if (MatchStatus.InWindow == match.getMatchStatus()) {
            notifyFirstPickPlayers();
            match.setMatchStatus(MatchStatus.FirstPickPlayersNotified);
        }

        final List<Player> team = match.getAcceptedPlayers(getPlayerPool());
        if (getSelectionStrategy().isViable(team)) {

            // send confirmation notifications
            sendNotificationToPlayers(team, MatchConfirmation);
            sendTeamCaptainAlert(MatchFulfilled);

            // send stand-down notifications
            final List<Player> standbyPlayers = match.getAcceptedOnStandbyPlayers(getPlayerPool());
            sendNotificationToPlayers(standbyPlayers, StandDown);
            standbyPlayers.stream().forEach(player -> getState().setPlayerState(player, PlayerStatus.None));

            // update state
            team.stream().forEach(player -> getState().setPlayerState(player, PlayerStatus.Confirmed));
            match.setMatchStatus(MatchStatus.MatchFulfilled);

        }

        if ((MatchStatus.FirstPickPlayersNotified == match.getMatchStatus()) &&
                (getDaysTillMatch() <= getNotificationStrategy().getDaysTillMatchForStandbys())) {
            sendStandbys();
        }

        if ((MatchStatus.FirstPickPlayersNotified == match.getMatchStatus()) &&
                (getDaysTillMatch() <= getNotificationStrategy().getDaysTillMatchForReminders())) {
            sendReminders();
        }

        if ((MatchStatus.FirstPickPlayersNotified == match.getMatchStatus()) &&
                (getDaysTillMatch() <= getNotificationStrategy().getDaysTillMatchForStatusUpdate())) {
            sendStatusUpdates();
        }

        try {
            workflowService.recordWorkflow(this);
        } catch (final IOException e) {
            throw new RuntimeException("Failed pump", e);
        }

        return this;
    }

    public void sendTeamCaptainAlert(NotificationKind kind) {
        notificationService.teamCaptainNotification(competition, match, kind);
    }

    private void sendNotificationToPlayers(List<Player> team, NotificationKind kind) {
        sendNotificationToPlayers(team.stream(), kind);
    }

    private void sendNotificationToPlayers(Stream<Player> team, NotificationKind kind) {
        team.forEach(player -> sendNotification(player, kind));
    }

    private void sendStandbyRequestIfPossible(Player player) {

        final Substitute nextPick = getNextPickPlayer(player);

        if (!nextPick.sub.isPresent()) {
            log.info("No available player to standby for " + player);
            return;
        }

        final Player theSubstitute = nextPick.sub.get();
        match.setPlayerStatus(theSubstitute, PlayerStatus.NotifiedForStandby);
        sendNotification(theSubstitute, StandBy);
        sendTeamCaptainAlert(StandbyPlayersNotified);
    }

    private void sendStandbys() {
        getPlayerPool().getPlayers().stream()
                .filter(player -> wasPlayerNotifiedAtLeastADayAgo(player, StandBy))
                .forEach(player -> sendStandbyRequestIfPossible(player));

    }

    private void sendReminders() {
        // EnumSet<NotificationKind> kinds = EnumSet.allOf(NotificationKind.class);
        final EnumSet<NotificationKind> kinds = EnumSet.of(Reminder, StandBy, MatchStatusUpdate);

        getPlayerPool().getPlayers().stream()
                .filter(stateIsOneOf(PlayerStatus.Notified, PlayerStatus.NotifiedForStandby))
                .filter(player -> wasPlayerNotifiedAtLeastADayAgo(player, kinds))
                .forEach(player -> sendNotification(player, Reminder));
    }

    private void sendStatusUpdates() {
        getPlayerPool()
                .getPlayers()
                .stream()
                .filter(stateIsOneOf(PlayerStatus.Notified, PlayerStatus.NotifiedForStandby,
                        PlayerStatus.AcceptedOnStandby,
                        PlayerStatus.Accepted))
                .filter(player -> wasPlayerNotifiedAtLeastADayAgo(player, MatchStatusUpdate))
                .forEach(player -> sendNotification(player, MatchStatusUpdate));
    }

    private MatchWorkflow notifyFirstPickPlayers() {

        final Collection<Player> potentialFirstPick = getSelectionStrategy().firstPick(getPlayerPool());

        final List<Substitute> potentialSubstitutes =
                potentialFirstPick.stream().filter(p -> match.getPlayerState(p) == PlayerStatus.Declined)
                        .peek(p -> log.info("The first pick player " + p
                                + " had previously declined this match so will be substituted"))
                        .map(s -> getNextPickPlayer(s)).collect(Collectors.toList());

        if (potentialSubstitutes.stream().filter(s -> !s.sub.isPresent()).findAny().isPresent()) {
            sendTeamCaptainAlert(InsufficientPlayers);
        }

        final Stream<Player> firstPick = potentialFirstPick.stream().filter(
                p -> match.getPlayerState(p) != PlayerStatus.Declined);

        final Stream<Player> substitutes = potentialSubstitutes.stream().filter(s -> s.sub.isPresent())
                .map(s -> s.sub.get());

        Stream.concat(substitutes, firstPick)
                .peek(player -> log.debug("notifying " + player + " for " + match + " for "
                        + CanYouPlay))
                .peek(player -> getState().setPlayerState(player, PlayerStatus.Notified))
                .forEach(player -> sendNotification(player, CanYouPlay));
        return this;
    }

    Notification earliest(List<Notification> notifications) {
        return notifications.get(0);
    }

    public void iCanPlay(Player player) throws IOException {
        match.setPlayerStatus(player, PlayerStatus.Accepted);
        sendNotification(player, ConfirmationOfAcceptance);
        pump();
    }

    private void iCanStandby(Player player) {
        match.setPlayerStatus(player, PlayerStatus.AcceptedOnStandby);
        sendNotification(player, ConfirmationOfStandby);
        pump();
    }

    private void iCannotPlay(Player player) {
        match.setPlayerStatus(player, PlayerStatus.Declined);
        sendNotification(player, ConfirmationOfDecline);

        final Substitute sub = getNextPickPlayer(player);

        if (!sub.sub.isPresent()) {
            sendTeamCaptainAlert(InsufficientPlayers);
            return;
        }

        final Player theSubstitute = sub.sub.get();

        sendNotification(theSubstitute, CanYouPlay);
        match.setPlayerStatus(theSubstitute, PlayerStatus.Notified);
        pump();
    }

    public void sendNotification(Player player, NotificationKind notificationKind) {
        log.debug("Sending " + notificationKind + " to " + player);
        notificationService.playerNotification(competition, match, player, notificationKind);
    }

    private WorkflowState getState() {
        return match.getWorkflowState();
    }

    public Match getMatch() {
        return this.match;
    }

    public void notify(PlayerResponse playerResponse) {
        try {
            switch (playerResponse.getKind()) {
            case ICanPlay:
                iCanPlay(playerResponse.getPlayer());
                break;
            case ICanStandby:
                iCanStandby(playerResponse.getPlayer());
                break;
            case ICantPlay:
                iCannotPlay(playerResponse.getPlayer());
                break;
            default:
                break;
            }
        } catch (final Exception e) {
            throw new RuntimeException("Failed to update workflow state " + this + " with " + playerResponse);
        }
    }

    public boolean wasPlayerNotifiedAtLeastADayAgo(Player player, Set<NotificationKind> kinds) {
        final PlayerState playerState = getMatch().getWorkflowState().getPlayerState(player);
        return playerState == null ? false : playerState.wasPlayerNotifiedAtLeastADayAgo(now(), kinds);
    }

    public boolean wasPlayerNotifiedAtLeastADayAgo(Player player, NotificationKind kind) {
        final PlayerState playerState = getMatch().getWorkflowState().getPlayerState(player);
        return playerState == null ? false : playerState.wasPlayerNotifiedAtLeastADayAgo(now(), kind);
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
            if ((nextPick != null) && (match.getPlayerState(nextPick) == PlayerStatus.None)) {
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

    private Predicate<? super Player> stateIsOneOf(PlayerStatus playerstate, PlayerStatus... playerstates) {
        return player -> EnumSet.of(playerstate, playerstates).contains(
                getPlayerState(player));
    }

    private PlayerStatus getPlayerState(Player player) {
        return getState().getPlayerStatus(player);
    }

}