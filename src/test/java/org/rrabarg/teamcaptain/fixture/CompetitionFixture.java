package org.rrabarg.teamcaptain.fixture;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
import org.rrabarg.teamcaptain.service.ScheduleService;
import org.rrabarg.teamcaptain.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompetitionFixture {

    Logger log = LoggerFactory.getLogger(this.getClass());

    String aTitle = "A test match";
    LocalDate aDate = LocalDate.of(2014, 3, 20);
    LocalTime aTime = LocalTime.of(20, 00);
    LocalTime aEndTime = aTime.plus(3, HOURS);
    String aLocationFirstLine = "1 some street";
    String aLocationPostcode = "EH1 1YA";

    @Autowired
    CompetitionService competitionService;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    TestClockFactory clockFactory;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    TestMailbox mailbox;

    private final Player joe = new Player("Joe", "Ninety", Gender.Male, "joeninety@nomail.com", "3456");

    private final Player stacy = new Player("Stacy", "Fignorks", Gender.Female, "stacyfignorks@nomail.com", "2345");

    private final Player peter = new Player("Peter", "Pan", Gender.Male, "peterpan@nomail.com", "1234");

    private Competition competition;

    private Match match;

    private Player playerThatCanPlayInMatch;

    private Player playerThatCannotPlayInMatch;

    private final SelectionStrategy testStrategy = new SimpleGenderedStrategy(1, 1, 7, 10);

    private Player playerThatDidNotRespond;

    public void clearCompetition() {

        if (competition == null) {
            return;
        }

        competitionService.clearCompetition(competition);
    }

    private Match standardMatch() {
        return new MatchBuilder().withTitle(aTitle)
                .withStart(aDate, aTime)
                .withEnd(aDate, aEndTime)
                .withLocation(aLocationFirstLine, aLocationPostcode).build();
    }

    public void setup() {
        clearCompetition();
    }

    public void teardown() {
        clearCompetition();
    }

    public void fixDateTimeBeforeMatch(long amount, ChronoUnit unit) {
        clockFactory.fixInstant(aDate.atTime(aTime).minus(amount, unit).atZone(ZoneId.systemDefault()).toInstant());
        log.debug("Fixing time to " + amount + " days before the match " + clockFactory.clock().instant());

    }

    public void refreshWorkflows() throws IOException {
        workflowService.refresh(competition.getName());
    }

    public void createCompetition() {
        competition = standardCompetition();
        competitionService.saveCompetition(competition);
    }

    public void checkAllCanYouPlayNotificationsWereSent() {
        Stream.of(joe, stacy).forEach(player -> assertOutboundEmailIsCorrect(player, Kind.CanYouPlay));
    }

    private void assertOutboundEmailIsCorrect(Player player, Kind kind) {
        assertEmailIsCorrect(match, player, mailbox.pop(player.getEmailAddress()), kind, null);
    }

    private void assertEmailIsCorrect(Match match, Player player, Email email, Kind kindOfEmail, Integer daysBeforeMatch) {
        assertThat("Email must not be null for player " + player, email, notNullValue());
        assertThat("Email Subject mismatch for player " + player, email.getSubject(), containsString(match.getTitle()));
        assertThat("Email Body mismatch for player " + player, email.getBody(),
                containsString(getStringFor(kindOfEmail)));
        if (daysBeforeMatch != null) {
            assertThat("Email Date is not correct for player " + player, email.getTimestamp(),
                    isDaysBeforeMatch(daysBeforeMatch));
        }
    }

    private String getStringFor(Kind kindOfEmail) {
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
            break;
        case StandDown:
            break;
        default:
            break;
        }
        return null;
    }

    private Competition standardCompetition() {
        return new Competition(getTestCompetitionName(),
                standardSchedule(getTestCompetitionName()),
                standardPoolOfPlayers(getTestCompetitionName()),
                testStrategy);
    }

    private PoolOfPlayers standardPoolOfPlayers(String competitionName) {
        return new PoolOfPlayers(joe, stacy, peter);
    }

    private Schedule standardSchedule(String scheduleName) {
        match = standardMatch();
        return new Schedule(match);
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

    public void theRemainingPlayersSayTheyCanPlay() {
        mailbox.email().from(playerThatDidNotRespond.getEmailAddress()).subject("any text").body("Yes").send();
    }

    public void aPlayerInThePoolSaysTheyCanPlay() {
        playerThatCanPlayInMatch = stacy;
        mailbox.email().from(playerThatCanPlayInMatch.getEmailAddress()).subject("any text").body("Yes").send();
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

    public void checkAcknowledgementGoesToPlayerWhoAccepted() {
        assertEmailIsCorrect(match, playerThatCanPlayInMatch, mailbox.pop(playerThatCanPlayInMatch.getEmailAddress()),
                Kind.ConfirmationOfAcceptance, null);
    }

    public void aPlayerInThePoolSaysTheyCannotPlay() {
        playerThatCannotPlayInMatch = joe;
        mailbox.email().from(playerThatCannotPlayInMatch.getEmailAddress()).subject("any text").body("No").send();
    }

    public void checkAcknowledgementGoesToPlayerWhoDeclined() {
        assertEmailIsCorrect(match, playerThatCannotPlayInMatch,
                mailbox.pop(playerThatCannotPlayInMatch.getEmailAddress()),
                Kind.ConfirmationOfDecline, null);
    }

    public void nextAppropriatePlayerInThePoolIsNotified() {
        assertOutboundEmailIsCorrect(peter, Kind.CanYouPlay);
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

    private Instant getMatchInstant() {
        return aDate.atTime(aTime).atZone(ZoneId.systemDefault()).toInstant();
    }

    public void allButOneFirstPickPlayersRespond() {
        log.debug("All but one first pick players respond");
        assertOutboundEmailIsCorrect(stacy, Kind.CanYouPlay);
        aPlayerInThePoolSaysTheyCanPlay();
        playerThatDidNotRespond = joe;
    }

    public void checkThereAreNoRemindersForPlayersThatDidNotRespond() {

        final Optional<Email> findAnyReminder = mailbox.viewAll(playerThatDidNotRespond.getEmailAddress())
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())).findFirst();

        assertThat(findAnyReminder.get().getTimestamp(), isDaysBeforeMatch(5));
    }

    public void checkDailyReminderIsSentForDaysBeforeMatch(int daysBeforeMatch) {
        for (int i = 0; i < daysBeforeMatch; i++) {
            checkReminderOnDay(i);
        }
    }

    private void checkReminderOnDay(int daysBeforeMatch) {
        assertEmailIsCorrect(
                match,
                playerThatDidNotRespond,
                mailbox.pop(playerThatDidNotRespond.getEmailAddress()),
                Kind.Reminder,
                daysBeforeMatch);
    }

    private Matcher<Instant> isDaysBeforeMatch(Integer daysBeforeMatch) {
        return new BaseMatcher<Instant>() {

            @Override
            public boolean matches(Object item) {

                if ((item == null) || (!(item instanceof Instant))) {
                    return false;
                }

                return getDaysTillMatch((Instant) item) == daysBeforeMatch;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("days till match " + daysBeforeMatch + " for match on "
                        + match.getStartDateTime());
            }
        };
    }

    private long getDaysTillMatch() {
        return getDaysTillMatch(clockFactory.clock().instant());
    }

    private long getDaysTillMatch(Instant instant) {
        return Duration.between(instant, getMatchInstant()).toDays();
    }

}
