package org.rrabarg.teamcaptain.steps;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Pending;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.fixture.CambridgeLeagueCompetitionFixture;
import org.rrabarg.teamcaptain.fixture.SimpleGenericCompetitionFixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gdata.util.ServiceException;

@Component
public class ArrangeMatchSteps extends Steps {

    @Autowired
    SimpleGenericCompetitionFixture genericFixture;

    @Autowired
    CambridgeLeagueCompetitionFixture cambsLeagueFixture;

    Logger log = LoggerFactory.getLogger(getClass().getName());

    Match match;

    @BeforeScenario
    public void setup() throws IOException, InterruptedException {
        match = null;
        genericFixture.setupScenario();
    }

    @AfterStories
    public void teardown() throws IOException, InterruptedException {
        genericFixture.teardownStory();
        log.info("teardown complete");
        match = null;
    }

    @Given("a match is scheduled")
    public void givenAMatchIsScheduled() throws IOException, ServiceException, InterruptedException {
        final Competition competition = genericFixture.createCompetition();
        match = competition.getSchedule().getMatches().get(0);
    }

    @When("it is 10 days before the match")
    public void whenItIs10DaysBeforeTheMatch() throws IOException {
        genericFixture.fixDateTimeBeforeMatch(10, ChronoUnit.DAYS, match);
        genericFixture.refreshWorkflows();
    }

    @Then("an availability notification is sent to the first pick members")
    public void thenAnAvailabilityNotificationIsSentToTheFirstPickMembers() {
        genericFixture.checkAllCanYouPlayNotificationsWereSent(match);
    }

    @Given("notifications have been sent out to the proposed team members")
    public void givenNotificationsHaveBeenSentOutToTheProposedTeamMembers() throws IOException {
        whenItIs10DaysBeforeTheMatch();
        genericFixture.checkAllCanYouPlayNotificationsWereSent(match);
    }

    @When("a team member acknowledges their availability")
    public void whenATeamMemberAcknowledgesTheirAvailability() {
        genericFixture.aPlayerInThePoolSaysTheyCanPlay();
    }

    @Then("they are assigned to the match")
    public void thenTheyAreAssignedToTheMatch() {
        genericFixture.checkThatThoseWhoSaidTheyCouldPlayAreAssignedToTheMatch();
    }

    @Then("an acceptance acknowledgement notification goes to the player")
    public void thenAnAcknowledgementNotificationGoesToThePlayer() {
        genericFixture.checkAcknowledgementGoesToPlayerWhoAccepted(match);
    }

    @Given("notifications have been sent out to the first pick players")
    public void givenNotificationsHaveBeenSentOutToTheFirstPickPlayers() throws IOException {
        givenNotificationsHaveBeenSentOutToTheProposedTeamMembers();
    }

    @When("a player responds that they are not available")
    public void whenAPlayerRespondsThatTheyAreNotAvailable() {
        genericFixture.aPlayerInThePoolSaysTheyCannotPlay();
    }

    @Then("a notification goes out to the next appropriate player in the pool")
    public void thenANotificationGoesOutToTheNextAppropriatePlayerInThePool() {
        genericFixture.checkNotificationGoesToNextAppropriatePlayerInThePool(match);
    }

    @Then("a decline acknowledgement notification goes to the player")
    public void thenADeclineAcknowledgementNotificationGoesToThePlayer() {
        genericFixture.checkAcknowledgementGoesToPlayerWhoDeclined(match);
    }

    @Given("it is 10 days before the match")
    public void givenItIs10DaysBeforeTheMatch() throws IOException {
        whenItIs10DaysBeforeTheMatch();
    }

    @Given("all but one first pick players responds")
    public void givenAllButOneFirstPickPlayersResponds() throws IOException {
        genericFixture.allButOneFirstPickPlayersRespond(match);
    }

    @When("times elapses till the match")
    public void whenTimesElapsesTillTheMatch() {
        genericFixture.pumpWorkflowsTillXDaysBeforeMatch(0, match);
    }

