package org.rrabarg.teamcaptain.fixture;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import org.rrabarg.teamcaptain.NotificationStrategy;
import org.rrabarg.teamcaptain.SelectionStrategy;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerPool;
import org.rrabarg.teamcaptain.domain.PlayerState;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.rrabarg.teamcaptain.service.MatchBuilder;
import org.rrabarg.teamcaptain.strategy.BasicNotificationStrategy;
import org.rrabarg.teamcaptain.strategy.ContactPreference;
import org.rrabarg.teamcaptain.strategy.SimpleGenderedSelectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class SimpleGenericCompetitionFixture extends BaseFixture {

    private static Logger log = LoggerFactory.getLogger(SimpleGenericCompetitionFixture.class);

    private final Player[] firstPick = new Player[] { joe, stacy };
    private final SelectionStrategy testSelectionStrategy = new SimpleGenderedSelectionStrategy(1, 1);
    private final NotificationStrategy testNotificationStrategy = new BasicNotificationStrategy(7, 10, 4,
            ContactPreference.emailOnly());
    private final LocalDate aDate = LocalDate.of(2014, 3, 20);
    private final LocalTime aTime = LocalTime.of(20, 00);
    private final LocalTime aEndTime = aTime.plus(3, HOURS);
    private final String aLocationFirstLine = "1 some street";
    private final String aLocationPostcode = "EH1 1YA";
    private final String aTitle = "A test match";

    @Override
    public Competition createCompetitionImpl() {
        return standardCompetition();
    }

    @Override
    protected void setupScenarioImpl() {
        firstPickPlayers.addAll(Arrays.asList(firstPick));
    }

    private Competition standardCompetition() {
        return new Competition(getTestCompetitionName(),
                standardSchedule(getTestCompetitionName()),
                standardPlayerPool(getTestCompetitionName()),
                testSelectionStrategy, testNotificationStrategy, teamCaptain);
    }

    private Match standardMatch() {
        return new MatchBuilder().withTitle(aTitle)
                .withStart(aDate, aTime)
                .withEnd(aDate, aEndTime)
                .withLocation(aLocationFirstLine, aLocationPostcode).build();
    }

    private PlayerPool standardPlayerPool(String competitionName) {
        return new PlayerPool(joe, stacy, peter);
    }

    private Schedule standardSchedule(String scheduleName) {
        match = standardMatch();
        return new Schedule(match);
    }

    public void allButOneFirstPickPlayersRespond(Match match) {
        log.debug("All but one first pick players respond");
        checkOutboundEmailIsCorrect(stacy, NotificationKind.CanYouPlay, match);
        aPlayerInThePoolSaysTheyCanPlay();
        playersThatDidntRespond.add(joe);
    }

    public void aPlayerInThePoolSaysTheyCannotPlay() {
        playersThatCannotPlayInMatch.add(joe);
        aPlayerRespondsWith(joe, "No");
    }

    public void aPlayerWhoDoesntHaveAnEligibleSubstituteDeclines() {
        playersThatCannotPlayInMatch.add(stacy);
        aPlayerRespondsWith(stacy, "No");
    }

    public void aPlayerInThePoolSaysTheyCanPlay() {
        allConfirmedPlayers.add(stacy);
        aPlayerRespondsWith(stacy, "Yes");
    }

    public void checkNotificationGoesToNextAppropriatePlayerInThePool(Match match) {
        checkOutboundEmailIsCorrect(peter, NotificationKind.CanYouPlay, match);
    }

    public void checkNextAppropriatePlayerInThePoolIsNotifiedOfStandby(Match match) {
        checkOutboundEmailIsCorrect(peter, NotificationKind.StandBy, match);
    }

    public void aFirstPickPoolMemberHasAlreadyDeclined(Match match) throws IOException {
        match.setPlayerState(joe, PlayerState.Declined);
        playersThatCannotPlayInMatch.add(joe);
        scheduleService.updateMatch(match);
    }

    public void checkNotificationDoesNotGoToPlayerWhoDeclined() {
        assertThat("Mailbox of player who decline prior to match is empty",
                mailbox.peek(joe.getEmailAddress()), nullValue());
    }

    public void checkThatThoseWhoSaidTheyCouldPlayAreAssignedToTheMatch() {
        checkThatThoseWhoSaidTheyCouldPlayAreAssignedToTheMatch(stacy);
    }
}
