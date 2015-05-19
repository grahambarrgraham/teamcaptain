package org.rrabarg.teamcaptain.fixture;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.rrabarg.teamcaptain.TestMailbox;
import org.rrabarg.teamcaptain.channel.Email;
import org.rrabarg.teamcaptain.channel.Message;
import org.rrabarg.teamcaptain.config.MutableClockFactory;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.MatchState;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerState;
import org.rrabarg.teamcaptain.service.CompetitionService;
import org.rrabarg.teamcaptain.service.NotificationRepository;
import org.rrabarg.teamcaptain.service.ScheduleService;
import org.rrabarg.teamcaptain.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseFixture {

    protected final Logger log = LoggerFactory.getLogger(BaseFixture.class);

    protected final List<Player> selectedPlayers = new ArrayList<Player>();
    protected final List<Player> playersThatDidntRespond = new ArrayList<Player>();
    protected final List<Player> selectedPlayersThatDeclined = new ArrayList<Player>();
    protected final List<Player> selectedPlayersThatAccepted = new ArrayList<Player>();
    protected final List<Player> standbyPlayersThatAccepted = new ArrayList<Player>();
    protected final List<Player> standbyPlayersThatDeclined = new ArrayList<Player>();

    protected Competition competition;
    protected Match match;

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
    ScheduleService scheduleService;

    @Autowired
    TestMailbox mailbox;

    @Autowired
    NotificationRepository playerNotificationRepository;

    @Autowired
    WorkflowService workflowService;

    protected abstract Competition createCompetitionImpl();

    protected abstract void setupScenarioImpl();

    public void pumpWorkflows() {
        log.debug("Pumping the workflow");
        workflowService.pump();
    }

    public void refreshWorkflows() throws IOException {
        workflowService.refresh(competition);
    }

    public void clearCompetition() {

        if (competition == null) {
            return;
        }

        competitionService.clearCompetition(competition);
    }

    public void clearScenarioState() {
        mailbox.clear();
        playerNotificationRepository.clear();
        selectedPlayersThatAccepted.clear();
        standbyPlayersThatAccepted.clear();
        standbyPlayersThatDeclined.clear();
        selectedPlayers.clear();
        playersThatDidntRespond.clear();
        selectedPlayersThatDeclined.clear();
    }

    public Competition createCompetition() {
        competition = createCompetitionImpl();
        competitionService.saveCompetition(competition);
        return competition;
    }

    public void setupScenario() {
        clearCompetition();
        clearScenarioState();
        setupScenarioImpl();
    }

    public void teardownScenario() {

    }

    public void teardownStory() {
        clearCompetition();
    }

    public void allFirstPickPlayersConfirmTheyCanPlay() {
        selectedPlayersThatAccepted.addAll(selectedPlayers);
        selectedPlayers.stream().forEach(player -> aPlayerRespondsWith(player, "Yes"));
    }

    public void checkAcknowledgementGoesToPlayerWhoAccepted(Match match) {
        selectedPlayersThatAccepted.forEach(player ->
                checkAcknowledgement(match, player, NotificationKind.ConfirmationOfAcceptance));
    }

    public void checkAcknowledgementGoesToPlayerWhoAcceptedStandby(Match match) {
        standbyPlayersThatAccepted.forEach(player ->
                checkAcknowledgement(match, player, NotificationKind.ConfirmationOfStandby));
    }

    public void checkAcknowledgementGoesToPlayerWhoDeclined(Match match) {
        selectedPlayersThatDeclined.forEach(p -> checkAcknowledgement(match, p,
                NotificationKind.ConfirmationOfDecline));

        standbyPlayersThatDeclined.forEach(p -> checkAcknowledgement(match, p,
                NotificationKind.ConfirmationOfDecline));
    }

    public void checkHasReceivedDetailedMatchStatus(Player player, Match match) {
        checkOutboundMessageIsCorrect(player, NotificationKind.MatchStatus, match);
    }

    protected void checkAcknowledgement(Match match, Player player, NotificationKind kind) {
        checkEmailIsCorrect(match, player,
                mailbox.pop(player.getEmailAddress()),
                kind, null);
    }

    public void checkAllCanYouPlayNotificationsWereSent(Match match) {
        selectedPlayers.stream().forEach(
                player -> checkOutboundMessageIsCorrect(player, NotificationKind.CanYouPlay, match));
    }

    public void checkAnAdministratorMatchConfirmationIsRaised(Match match) {
        final Email email = mailbox.peek(competition.getTeamCaptain().getContactDetail().getEmailAddress());
        assertThat("While checking that outbound admin email confirmation, found email null", email, notNullValue());
        assertThat("While checking that outbound admin email confirmation body, found body null", email.getBody(),
                notNullValue());
        assertThat("Subject should contain string 'confirmation'", email.getSubject().toLowerCase(),
                containsString("confirmation"));
        checkOutboundEmailContainsListOfPlayers(email);
        checkOutboundEmailContainsMatchDetails(email, match);
    }

    public void checkAnAdminstratorStandbyAlertIsRaised() {
        checkAdminAlert("standby");
    }

    public void checkAnAdminstratorInsufficientPlayersAlertIsRaised() {
        checkAdminAlert("insufficient");
    }

    protected void checkAdminAlert(String expectedText) {
        final Email email = mailbox.pop(competition.getTeamCaptain().getContactDetail().getEmailAddress());
        assertThat("Admin Email must not be null", email, notNullValue());
        assertThat("Admin alert should be ", email.getSubject().toLowerCase(), containsString(expectedText));
    }

    public void checkDailyReminderIsSentForDaysBeforeMatch(int daysBeforeMatch, Match match) {
        for (int i = 0; i < daysBeforeMatch; i++) {
            checkReminderOnDay(i, match);
        }
    }

    public void checkPlayerIsAssignedToTheMatch(Player player) {

        final Competition comp = competitionService.findCompetitionByName(competition.getName());

        final Optional<Match> thematch = comp.getSchedule().getMatches()
                .stream().findFirst();

        assertThat("The match must exist", thematch.isPresent());
        assertThat("The match must be in notified state", MatchState.FirstPickPlayersNotified == thematch.get()
                .getMatchState());
        assertThat("The player should be in the accepted state", PlayerState.Accepted == thematch.get()
                .getPlayerState(player));
    }

    public void checkThereAreNoRemindersForPlayersThatDidNotRespond(Match match) {
        playersThatDidntRespond.forEach(p -> checkThereAreNoRemindersForPlayersThatDidNotRespond(match, p));
    }

    protected void checkThereAreNoRemindersForPlayersThatDidNotRespond(Match match, final Player playerThatDidNotRespond) {
        final Optional<Email> findAnyReminder = mailbox.viewAll(playerThatDidNotRespond.getEmailAddress())
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())).findFirst();

        assertThat(findAnyReminder.get().getTimestamp(), isDaysBeforeMatch(5, match));
    }

    public void fixDateTimeBeforeMatch(long amount, ChronoUnit unit, Match match) {
        clockFactory.fixInstant(match.getStartDateTime().minus(amount, unit).toInstant());
        log.debug("Fixing time to " + amount + " days before the match " + clockFactory.clock().instant());

    }

    public void checkMatchConfirmationContainsListOfPlayerInTeam() {
        selectedPlayersThatAccepted.stream().forEach(
                player -> checkOutboundEmailContainsListOfPlayers(mailbox.peek(player.getEmailAddress())));
    }

    public void checkMatchConfirmationContainsTheMatchDetails(Match match) {
        selectedPlayersThatAccepted.stream().forEach(
                player -> checkOutboundEmailContainsMatchDetails(mailbox.peek(player.getEmailAddress()), match));
    }

    public void checkMatchConfirmationSentToAllConfirmedPlayers(Match match) {
        selectedPlayersThatAccepted.stream().forEach(
                player -> checkOutboundMessageIsCorrect(player, NotificationKind.MatchConfirmation, match));
    }

    public void pumpWorkflowsTillXDaysBeforeMatch(final int daysTillMatchToStartReminders, Match match) {
        long daysTillMatch = getDaysTillMatch(match);
        while (--daysTillMatch >= daysTillMatchToStartReminders) {
            fixDateTimeBeforeMatch(daysTillMatch, ChronoUnit.DAYS, match);

            // simulate pump workflows being schedule multiple times during the day, not just daily.
            for (int i = 0; i < 6; i++) {
                pumpWorkflows();
            }
        }
    }

    public void theRemainingPlayersSayTheyCanPlay() {
        playersThatDidntRespond.forEach(p -> aPlayerRespondsWith(p, "Yes"));
    }

    protected void aPlayerRespondsWith(Player player, String response) {
        mailbox.email().from(player.getEmailAddress()).subject("any text").body(response).send();
    }

    protected void checkEmailIsCorrect(Match match, Player player, Email email, NotificationKind kindOfEmail,
            Integer daysBeforeMatch) {
        assertThat("Email must not be null for player " + player, email, notNullValue());
        assertThat("Email Subject mismatch for player " + player, email.getSubject(), containsString(match.getTitle()));
        assertThat("Email Body mismatch for player " + player, email.getBody(),
                containsString(getContentStringFor(kindOfEmail)));
        if (daysBeforeMatch != null) {
            assertThat("Email Date is not correct for player " + player, email.getTimestamp(),
                    isDaysBeforeMatch(daysBeforeMatch, match));
        }
    }

    protected void checkOutboundEmailContainsListOfPlayers(Email email) {
        assertThat("While checking that outbound email contained list of team's players, found email null", email,
                notNullValue());
        selectedPlayersThatAccepted.stream().forEach(
                player -> checkPlayerIsNamedInMessage(email, player));
    }

    private void checkPlayerIsNamedInMessage(Message message, Player player) {
        assertThat("Email body must contain the players name", message.getBody(),
                containsString(player.getKey()));
    }

    protected void checkOutboundEmailContainsMatchDetails(Message message, Match match) {
        assertThat("While checking that outbound email contained match details, found email null", message,
                notNullValue());

        assertThat("Email body must contain the match location", message.getBody(),
                containsString(match.getLocation().toString()));

        assertThat("Email body must contain the match title", message.getBody(),
                containsString(match.getTitle().toString()));

        assertThat("Email body must contain the match date", message.getBody(),
                containsString(match.getStartDateTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))));

        assertThat("Email body must contain the match start time", message.getBody(),
                containsString(match.getStartDateTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))));

        if (match.getTravelDetails() != null) {
            assertThat("Email body must contain the match travel details", message.getBody(),
                    containsString(match.getTravelDetails()));
        }
    }

    public Message checkOutboundMessageIsCorrect(Player player, NotificationKind kind, Match match) {
        final Email message = mailbox.peek(player.getEmailAddress());
        checkEmailIsCorrect(match, player, message, kind, null);
        checkPlayerIsNamedInMessage(message, player);
        checkOutboundEmailContainsListOfPlayers(message);
        checkOutboundEmailContainsMatchDetails(message, match);

        return message;
    }

    protected void checkReminderOnDay(int daysBeforeMatch, Match match) {
        playersThatDidntRespond.forEach(p -> checkReminderOnDay(daysBeforeMatch, match, p));
    }

    protected void checkReminderOnDay(int daysBeforeMatch, Match match, Player playerThatDidNotRespond) {
        checkEmailIsCorrect(
                match,
                playerThatDidNotRespond,
                mailbox.pop(playerThatDidNotRespond.getEmailAddress()),
                NotificationKind.Reminder,
                daysBeforeMatch);
    }

    protected String getContentStringFor(NotificationKind kindOfEmail) {
        switch (kindOfEmail) {
        case CanYouPlay:
            return "Can you play";
        case ConfirmationOfStandby:
            return "Brilliant, thanks, I'll be in touch shortly to confirm.";
        case ConfirmationOfAcceptance:
            return "Brilliant, your in";
        case ConfirmationOfDecline:
            return "Sorry you couldn't play";
        case Reminder:
            return "Sorry to bother you again";
        case StandBy:
            return "Can you standby";
        case StandDown:
            break;
        case MatchConfirmation:
            return "the details for";
        case MatchStatus:
            return "match status";
        default:
            break;
        }
        assertThat("Fixture didn't know how to match email of type " + kindOfEmail, false);
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

    protected Matcher<Instant> isDaysBeforeMatch(Integer daysBeforeMatch, Match match) {
        return new BaseMatcher<Instant>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("days till match " + daysBeforeMatch + " for match on "
                        + match.getStartDateTime());
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

    public void checkNotificationGoesToEligibleFirstPickPlayers(Match match) {
        selectedPlayers.stream().filter(p -> !selectedPlayersThatDeclined.contains(p))
                .forEach(player -> checkOutboundMessageIsCorrect(player, NotificationKind.CanYouPlay, match));
    }

    public void checkOutboundTeamCaptainEmailIsCorrect(NotificationKind kind, Match match, Player referencePlayer) {

        switch (kind) {
        case OutOfBandMessage:
            checkAdminAlert("Message requires attention");
            // need to check contents refers to match and reference players message content
        default:
            break;
        }

        throw new UnsupportedOperationException(String.format("Team captain email checking not implemented for %type",
                kind.toString()));
    }

    public Player allSelectedPlayersRespondExcept(Match match, Response response, Player exception) {
        log.debug("All players %s except %s", response, exception);
        aPlayerDoesNotRespond(exception, match);
        selectedPlayers.stream().filter(a -> a.equals(exception))
                .forEach(a -> aSelectedPlayerResponds(a, response.inverse()));
        return exception;
    }

    public void checkNotificationDoesNotGoToPlayerWhoDeclined() {
        for (final Player player : selectedPlayersThatDeclined) {
            assertThat("Mailbox of player who decline prior to match is empty",
                    mailbox.peek(player.getEmailAddress()), nullValue());
        }
    }

    public void checkThatThoseWhoSaidTheyCouldPlayAreAssignedToTheMatch() {
        for (final Player player : selectedPlayersThatAccepted) {
            checkPlayerIsAssignedToTheMatch(player);
        }
    }

    private void aPlayerDoesNotRespond(Player player, Match match) {
        checkOutboundMessageIsCorrect(player, NotificationKind.CanYouPlay, match);
        playersThatDidntRespond.add(player);
    }

    public Player aSelectedPlayerResponds(Player player, Response response) {
        switch (response) {
        case Decline:
            selectedPlayersThatDeclined.add(player);
            aPlayerRespondsWith(player, "No");
        case Accept:
            selectedPlayersThatAccepted.add(player);
            aPlayerRespondsWith(player, "Yes");
        }
        return player;
    }

    public Player aStandbyPlayerResponds(Player player, Response response) {
        if (response == Response.Accept) {
            standbyPlayersThatAccepted.add(player);
            aPlayerRespondsWith(player, "Yes");
        } else {
            standbyPlayersThatDeclined.add(player);
            aPlayerRespondsWith(player, "No");
        }

        return player;
    }

}