    @Then("a daily reminder is sent to the non-responding player from 7 days before the match")
    public void thenADailyReminderIsSentToTheNonrespondingPlayerFrom7DaysBeforeTheMatch() {
        genericFixture.checkDailyReminderIsSentForDaysBeforeMatch(7, match);
    }

    @Given("times elapses till the 5 days before the match")
    public void givenTimesElapsesTillThe5DaysBeforeTheMatch() {
        genericFixture.pumpWorkflowsTillXDaysBeforeMatch(5, match);
    }

    @When("the remaining team member acknowledges their availability")
    public void whenTheRemainingTeamMemberAcknowledgesTheirAvailability() {
        genericFixture.theRemainingPlayersSayTheyCanPlay();
    }

    @Then("no further reminders are sent to the player")
    public void thenNoFurtherRemindersAreSentToThePlayer() {
        genericFixture.pumpWorkflowsTillXDaysBeforeMatch(0, match);
        genericFixture.checkThereAreNoRemindersForPlayersThatDidNotRespond(match);
    }

    @When("times elapses till the 4 days before the match")
    public void whenTimesElapsesTillThe4DaysBeforeTheMatch() {
        genericFixture.pumpWorkflowsTillXDaysBeforeMatch(4, match);
    }

    @Then("a standby notification goes out to the next appropriate player in the pool")
    public void thenAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
        genericFixture.checkNextAppropriatePlayerInThePoolIsNotifiedOfStandby(match);
    }

    @Then("an administrator standby alert is raised")
    public void thenAnAdministratorAlertIsRaised() {
        genericFixture.checkAnAdminstratorStandbyAlertIsRaised();
    }

    @When("sufficient players are assigned to the match")
    public void whenSufficientPlayersAreAssignedToTheMatch() throws IOException {
        givenNotificationsHaveBeenSentOutToTheProposedTeamMembers();
        genericFixture.allFirstPickPlayersConfirmTheyCanPlay();
    }

    @Then("a match confirmation notification is sent out to all notified players")
    public void thenAMatchConfirmationNotificationIsSentOutToAllNotifiedPlayers() {
        genericFixture.checkMatchConfirmationSentToAllConfirmedPlayers(match);
    }

    @Then("the confirmation contains the list of players assigned to the match")
    public void thenTheConfirmationContainsTheListOfPlayersAssignedToTheMatch() {
        genericFixture.checkMatchConfirmationContainsListOfPlayerInTeam();
    }

    @Then("the confirmation contains the match details")
    public void thenTheConfirmationContainsTheMatchDetails() {
        genericFixture.checkMatchConfirmationContainsTheMatchDetails(match);
    }

    @Then("an administration confirmation notification is raised")
    public void thenAnAdministrationConfirmationNotificationIsRaised() {
        genericFixture.checkAnAdministratorMatchConfirmationIsRaised(match);
    }

    @Given("a member of the pool is on holiday on the date of the match")
    public void givenAMemberOfThePoolIsOnHolidayOnTheDateOfTheMatch() throws IOException {
        genericFixture.aFirstPickPoolMemberHasAlreadyDeclined(match);
    }

    @Then("the player on holiday is not notified")
    public void thenThePlayerOnHolidayIsNotNotified() {
        genericFixture.checkNotificationDoesNotGoToPlayerWhoDeclined();
        genericFixture.checkNotificationGoesToEligibleFirstPickPlayers(match);
    }

    @When("there are insufficient eligible players to fulfill the match")
    public void givenAnThereAreInsufficientEligiblePlayersToFulfillTheMatch() throws IOException {
        genericFixture.aPlayerWhoDoesntHaveAnEligibleSubstituteDeclines();
    }

    @Then("an administrator insufficient players alert is raised")
    public void thenAnAdministratorInsufficientPlayersAlertIsRaised() {
        genericFixture.checkAnAdminstratorInsufficientPlayersAlertIsRaised();
    }

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
}
