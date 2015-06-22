package org.rrabarg.teamcaptain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.config.MutableClockFactory;
import org.rrabarg.teamcaptain.domain.Channel;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.MatchStatus;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerStatus;
import org.rrabarg.teamcaptain.domain.User;
import org.rrabarg.teamcaptain.service.CompetitionService;
import org.rrabarg.teamcaptain.service.NotificationRepository;
import org.rrabarg.teamcaptain.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CompetitionFixture {

    protected final Logger log = LoggerFactory.getLogger(CompetitionFixture.class);

    public enum Response {
        Decline, Accept;

        public Response inverse() {
            if (this == Decline) {
                return Accept;
            }
            return Decline;
        }
    };

    @Autowired
    MutableClockFactory clockFactory;

    @Autowired
    CompetitionService competitionService;

    @Autowired
    TestMailbox mailbox;

    @Autowired
    NotificationRepository playerNotificationRepository;

    @Autowired
    WorkflowService workflowService;

    public void pumpWorkflows() {
        log.debug("Pumping the workflow");
        workflowService.pump();
    }

    public void refreshWorkflows(Competition competition) throws IOException {
        workflowService.refresh(competition);
    }

    public void clearCompetition(Competition comp) {
        competitionService.clearCompetition(comp);
        clearScenarioState();
    }

    public void clearScenarioState() {
        mailbox.clear();
        playerNotificationRepository.clear();
    }

    public void checkHasReceivedDetailedMatchStatus(Player player, Match match, List<Player> allNotifiedPlayers) {
        final Optional<Message> message = checkOutboundMessageIsCorrect(player,
                NotificationKind.MatchStatusUpdate, match);
        checkPlayerIsNamedInMessage(message, player);
        checkOutboundMessageMatchStatusDetails(message, match, allNotifiedPlayers);
    }

    public void checkAnTeamCaptainStandbyAlertIsRaised(Competition comp) {
        checkTeamCaptainAlert(comp, "standby");
    }

    public void checkTeamCaptainInsufficientPlayersAlertIsRaised(Competition comp) {
        checkTeamCaptainAlert(comp, "insufficient");
    }

    protected Optional<Message> checkTeamCaptainAlert(Competition comp, String expectedText) {

        final Optional<Message> optionalMessage = peekLastMessage(comp.getTeamCaptain());
        assertThat("Team captain alert message is expected", optionalMessage.isPresent());
        assertThat("Team captain alert should contain text '" + expectedText
                + "'", optionalMessage.get().getSubjectAndBody(),
                containsString(expectedText));
        return optionalMessage;
    }

    public void checkDailyReminderIsSentForDaysBeforeMatch(int daysBeforeMatch,
            Match match, Player playerThatDidntRespond) {
        for (int i = 0; i < daysBeforeMatch; i++) {
            checkReminderOnDay(i, match, playerThatDidntRespond);
        }
    }

    public void checkPlayerIsAssignedToTheMatch(Player player, Match thematch) {
        assertThat("The match must exist", thematch, notNullValue());
        assertThat("The match must be in notified state",
                MatchStatus.SelectionInProgress == thematch.getMatchStatus());
        assertThat("The player should be in the accepted state",
                PlayerStatus.Accepted == thematch.getPlayerState(player));
    }

    public void checkThereAreNoRemindersForPlayersThatDidNotRespond(Match match, List<Player> playersThatDidntRespond) {
        playersThatDidntRespond
                .forEach(p -> checkThereAreNoRemindersForPlayersThatDidNotRespond(
                        match, p));
    }

    protected void checkThereAreNoRemindersForPlayersThatDidNotRespond(
            Match match, final Player playerThatDidNotRespond) {
        final Optional<Message> findAnyReminder = mailbox
                .viewAll(playerThatDidNotRespond.getEmailAddress())
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .findFirst();

        if (findAnyReminder.isPresent()) {
            assertThat(findAnyReminder.get().getTimestamp(),
                    isDaysBeforeMatch(5, match));
        }
    }

    public void fixDateTimeBeforeMatch(long amount, ChronoUnit unit, Match match) {
        clockFactory.fixInstant(match.getStartDateTime().minus(amount, unit)
                .toInstant());
        log.debug("Fixing time to " + amount + " days before the match "
                + clockFactory.clock().instant());

    }

    public void checkMatchConfirmationContainsListOfPlayerInTeam(List<Player> selectedPlayersThatAccepted) {
        selectedPlayersThatAccepted.stream().forEach(
                player -> checkOutboundMessageContainsListOfPlayers(
                        peekLastMessage(player), selectedPlayersThatAccepted));
    }

    private Optional<Message> peekLastMessage(User user) {
        return mailbox.peek(user.getEmailAddress());
    }

    public void checkMatchConfirmationContainsTheMatchDetails(Match match, List<Player> selectedPlayersThatAccepted) {
        selectedPlayersThatAccepted.stream().forEach(
                player -> checkOutboundMessageMatchConfirmationDetails(
                        peekLastMessage(player), match, selectedPlayersThatAccepted));
    }

    public void checkMatchConfirmationSentToAllConfirmedPlayers(Match match, List<Player> selectedPlayersThatAccepted) {
        selectedPlayersThatAccepted.stream().forEach(
                player -> checkOutboundMessageIsCorrect(player,
                        NotificationKind.MatchConfirmation, match));
    }

    public void pumpWorkflowsTillXDaysBeforeMatch(
            final int daysTillMatchToStartReminders, Match match) {
        long daysTillMatch = getDaysTillMatch(match);
        while (--daysTillMatch >= daysTillMatchToStartReminders) {
            fixDateTimeBeforeMatch(daysTillMatch, ChronoUnit.DAYS, match);

            // simulate pump workflows being schedule multiple times during the
            // day, not just daily.
            for (int i = 0; i < 6; i++) {
                pumpWorkflows();
            }
        }
    }

    public void theRemainingPlayersSayTheyCanPlay(List<Player> playersThatDidntRespond) {
        playersThatDidntRespond.forEach(p -> aPlayerRespondsWith(p, "Yes"));
    }

    protected void aPlayerRespondsWith(Player player, String response) {
        mailbox.email().from(player.getEmailAddress()).subject("any text")
                .body(response).send();
    }

    protected void checkOutboundMessageIsCorrect(Player player,
            NotificationKind kindOfMessage, Match match, Message message,
            Integer daysBeforeMatch) {
        assertThat("Message must not be null for player " + player, message,
                notNullValue());

        assertThat("Message content doesn't contain the match title" + player,
                message.getSubjectAndBody(), containsString(match.getTitle()));

        assertThat("Message content mismatch for player " + player,
                message.getSubjectAndBody(),
                containsString(getContentStringFor(kindOfMessage)));
        if (daysBeforeMatch != null) {
            assertThat("Email Date is not correct for player " + player,
                    message.getTimestamp(),
                    isDaysBeforeMatch(daysBeforeMatch, match));
        }
    }

    protected void checkOutgoingMessageIsCorrect(Player player,
            NotificationKind kindOfMessage, Match match, Optional<Message> message,
            Integer daysBeforeMatch) {

        assertThat("Message must be present for player " + player, message.isPresent());

        final Message m = message.get();

        if (Channel.Email == m.getChannel()) {
            assertThat("Email subject must contain match title" + player,
                    m.getSubject(), containsString(match.getTitle()));
        } else {
            assertThat("Message must contain match title" + player,
                    m.getSubjectAndBody(), containsString(match.getTitle()));
        }

        assertThat("The outgoing message must be of the expected type : " + kindOfMessage, m.getSubjectAndBody(),
                containsString(getContentStringFor(kindOfMessage)));

        if (daysBeforeMatch != null) {
            assertThat("Message date is not correct for player " + player,
                    m.getTimestamp(),
                    isDaysBeforeMatch(daysBeforeMatch, match));
        }
    }

    protected void checkOutboundMessageContainsListOfPlayers(Optional<Message> optionalMessage,
            List<Player> players) {
        assertThat(
                "While checking that outbound email contained list of team's players, found email null",
                optionalMessage.isPresent());
        players.stream().forEach(
                player -> checkPlayerIsNamedInMessage(optionalMessage, player));
    }

    private void checkPlayerIsNamedInMessage(Optional<Message> message, Player player) {
        assertThat("Message content must contain the players name",
                message.get().getSubjectAndBody(), containsString(player.getKey()));
    }

    protected void checkOutboundMessageMatchConfirmationDetails(Optional<Message> optionalMessage, Match match,
            List<Player> players) {

        checkOutboundMessageMatchStatusDetails(optionalMessage, match, players);

        final Message message = optionalMessage.get();

        assertThat(
                "Message content must contain the match start time",
                message.getSubjectAndBody(),
                containsString(match.getStartDateTime().format(
                        DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))));

        if (match.getTravelDetails() != null) {
            assertThat("Message content must contain the match travel details",
                    message.getSubjectAndBody(), containsString(match.getTravelDetails()));
        }

        assertThat("Message content must contain the match location",
                message.getSubjectAndBody(), containsString(match.getLocation()
                        .toString()));

    }

    protected void checkOutboundMessageMatchStatusDetails(Optional<Message> optionalMessage,
            Match match, List<Player> players) {
        assertThat(
                "While checking that outbound message contained match details, found email null",
                optionalMessage.isPresent());

        final Message message = optionalMessage.get();

        assertThat("Message content must contain the match title",
                message.getSubjectAndBody(), containsString(match.getTitle().toString()));

        assertThat(
                "Message content must contain the match date",
                message.getSubjectAndBody(),
                containsString(match.getStartDateTime().format(
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))));

        checkOutboundMessageContainsListOfPlayers(optionalMessage, players);

    }

    public Optional<Message> checkOutboundMessageIsCorrect(Player player,
            NotificationKind kind, Match match) {
        final Optional<Message> findMessage = peekLastMessage(player);
        checkOutgoingMessageIsCorrect(player, kind, match, findMessage, null);
        return findMessage;
    }

    private Optional<Message> findMessage(Player player, NotificationKind kind) {
        return mailbox.viewAll(player.getEmailAddress())
                .filter(message -> message.getSubjectAndBody().contains(getContentStringFor(kind)))
                .findFirst();
    }

    protected void checkReminderOnDay(int daysBeforeMatch, Match match, List<Player> playersThatDidntRespond) {
        playersThatDidntRespond.forEach(p -> checkReminderOnDay(
                daysBeforeMatch, match, p));
    }

    protected void checkReminderOnDay(int daysBeforeMatch, Match match,
            Player playerThatDidNotRespond) {

        checkOutgoingMessageIsCorrect(playerThatDidNotRespond,
                NotificationKind.Reminder, match,
                peekLastMessage(playerThatDidNotRespond), daysBeforeMatch);
    }

    protected String getContentStringFor(NotificationKind messageKind) {
        switch (messageKind) {
        case CanYouPlay:
            return "selected to play";
        case ConfirmationOfStandby:
            return "Brilliant, thanks, I'll be in touch shortly to confirm.";
        case ConfirmationOfAcceptance:
            return "Brilliant, your in";
        case ConfirmationOfDecline:
            return "Sorry you couldn't play";
        case Reminder:
            return "Match reminder";
        case StandBy:
            return "Can you standby";
        case StandDown:
            return "won't need you for this match";
        case MatchConfirmation:
            return "Team selection has been now been confirmed";
        case MatchStatusUpdate:
            return "here is a status update";
        default:
            break;
        }
        assertThat("Fixture didn't know how to match message of type "
                + messageKind, false);
        return null;
    }

    protected long getDaysTillMatch(Match match) {
        return getDaysTillMatch(clockFactory.clock().instant(), match);
    }

    protected long getDaysTillMatch(Instant instant, Match match) {
        return Duration.between(instant, getMatchInstant(match)).toDays();
    }

    protected Instant getMatchInstant(Match match) {
        return match.getStartDateTime().toInstant();
    }

    protected String getTestCompetitionName() {
        return "Test competition-" + getUniqueNameForHost();
    }

    protected String getUniqueNameForHost() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (final UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    protected Matcher<Instant> isDaysBeforeMatch(Integer daysBeforeMatch,
            Match match) {
        return new BaseMatcher<Instant>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("days till match " + daysBeforeMatch
                        + " for match on " + match.getStartDateTime());
            }

            @Override
            public boolean matches(Object item) {

                if ((item == null) || (!(item instanceof Instant))) {
                    return false;
                }

                return getDaysTillMatch((Instant) item, match) == daysBeforeMatch;
            }
        };
    }

    public void checkNotificationGoesToEligibleFirstPickPlayers(Match match, List<Player> selectedPlayers,
            List<Player> selectedPlayersThatDeclined) {
        selectedPlayers
                .stream()
                .filter(p -> !selectedPlayersThatDeclined.contains(p))
                .forEach(
                        player -> checkOutboundMessageIsCorrect(player,
                                NotificationKind.CanYouPlay, match));
    }

    public void checkOutboundTeamCaptainMessageIsCorrect(Competition comp, NotificationKind kind,
            Match match, Player referencePlayer, List<Player> selectedPlayersThatAccepted) {

        switch (kind) {
        case OutOfBandMessage:
            checkTeamCaptainAlert(comp, "Message requires attention");
            break;
        case MatchFulfilled:
            checkTeamCaptainAlert(comp, "Team selection has been now been confirmed");
            checkOutboundMessageContainsListOfPlayers(peekLastMessage(comp.getTeamCaptain()),
                    selectedPlayersThatAccepted);
            break;
        case InsufficientPlayers:
            checkTeamCaptainAlert(comp, "Insufficient");
            break;
        default:
            throw new UnsupportedOperationException(String.format(
                    "Team captain email checking not implemented for %s",
                    kind));
        }
    }

    public List<Player> allSelectedPlayersRespondExcept(Match match,
            Response response, List<Player> exceptions, List<Player> selectedPlayers) {
        log.debug("All players %s except %s", response, exceptions);

        exceptions.stream().forEach(e -> aPlayerDoesNotRespond(e, match));

        selectedPlayers.stream().filter(player -> exceptions.contains(player))
                .forEach(player -> aPlayerResponds(player, response.inverse()));
        return exceptions;
    }

    public void checkNotificationDoesNotGoToPlayerWhoDeclined(List<Player> selectedPlayersThatDeclined) {
        for (final Player player : selectedPlayersThatDeclined) {
            assertThat("Mailbox of player who decline prior to match is empty",
                    peekLastMessage(player), nullValue());
        }
    }

    public void checkThatThoseWhoSaidTheyCouldPlayAreAssignedToTheMatch(Match match,
            List<Player> selectedPlayersThatAccepted) {
        for (final Player player : selectedPlayersThatAccepted) {
            checkPlayerIsAssignedToTheMatch(player, match);
        }
    }

    private void aPlayerDoesNotRespond(Player player, Match match) {
        checkOutboundMessageIsCorrect(player, NotificationKind.CanYouPlay, match);
    }

    public Player aPlayerResponds(Player player, Response response) {
        switch (response) {
        case Decline:
            aPlayerRespondsWith(player, "No");
        case Accept:
            aPlayerRespondsWith(player, "Yes");
        }
        return player;
    }

}
