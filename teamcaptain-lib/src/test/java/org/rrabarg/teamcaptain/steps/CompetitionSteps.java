package org.rrabarg.teamcaptain.steps;

import static java.util.Arrays.asList;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.getPlayerByFirstName;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.getPlayersByFirstName;
import static org.rrabarg.teamcaptain.domain.NotificationKind.MatchFulfilled;
import static org.rrabarg.teamcaptain.domain.NotificationKind.StandDown;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.Aliases;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Composite;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.rrabarg.teamcaptain.CompetitionFixture;
import org.rrabarg.teamcaptain.CompetitionFixture.Response;
import org.rrabarg.teamcaptain.NotificationStrategy;
import org.rrabarg.teamcaptain.demo.CompetitionBuilder;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.strategy.BasicNotificationStrategy;
import org.rrabarg.teamcaptain.strategy.ContactPreference;
import org.rrabarg.teamcaptain.strategy.SimpleGenderedSelectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CompetitionSteps {

    private static final int DAYS_TILL_MATCH_TILL_WINDOW_OPEN = 10;

    private final NotificationStrategy testNotificationStrategy = new BasicNotificationStrategy(7,
            DAYS_TILL_MATCH_TILL_WINDOW_OPEN, 4, 3,
            ContactPreference.emailOnly());

    @Inject
    Provider<CompetitionFixture> fixtureProvider;

    Logger log = LoggerFactory.getLogger(getClass().getName());

    private CompetitionBuilder builder;
    private Match match;
    private CompetitionFixture fixture;
    private Competition competition;

    @BeforeScenario
    public void setup() throws IOException, InterruptedException {
        fixture = fixtureProvider.get();
        builder = null;
        match = null;
        competition = null;
    }

    @AfterScenario
    public void teardown() throws IOException, InterruptedException {
        if ((competition != null) && (fixture != null)) {
            fixture.clearCompetition(competition);
        }
    }

    @Given("a competition requires %numberOfMen men and %numberOfLadies ladies per match")
    @Aliases(values = {
            "a competition requires %numberOfMen man and %numberOfLadies lady per match",
            "a competition requires %numberOfMen men and %numberOfLadies lady per match",
            "a competition requires %numberOfMen man and %numberOfLadies ladies per match"
    })
    public void givenACompetitionRequires(int numberOfMen, int numberOfLadies) {
        builder = new CompetitionBuilder()
                .withNotificationStrategy(testNotificationStrategy)
                .withSelectStrategy(new SimpleGenderedSelectionStrategy(2, 1));
    }

    @Given("the player pool consists of %players")
    public void givenThePlayerPoolConsistsOf(String players) {
        builder.withPlayerPool(getPlayersByFirstName(getPlayerFirstNamesFromText(players)));
    }

    @Given("a match is scheduled and is in the selection window")
    @Composite(steps = {
            "Given a match is scheduled",
            "Given the match is in the selection window" })
    public void givenAMatchIsScheduledAndIsInTheWindow() throws IOException {
        // nothing additional required
    }

    @Given("a match is scheduled")
    public void givenAMatchIsScheduled() throws IOException {
        competition = builder.build();
        match = competition.getSchedule().getMatches().get(0);
    }

    @Given("the match is in the selection window")
    public void theMatchIsInTheSelectionWindow() throws IOException {
        fixture.fixDateTimeBeforeMatch(DAYS_TILL_MATCH_TILL_WINDOW_OPEN, ChronoUnit.DAYS, match);
        fixture.refreshWorkflows(competition);
    }

    @Given("%players is selected and %acceptsOrDecline")
    @Aliases(values = {
            "%players is selected to standby and %acceptOrDecline",
            "%players are selected to standby and %acceptOrDecline",
            "%players are selected and %acceptsOrDecline",
            "%players then %acceptOrDecline" })
    public void givenPlayersRespond(String players, String acceptOrDecline) {
        whenPlayersRespond(players, acceptOrDecline);
    }

    @Given("time elapses till %daysBeforeMatch days before the match")
    public void timeElapses(int daysBeforeMatch) {
        fixture.pumpWorkflowsTillXDaysBeforeMatch(daysBeforeMatch, match);
    }

    @When("time elapses till %daysBeforeMatch days before the match")
    public void whenTimeElapses(int daysBeforeMatch) {
        timeElapses(daysBeforeMatch);
    }

    @When("%players is selected and %acceptsOrDecline")
    @Aliases(values = {
            "%players is selected to standby and %acceptOrDecline",
            "%players are selected to standby and %acceptOrDecline",
            "%players are selected and %acceptsOrDecline",
            "%players then %acceptOrDecline" })
    public void whenPlayersRespond(String players, String acceptOrDecline) {
        getPlayers(players).stream().forEach(p -> fixture.aPlayerResponds(p, getResponse(acceptOrDecline)));
    }

    @Then("the match is confirmed with %players")
    public void theMatchIsConfirmed(String players) {
        final List<Player> selectedPlayersThatAccepted = getPlayers(players);
        fixture.checkMatchConfirmationSentToAllConfirmedPlayers(match, selectedPlayersThatAccepted);
        fixture.checkMatchConfirmationContainsListOfPlayerInTeam(selectedPlayersThatAccepted);
        fixture.checkMatchConfirmationContainsTheMatchDetails(match, selectedPlayersThatAccepted);
        fixture.checkOutboundTeamCaptainMessageIsCorrect(competition, MatchFulfilled, match, null,
                selectedPlayersThatAccepted);
    }

    @Then("%players are stood down")
    public void thenPlayersAreStoodDown(String players) {
        getPlayers(players).stream().forEach(p -> fixture.checkOutboundMessageIsCorrect(p, StandDown, match));
    }

    @Then("the team captain is notified of the %type message from the system")
    public void checkTeamCaptainNotification(String type) {
        fixture.checkOutboundTeamCaptainMessageIsCorrect(competition, getTeamCaptainNotificationKind(type), match,
                null, null);
    }

    @Then("%players are sent a detailed match status")
    public void checkPlayersAreSentMatchStatus(String players) {
        final List<Player> allNotifiedPlayers = getPlayers(players);
        allNotifiedPlayers.stream().forEach(
                p -> fixture.checkHasReceivedDetailedMatchStatus(p, match, allNotifiedPlayers));
    }

    private Response getResponse(String declinesOrAccepts) {

        final String response = declinesOrAccepts.trim().toLowerCase();

        if (response.startsWith("decline")) {
            return Response.Decline;
        } else if (response.startsWith("accept")) {
            return Response.Accept;
        }

        throw new UnsupportedOperationException("Unparsable response string " + response);
    }

    private NotificationKind getTeamCaptainNotificationKind(String type) {

        switch (type.trim().toLowerCase()) {
        case "unexpected":
            return NotificationKind.OutOfBandMessage;
        case "standby alert":
            return NotificationKind.StandbyPlayersNotified;
        case "match confirmation":
            return NotificationKind.MatchFulfilled;
        case "insufficient players":
            return NotificationKind.InsufficientPlayers;
        }

        throw new UnsupportedOperationException("Unparsable team captain notification kind " + type);
    }

    private List<Player> getPlayers(String string) {
        return getPlayerFirstNamesFromText(string).stream().map(t -> getPlayerByFirstName(t))
                .collect(Collectors.toList());
    }

    private List<String> getPlayerFirstNamesFromText(String string) {
        return asList(string.split(" ")).stream()
                .map(s -> s.replaceAll(",", ""))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(a -> !"and".equals(a))
                .collect(Collectors.toList());
    }

    // private int getNumber(String numberOfPlayers) {
    //
    // final int n = numberOfPlayers.indexOf(' ');
    // final String s = n >= 0 ? numberOfPlayers.substring(0, n - 1).trim() : s;
    //
    // switch (s.trim().toLowerCase()) {
    // case "one":
    // return 1;
    // case "two":
    // return 2;
    // case "three":
    // return 3;
    // default:
    // return Integer.parseInt(numberOfPlayers);
    // }
    // }
}