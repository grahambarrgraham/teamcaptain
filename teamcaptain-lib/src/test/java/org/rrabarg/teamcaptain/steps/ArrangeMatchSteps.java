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

    @Given("a player responds that they are not available")
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

    @Given("a notification goes out to the next appropriate player in the pool")
    @Pending
    public void givenANotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @When("a player responds that they are available")
    @Pending
    public void whenAPlayerRespondsThatTheyAreAvailable() {
      // PENDING
    }

    @Then("they become eligible again and are returned to the pool as the highest ranked substitute")
    @Pending
    public void thenTheyBecomeEligibleAgainAndAreReturnedToThePoolAsTheHighestRankedSubstitute() {
      // PENDING
    }

    @Then("they are notified that they are eligible again")
    @Pending
    public void thenTheyAreNotifiedThatTheyAreEligibleAgain() {
      // PENDING
    }


    Scenario: a player declines, then subsequently confirms, and is subsequently picked
    Given a match is scheduled
    And notifications have been sent out to the first pick players
    And a player responds that they are not available (PENDING)
    And a notification goes out to the next appropriate player in the pool (PENDING)
    When a player responds that they are available (PENDING)
    And the next appropriate player declines (PENDING)
    Then they are assigned to the match (NOT PERFORMED)
    @Given("a player responds that they are not available")
    @Pending
    public void givenAPlayerRespondsThatTheyAreNotAvailable() {
      // PENDING
    }

    @Given("a notification goes out to the next appropriate player in the pool")
    @Pending
    public void givenANotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @When("a player responds that they are available")
    @Pending
    public void whenAPlayerRespondsThatTheyAreAvailable() {
      // PENDING
    }

    @When("the next appropriate player declines")
    @Pending
    public void whenTheNextAppropriatePlayerDeclines() {
      // PENDING
    }


    Scenario: a standby player confirms, and then the original player declines, standby player is selected
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    When the next appropriate player accepts (PENDING)
    And the outstanding player declines (PENDING)
    Then the standby player is selected (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @When("the next appropriate player accepts")
    @Pending
    public void whenTheNextAppropriatePlayerAccepts() {
      // PENDING
    }

    @When("the outstanding player declines")
    @Pending
    public void whenTheOutstandingPlayerDeclines() {
      // PENDING
    }

    @Then("the standby player is selected")
    @Pending
    public void thenTheStandbyPlayerIsSelected() {
      // PENDING
    }


    Scenario: a standby player confirms, and then the original player fails to respond 2 days before match, standby player is selected
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    And the next appropriate player accepts (PENDING)
    When times elapses till the 2 days before the match (PENDING)
    Then the next appropriate player is selected (PENDING)
    And the outstanding player is automatically declined (PENDING)
    And the outstanding player is notified of the automatic decline (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("the next appropriate player accepts")
    @Pending
    public void andTheNextAppropriatePlayerAccepts() {
      // PENDING
    }

    @When("times elapses till the 2 days before the match")
    @Pending
    public void whenTimesElapsesTillThe2DaysBeforeTheMatch() {
      // PENDING
    }

    @Then("the next appropriate player is selected")
    @Pending
    public void thenTheNextAppropriatePlayerIsSelected() {
      // PENDING
    }

    @Then("the outstanding player is automatically declined")
    @Pending
    public void thenTheOutstandingPlayerIsAutomaticallyDeclined() {
      // PENDING
    }

    @Then("the outstanding player is notified of the automatic decline")
    @Pending
    public void thenTheOutstandingPlayerIsNotifiedOfTheAutomaticDecline() {
      // PENDING
    }


    Scenario: a standby player confirms, and then the original player confirms in time, original player is selected, standby is stood down
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    And the next appropriate player accepts (PENDING)
    When times elapses till the 3 days before the match (PENDING)
    Then the outstanding player is selected (PENDING)
    And the standby player is stood down (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("the next appropriate player accepts")
    @Pending
    public void andTheNextAppropriatePlayerAccepts() {
      // PENDING
    }

    @When("times elapses till the 3 days before the match")
    @Pending
    public void whenTimesElapsesTillThe3DaysBeforeTheMatch() {
      // PENDING
    }

    @Then("the outstanding player is selected")
    @Pending
    public void thenTheOutstandingPlayerIsSelected() {
      // PENDING
    }

    @Then("the standby player is stood down")
    @Pending
    public void thenTheStandbyPlayerIsStoodDown() {
      // PENDING
    }


    Scenario: original and standby player both fail to respond, next standby is notified
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    When times elapses till the 3 days before the match (PENDING)
    Then a standby notification goes out to the 2nd next appropriate player in the pool (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @When("times elapses till the 3 days before the match")
    @Pending
    public void whenTimesElapsesTillThe3DaysBeforeTheMatch() {
      // PENDING
    }

    @Then("a standby notification goes out to the 2nd next appropriate player in the pool")
    @Pending
    public void thenAStandbyNotificationGoesOutToThe2ndNextAppropriatePlayerInThePool() {
      // PENDING
    }


    Scenario: unconfirmed standbys are stood down before confirmed standbys
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    And times elapses till the 3 days before the match (PENDING)
    And a standby notification goes out to the 2nd next appropriate player in the pool (PENDING)
    And the 2nd standby player accepts (PENDING)
    When the original player declines (PENDING)
    Then the 2nd standby player is selected (PENDING)
    And the 1st standby player is stood down (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("times elapses till the 3 days before the match")
    @Pending
    public void andTimesElapsesTillThe3DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the 2nd next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToThe2ndNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("the 2nd standby player accepts")
    @Pending
    public void andThe2ndStandbyPlayerAccepts() {
      // PENDING
    }

    @When("the original player declines")
    @Pending
    public void whenTheOriginalPlayerDeclines() {
      // PENDING
    }

    @Then("the 2nd standby player is selected")
    @Pending
    public void thenThe2ndStandbyPlayerIsSelected() {
      // PENDING
    }

    @Then("the 1st standby player is stood down")
    @Pending
    public void thenThe1stStandbyPlayerIsStoodDown() {
      // PENDING
    }


    Scenario: acceptance stands down lowest ranked unconfirmed standby
    And it is 10 days before the match (PENDING)
    And all but two equivalent first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    And a standby notification goes out to the 2nd next appropriate player in the pool (PENDING)
    And times elapses till the 3 days before the match (PENDING)
    When one of the outstanding players accepts (PENDING)
    Then the 1st standby player is selected (PENDING)
    And the 2nd standby player is stood down (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but two equivalent first pick players responds")
    @Pending
    public void andAllButTwoEquivalentFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("a standby notification goes out to the 2nd next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToThe2ndNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("times elapses till the 3 days before the match")
    @Pending
    public void andTimesElapsesTillThe3DaysBeforeTheMatch() {
      // PENDING
    }

    @When("one of the outstanding players accepts")
    @Pending
    public void whenOneOfTheOutstandingPlayersAccepts() {
      // PENDING
    }

    @Then("the 1st standby player is selected")
    @Pending
    public void thenThe1stStandbyPlayerIsSelected() {
      // PENDING
    }

    @Then("the 2nd standby player is stood down")
    @Pending
    public void thenThe2ndStandbyPlayerIsStoodDown() {
      // PENDING
    }


    Scenario: acceptance stands down lowest ranked confirmed standby
    And it is 10 days before the match (PENDING)
    And all but two equivalent first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    And a standby notification goes out to the 2nd next appropriate player in the pool (PENDING)
    And the 1st standby player accepts (PENDING)
    And the 2nd standby player accepts (PENDING)
    When one of the outstanding players accepts (PENDING)
    Then the 1st standby player is selected (PENDING)
    And the 2nd standby player is stood down (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but two equivalent first pick players responds")
    @Pending
    public void andAllButTwoEquivalentFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("a standby notification goes out to the 2nd next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToThe2ndNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("the 1st standby player accepts")
    @Pending
    public void andThe1stStandbyPlayerAccepts() {
      // PENDING
    }

    @And("the 2nd standby player accepts")
    @Pending
    public void andThe2ndStandbyPlayerAccepts() {
      // PENDING
    }

    @When("one of the outstanding players accepts")
    @Pending
    public void whenOneOfTheOutstandingPlayersAccepts() {
      // PENDING
    }

    @Then("the 1st standby player is selected")
    @Pending
    public void thenThe1stStandbyPlayerIsSelected() {
      // PENDING
    }

    @Then("the 2nd standby player is stood down")
    @Pending
    public void thenThe2ndStandbyPlayerIsStoodDown() {
      // PENDING
    }


    Scenario: a confirmed player can subsequently decline
    Given a match is scheduled
    And notifications have been sent out to the proposed team members
    And a team member acknowledges their availability (PENDING)
    And they are assigned to the match (PENDING)
    When the player subsequently declines (PENDING)
    Then a notification goes out to the next appropriate player in the pool (NOT PERFORMED)
    And a decline acknowledgement notification goes to the player (NOT PERFORMED)
    @Given("a team member acknowledges their availability")
    @Pending
    public void givenATeamMemberAcknowledgesTheirAvailability() {
      // PENDING
    }

    @Given("they are assigned to the match")
    @Pending
    public void givenTheyAreAssignedToTheMatch() {
      // PENDING
    }

    @When("the player subsequently declines")
    @Pending
    public void whenThePlayerSubsequentlyDeclines() {
      // PENDING
    }


    Scenario: an unconfirmed standby who has been stood down can be repicked for standby
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    And the standby player accepts (PENDING)
    And the original player accepts (PENDING)
    And times elapses till the 3 days before the match (PENDING)
    And the original player declines (PENDING)
    And a standby notification goes out to the standby player (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("the standby player accepts")
    @Pending
    public void andTheStandbyPlayerAccepts() {
      // PENDING
    }

    @And("the original player accepts")
    @Pending
    public void andTheOriginalPlayerAccepts() {
      // PENDING
    }

    @And("times elapses till the 3 days before the match")
    @Pending
    public void andTimesElapsesTillThe3DaysBeforeTheMatch() {
      // PENDING
    }

    @And("the original player declines")
    @Pending
    public void andTheOriginalPlayerDeclines() {
      // PENDING
    }

    @And("a standby notification goes out to the standby player")
    @Pending
    public void andAStandbyNotificationGoesOutToTheStandbyPlayer() {
      // PENDING
    }


    Scenario: player who has declined standby cannot be selected to play
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    And the standby player declines (PENDING)
    When all picked players decline (PENDING)
    Then the declined standby is not selected (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("the standby player declines")
    @Pending
    public void andTheStandbyPlayerDeclines() {
      // PENDING
    }

    @When("all picked players decline")
    @Pending
    public void whenAllPickedPlayersDecline() {
      // PENDING
    }

    @Then("the declined standby is not selected")
    @Pending
    public void thenTheDeclinedStandbyIsNotSelected() {
      // PENDING
    }


    Scenario: 

    Scenario: 

    Scenario: a standby reminder is sent to players on standby notification who have failed to respond
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    And times elapses till the 3 days before the match (PENDING)
    And a standby reminder goes out to the next appropriate player in the pool (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("times elapses till the 3 days before the match")
    @Pending
    public void andTimesElapsesTillThe3DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby reminder goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyReminderGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }


    Scenario: the match is confirmed, confirmed standby players are stood down
    And it is 10 days before the match (PENDING)
    And all but two first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And standby notifications go out to the next appropriate players in the pool (PENDING)
    And all standby players accept (PENDING)
    When all outstanding selected player accept
    The the match is confirmed (PENDING)
    Then all confirmed standby players are stood down (PENDING)
    Then all confirmed standby players are notified that they are stood down (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but two first pick players responds")
    @Pending
    public void andAllButTwoFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("standby notifications go out to the next appropriate players in the pool")
    @Pending
    public void andStandbyNotificationsGoOutToTheNextAppropriatePlayersInThePool() {
      // PENDING
    }

    @And("all standby players accept")
    @Pending
    public void andAllStandbyPlayersAccept() {
      // PENDING
    }

    @When("all outstanding selected player accept\nThe the match is confirmed")
    @Pending
    public void whenAllOutstandingSelectedPlayerAcceptTheTheMatchIsConfirmed() {
      // PENDING
    }

    @Then("all confirmed standby players are stood down")
    @Pending
    public void thenAllConfirmedStandbyPlayersAreStoodDown() {
      // PENDING
    }

    @Then("all confirmed standby players are notified that they are stood down")
    @Pending
    public void thenAllConfirmedStandbyPlayersAreNotifiedThatTheyAreStoodDown() {
      // PENDING
    }


    Scenario: the match is confirmed, players notified for standby are stood down
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    And times elapses till the 4 days before the match (PENDING)
    And a standby notification goes out to the next appropriate player in the pool (PENDING)
    And times elapses till the 3 days before the match (PENDING)
    And a standby reminder goes out to the 2nd next appropriate player in the pool (PENDING)
    When all outstanding selected player accept
    The the match is confirmed (PENDING)
    Then all unconfirmed standby players are stood down (PENDING)
    Then all unconfirmed standby players are notified that they are stood down (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @And("times elapses till the 4 days before the match")
    @Pending
    public void andTimesElapsesTillThe4DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby notification goes out to the next appropriate player in the pool")
    @Pending
    public void andAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @And("times elapses till the 3 days before the match")
    @Pending
    public void andTimesElapsesTillThe3DaysBeforeTheMatch() {
      // PENDING
    }

    @And("a standby reminder goes out to the 2nd next appropriate player in the pool")
    @Pending
    public void andAStandbyReminderGoesOutToThe2ndNextAppropriatePlayerInThePool() {
      // PENDING
    }

    @When("all outstanding selected player accept\nThe the match is confirmed")
    @Pending
    public void whenAllOutstandingSelectedPlayerAcceptTheTheMatchIsConfirmed() {
      // PENDING
    }

    @Then("all unconfirmed standby players are stood down")
    @Pending
    public void thenAllUnconfirmedStandbyPlayersAreStoodDown() {
      // PENDING
    }

    @Then("all unconfirmed standby players are notified that they are stood down")
    @Pending
    public void thenAllUnconfirmedStandbyPlayersAreNotifiedThatTheyAreStoodDown() {
      // PENDING
    }


    Scenario: the match is not confirmed 3 days before the match, all players are notified of status
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    When times elapses till the 3 days before the match (PENDING)
    Then all notified players who have not declined are sent a detailed match status (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @When("times elapses till the 3 days before the match")
    @Pending
    public void whenTimesElapsesTillThe3DaysBeforeTheMatch() {
      // PENDING
    }

    @Then("all notified players who have not declined are sent a detailed match status")
    @Pending
    public void thenAllNotifiedPlayersWhoHaveNotDeclinedAreSentADetailedMatchStatus() {
      // PENDING
    }


    Scenario: the match date passes, all players are notified it has passed, and notes player status
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    When times elapses till after the match (PENDING)
    Then all notified players who have not declined are sent a detailed match status with completed status. (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @When("times elapses till after the match")
    @Pending
    public void whenTimesElapsesTillAfterTheMatch() {
      // PENDING
    }

    @Then("all notified players who have not declined are sent a detailed match status with completed status.")
    @Pending
    public void thenAllNotifiedPlayersWhoHaveNotDeclinedAreSentADetailedMatchStatusWithCompletedStatus() {
      // PENDING
    }


//    Scenario: the match date passes, selected players who accept are notified that the match has passed
//    And it is 10 days before the match (PENDING)
//    And all but one first pick players responds (PENDING)
//    When times elapses till after the match (PENDING)
//    And the outstanding player is notified that the match has passed (PENDING)

    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @When("times elapses till after the match")
    @Pending
    public void whenTimesElapsesTillAfterTheMatch() {
      // PENDING
    }

    @When("the outstanding player is notified that the match has passed")
    @Pending
    public void whenTheOutstandingPlayerIsNotifiedThatTheMatchHasPassed() {
      // PENDING
    }


    Scenario: the match date passes, selected players who accept are notified that the match has passed
    And it is 10 days before the match (PENDING)
    And all but one first pick players responds (PENDING)
    When times elapses till after the match (PENDING)
    And the standby player is notified that the match has passed (PENDING)
    @And("it is 10 days before the match")
    @Pending
    public void andItIs10DaysBeforeTheMatch() {
      // PENDING
    }

    @And("all but one first pick players responds")
    @Pending
    public void andAllButOneFirstPickPlayersResponds() {
      // PENDING
    }

    @When("times elapses till after the match")
    @Pending
    public void whenTimesElapsesTillAfterTheMatch() {
      // PENDING
    }

    @When("the standby player is notified that the match has passed")
    @Pending
    public void whenTheStandbyPlayerIsNotifiedThatTheMatchHasPassed() {
      // PENDING
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
