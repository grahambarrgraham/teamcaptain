package org.rrabarg.teamcaptain.steps;

import java.io.IOException;

import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Pending;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.fixture.BaseFixture.Response;
import org.rrabarg.teamcaptain.fixture.CambridgeLeagueCompetitionFixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BadmintonCompSteps extends Steps {

    @Autowired
    CambridgeLeagueCompetitionFixture cambsLeagueFixture;

    Logger log = LoggerFactory.getLogger(getClass().getName());

    Match match;

    private Player theLastPlayerWhoReplied;
    private Player theFirstPickPlayerWhoReplied;
    private Player thePlayerStillToRespond;

    @Given("a mixed doubles match is scheduled")
    @Pending
    public void givenAMixedDoublesMatchIsScheduled() {
        final Competition competition = cambsLeagueFixture.createCompetition();
        match = competition.getSchedule().getMatches().get(0);
    }

    @Then("the 3 strongest eligible ladies are chosen from the pool")
    @Pending
    public void thenThe3StrongestEligibleLadiesAreChosenFromThePool() {
        // PENDING
    }

    @Then("the 3 strongest eligible men are chosen from the pool")
    @Pending
    public void thenThe3StrongestEligibleMenAreChosenFromThePool() {
        // PENDING
    }

    @Then("the 6 strongest eligible and available men are chosen from the pool")
    @Pending
    public void thenThe6StrongestEligibleAndAvailableMenAreChosenFromThePool() {
        // PENDING
    }

    @Then("the 6 strongest eligible and available ladies are chosen from the pool")
    @Pending
    public void thenThe6StrongestEligibleAndAvailableLadiesAreChosenFromThePool() {
        // PENDING
    }

    @Given("a Cambridge county match is scheduled")
    @Pending
    public void givenACambridgeCountyMatchIsScheduled() {
        // PENDING
    }

    @Then("the nominated players are chosen first")
    @Pending
    public void thenTheNominatedPlayersAreChosenFirst() {
        // PENDING
    }

    @Then("the nominated ladies are chosen")
    @Pending
    public void thenTheNominatedLadiesAreChosen() {
        // PENDING
    }

    @Then("the nominated men are chosen")
    @Pending
    public void thenTheNominatedMenAreChosen() {
        // PENDING
    }

    @Given("a nominated player is unavailable")
    @Pending
    public void givenANominatedPlayerIsUnavailable() {
        // PENDING
    }

    @Then("the next strongest player of the same gender is chosen")
    @Pending
    public void thenTheNextStrongestPlayerOfTheSameGenderIsChosen() {
        // PENDING
    }

    @When("players are chosen")
    @Pending
    public void whenPlayersAreChosen() {
        // PENDING
    }

    @Given("a Cambridge league mens doubles badminton match is scheduled")
    @Pending
    public void givenACambridgeLeagueMensDoublesBadmintonMatchIsScheduled() {
        // PENDING
    }

    @BeforeScenario
    public void setup() throws IOException, InterruptedException {
        match = null;
        cambsLeagueFixture.setupScenario();
    }

    @AfterStories
    public void teardown() throws IOException, InterruptedException {
        cambsLeagueFixture.teardownStory();
        log.info("teardown complete");
        match = null;
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
            return NotificationKind.MatchConfirmation;
        case "insufficient players":
            return NotificationKind.InsufficientPlayers;
        }

        throw new UnsupportedOperationException("Unparsable team captain notification kind " + type);
    }

}
