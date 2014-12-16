package org.rrabarg.teamcaptain.fixture;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.rrabarg.teamcaptain.SelectionStrategy;
import org.rrabarg.teamcaptain.TestClockFactory;
import org.rrabarg.teamcaptain.TestMailbox;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Gender;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.MatchState;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerNotification.Kind;
import org.rrabarg.teamcaptain.domain.PlayerState;
import org.rrabarg.teamcaptain.domain.PoolOfPlayers;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.rrabarg.teamcaptain.domain.SimpleGenderedStrategy;
import org.rrabarg.teamcaptain.service.CompetitionService;
import org.rrabarg.teamcaptain.service.Email;
import org.rrabarg.teamcaptain.service.MatchBuilder;
import org.rrabarg.teamcaptain.service.PlayerNotificationRepository;
import org.rrabarg.teamcaptain.service.ScheduleService;
import org.rrabarg.teamcaptain.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompetitionFixture {

    private static Logger log = LoggerFactory.getLogger(CompetitionFixture.class);

    private final Player stacy = new Player("Stacy", "Fignorks", Gender.Female, "stacyfignorks@nomail.com", "2345");
    private final Player joe = new Player("Joe", "Ninety", Gender.Male, "joeninety@nomail.com", "3456");
    private final Player peter = new Player("Peter", "Pan", Gender.Male, "peterpan@nomail.com", "1234");
    private final Player[] firstPickPlayers = new Player[] { joe, stacy };
    private final SelectionStrategy testStrategy = new SimpleGenderedStrategy(1, 1, 7, 10, 4);
    private final LocalDate aDate = LocalDate.of(2014, 3, 20);
    private final String adminstratorEmailAddress = "grahambarrgraham@gmail.com";
    private final LocalTime aTime = LocalTime.of(20, 00);
    private final LocalTime aEndTime = aTime.plus(3, HOURS);
    private final String aLocationFirstLine = "1 some street";
    private final String aLocationPostcode = "EH1 1YA";
    private final String aTitle = "A test match";

    private final List<Player> allConfirmedPlayers = new ArrayList<Player>();
    private Competition competition;
    private Match match;
    private Player playerThatCannotPlayInMatch;
    private Player playerThatDidNotRespond;

    @Autowired
    TestClockFactory clockFactory;

    @Autowired
    CompetitionService competitionService;

    @Autowired
    TestMailbox mailbox;

    @Autowired
    PlayerNotificationRepository playerNotificationRepository;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    WorkflowService workflowService;

    public void allButOneFirstPickPlayersRespond() {
        log.debug("All but one first pick players respond");
        checkOutboundEmailIsCorrect(stacy, Kind.CanYouPlay);
        aPlayerInThePoolSaysTheyCanPlay();
        playerThatDidNotRespond = joe;
    }

    public void allFirstPickPlayersConfirmTheyCanPlay() {
        allConfirmedPlayers.addAll(Arrays.asList(firstPickPlayers));
        Stream.of(firstPickPlayers).forEach(player -> aPlayerRespondsWith(player, "Yes"));
    }

    public void aPlayerInThePoolSaysTheyCannotPlay() {
        playerThatCannotPlayInMatch = joe;
        aPlayerRespondsWith(joe, "No");
    }

    public void aPlayerWhoDoesntHaveAnEligibleSubstituteDeclines() {
        playerThatCannotPlayInMatch = stacy;
        aPlayerRespondsWith(stacy, "No");
    }

    public void aPlayerInThePoolSaysTheyCanPlay() {
        allConfirmedPlayers.add(stacy);
        aPlayerRespondsWith(stacy, "Yes");
    }

    public void checkAcknowledgementGoesToPlayerWhoAccepted() {
        allConfirmedPlayers.forEach(player ->
                checkEmailIsCorrect(match, player,
                        mailbox.pop(player.getEmailAddress()),
                        Kind.ConfirmationOfAcceptance, null));
    }

    public void checkAcknowledgementGoesToPlayerWhoDeclined() {
        checkEmailIsCorrect(match, playerThatCannotPlayInMatch,
                mailbox.pop(playerThatCannotPlayInMatch.getEmailAddress()),
                Kind.ConfirmationOfDecline, null);
    }

    public void checkAllCanYouPlayNotificationsWereSent() {
        Stream.of(firstPickPlayers).forEach(player -> checkOutboundEmailIsCorrect(player, Kind.CanYouPlay));
    }

    public void checkAnAdministratorMatchConfirmationIsRaised() {
        final Email email = mailbox.peek(adminstratorEmailAddress);
        assertThat("While checking that outbound admin email confirmation, found email null", email, notNullValue());
        assertThat("While checking that outbound admin email confirmation body, found body null", email.getBody(),
                notNullValue());
        assertThat("Subject should contain string 'confirmation'", email.getSubject().toLowerCase(),
                containsString("confirmation"));
        checkOutboundEmailContainsListOfPlayers(email);
        checkOutboundEmailContainsMatchDetails(email);
    }

    public void checkAnAdminstratorStandbyAlertIsRaised() {
        checkAdminAlert("standby");
    }

    public void checkAnAdminstratorInsufficientPlayersAlertIsRaised() {
        checkAdminAlert("insufficient");
    }

    private void checkAdminAlert(String expectedText) {
        final Email email = mailbox.pop(adminstratorEmailAddress);
        assertThat("Admin Email must not be null", email, notNullValue());
        assertThat("Admin alert should be ", email.getSubject().toLowerCase(), containsString(expectedText));
    }

    public void checkDailyReminderIsSentForDaysBeforeMatch(int daysBeforeMatch) {
        for (int i = 0; i < daysBeforeMatch; i++) {
            checkReminderOnDay(i);
        }
    }

    public void checkThatThoseWhoSaidTheyCouldPlayAreAssignedToTheMatch() {
        final Optional<Match> thematch = scheduleService.findByName(competition.getName()).getUpcomingMatches()
                .stream().findFirst();

        assertThat("The match must exist", thematch.isPresent());
        assertThat("The match must be in notified state", MatchState.FirstPickPlayersNotified == thematch.get()
                .getMatchState());
        assertThat("The player should be in the accepted state", PlayerState.Accepted == thematch.get()
                .getPlayerState(stacy));
    }

    public void checkThereAreNoRemindersForPlayersThatDidNotRespond() {

        final Optional<Email> findAnyReminder = mailbox.viewAll(playerThatDidNotRespond.getEmailAddress())
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())).findFirst();

        assertThat(findAnyReminder.get().getTimestamp(), isDaysBeforeMatch(5));
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
    }

    public void createCompetition() {
        competition = standardCompetition();
        competitionService.saveCompetition(competition);
    }

    public void fixDateTimeBeforeMatch(long amount, ChronoUnit unit) {
        clockFactory.fixInstant(aDate.atTime(aTime).minus(amount, unit).atZone(ZoneId.systemDefault()).toInstant());
        log.debug("Fixing time to " + amount + " days before the match " + clockFactory.clock().instant());

    }

    public void checkMatchConfirmationContainsListOfPlayerInTeam() {
        allConfirmedPlayers.stream().forEach(
                player -> checkOutboundEmailContainsListOfPlayers(mailbox.peek(player.getEmailAddress())));
    }

    public void checkMatchConfirmationContainsTheMatchDetails() {
        allConfirmedPlayers.stream().forEach(
                player -> checkOutboundEmailContainsMatchDetails(mailbox.peek(player.getEmailAddress())));
    }

    public void checkMatchConfirmationSentToAllConfirmedPlayers() {
        allConfirmedPlayers.stream().forEach(player -> checkOutboundEmailIsCorrect(player, Kind.MatchConfirmation));
    }

    public void checkNotificationGoesToNextAppropriatePlayerInThePool() {
        checkOutboundEmailIsCorrect(peter, Kind.CanYouPlay);
    }

    public void checkNextAppropriatePlayerInThePoolIsNotifiedOfStandby() {
        checkOutboundEmailIsCorrect(peter, Kind.StandBy);
    }

    public void pumpWorkflows() {
        log.debug("Pumping the workflow");
        workflowService.pump();
    }

    public void pumpWorkflowsTillXDaysBeforeMatch(final int daysTillMatchToStartReminders) {
        long daysTillMatch = getDaysTillMatch();
        while (--daysTillMatch >= daysTillMatchToStartReminders) {
            fixDateTimeBeforeMatch(daysTillMatch, ChronoUnit.DAYS);
            pumpWorkflows();
        }
    }

    public void refreshWorkflows() throws IOException {
        workflowService.refresh(competition.getName());
    }

    public void setupScenario() {
        clearCompetition();
        clearScenarioState();
        allConfirmedPlayers.clear();
    }

    public void teardown() {
        clearCompetition();
    }

    public void theRemainingPlayersSayTheyCanPlay() {
        aPlayerRespondsWith(playerThatDidNotRespond, "Yes");
    }

    private void aPlayerRespondsWith(Player player, String response) {
        mailbox.email().from(player.getEmailAddress()).subject("any text").body(response).send();
    }

    private void checkEmailIsCorrect(Match match, Player player, Email email, Kind kindOfEmail, Integer daysBeforeMatch) {
        assertThat("Email must not be null for player " + player, email, notNullValue());
        assertThat("Email Subject mismatch for player " + player, email.getSubject(), containsString(match.getTitle()));
        assertThat("Email Body mismatch for player " + player, email.getBody(),
                containsString(getContentStringFor(kindOfEmail)));
        if (daysBeforeMatch != null) {
            assertThat("Email Date is not correct for player " + player, email.getTimestamp(),
                    isDaysBeforeMatch(daysBeforeMatch));
        }
    }

    private void checkOutboundEmailContainsListOfPlayers(Email email) {
        assertThat("While checking that outbound email contained list of team's players, found email null", email,
                notNullValue());
        allConfirmedPlayers.stream().forEach(
                player -> assertThat("Email body must contain the players name", email.getBody(),
                        containsString(player.getKey())));
    }

    private void checkOutboundEmailContainsMatchDetails(Email email) {
        assertThat("While checking that outbound email contained match details, found email null", email,
                notNullValue());

        assertThat("Email body must contain the match location", email.getBody(),
                containsString(match.getLocation().toString()));

        assertThat("Email body must contain the match title", email.getBody(),
                containsString(match.getTitle().toString()));

        assertThat("Email body must contain the match date", email.getBody(),
                containsString(match.getStartDateTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))));

        assertThat("Email body must contain the match start time", email.getBody(),
                containsString(match.getStartDateTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))));

        if (match.getTravelDetails() != null) {
            assertThat("Email body must contain the match travel details", email.getBody(),
                    containsString(match.getTravelDetails()));
        }
    }

    private void checkOutboundEmailIsCorrect(Player player, Kind kind) {
        checkEmailIsCorrect(match, player, mailbox.peek(player.getEmailAddress()), kind, null);
    }

    private void checkReminderOnDay(int daysBeforeMatch) {
        checkEmailIsCorrect(
                match,
                playerThatDidNotRespond,
                mailbox.pop(playerThatDidNotRespond.getEmailAddress()),
                Kind.Reminder,
                daysBeforeMatch);
    }

    private String getContentStringFor(Kind kindOfEmail) {
        switch (kindOfEmail) {
        case CanYouPlay:
            return "Can you play";
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
        default:
            break;
        }
        assertThat("Fixture didn't know how to match email of type " + kindOfEmail, false);
        return null;
    }

    private long getDaysTillMatch() {
        return getDaysTillMatch(clockFactory.clock().instant());
    }

    private long getDaysTillMatch(Instant instant) {
        return Duration.between(instant, getMatchInstant()).toDays();
    }

    private Instant getMatchInstant() {
        return aDate.atTime(aTime).atZone(ZoneId.systemDefault()).toInstant();
    }

    private String getTestCompetitionName() {
        return "Test competition-" + getUniqueNameForHost();
    }

    private String getUniqueNameForHost() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (final UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private Matcher<Instant> isDaysBeforeMatch(Integer daysBeforeMatch) {
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

                return getDaysTillMatch((Instant) item) == daysBeforeMatch;
            }
        };
    }

    private Competition standardCompetition() {
        return new Competition(getTestCompetitionName(),
                standardSchedule(getTestCompetitionName()),
                standardPoolOfPlayers(getTestCompetitionName()),
                testStrategy);
    }

    private Match standardMatch() {
        return new MatchBuilder().withTitle(aTitle)
                .withStart(aDate, aTime)
                .withEnd(aDate, aEndTime)
                .withLocation(aLocationFirstLine, aLocationPostcode).build();
    }

    private PoolOfPlayers standardPoolOfPlayers(String competitionName) {
        return new PoolOfPlayers(joe, stacy, peter);
    }

    private Schedule standardSchedule(String scheduleName) {
        match = standardMatch();
        return new Schedule(match);
    }

    public void aFirstPickPoolMemberHasAlreadyDeclined() throws IOException {
        match.setPlayerState(joe, PlayerState.Declined);
        playerThatCannotPlayInMatch = joe;
        scheduleService.updateMatch(match);
    }

    public void checkNotificationDoesNotGoToPlayerWhoDeclined() {
        assertThat("Mailbox of player who decline prior to match is empty",
                mailbox.peek(joe.getEmailAddress()), nullValue());
    }

    public void checkNotificationGoesToEligibleFirstPickPlayers() {
        Stream.of(firstPickPlayers).filter(p -> !p.equals(playerThatCannotPlayInMatch))
                .forEach(player -> checkOutboundEmailIsCorrect(player, Kind.CanYouPlay));
    }
}
