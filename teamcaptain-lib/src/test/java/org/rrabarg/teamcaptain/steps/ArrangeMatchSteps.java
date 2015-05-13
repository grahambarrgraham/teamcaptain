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

    @When("a selected player accepts")
    public void whenASelectedPlayerAccepts() {
        whenATeamMemberAcknowledgesTheirAvailability();
    }

    @Given("a selected player accepts")
    public void givenASelectedPlayerAccepts() {
        whenATeamMemberAcknowledgesTheirAvailability();
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
        genericFixture.aFirstPickPlayerDeclines();
    }

    @Given("a player responds that they are not available")
    public void givenAPlayerRespondsThatTheyAreNotAvailable() {
        genericFixture.aFirstPickPlayerDeclines();
    }

    @When("a selected player declines")
    public void whenAPlayerDeclines() {
        whenAPlayerRespondsThatTheyAreNotAvailable();
    }

    @When("the original player declines")
    public void whenTheOriginalPlayerDeclines() {
        whenAPlayerRespondsThatTheyAreNotAvailable();
    }

    @Given("a selected player declines")
    public void giveAPlayerDeclines() {
        whenAPlayerRespondsThatTheyAreNotAvailable();
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

    @Given("a match is scheduled and is in the selection window")
    public void givenAMatchIsScheduledAndIsInTheSelectionWindow() throws IOException, ServiceException,
            InterruptedException {
        givenAMatchIsScheduled();
        givenItIs10DaysBeforeTheMatch();
    }

    @Given("all but one first pick players responds")
    public void givenAllButOneFirstPickPlayersResponds() throws IOException {
        genericFixture.allButOneFirstPickPlayersRespond(match);
    }

    @When("time elapses till the match")
    public void whenTimesElapsesTillTheMatch() {
        genericFixture.pumpWorkflowsTillXDaysBeforeMatch(0, match);
    }

    @Then("a daily reminder is sent to the non-responding player from 7 days before the match")
    public void thenADailyReminderIsSentToTheNonrespondingPlayerFrom7DaysBeforeTheMatch() {
        genericFixture.checkDailyReminderIsSentForDaysBeforeMatch(7, match);
    }

    @Given("time elapses till %number days before the match")
    public void givenTimesElapsesTillXDaysBeforeTheMatch(int number) {
        genericFixture.pumpWorkflowsTillXDaysBeforeMatch(number, match);
    }

    @When("time elapses till %number days before the match")
    public void whenTimesElapsesTillTheXDaysBeforeTheMatch(int number) {
        givenTimesElapsesTillXDaysBeforeTheMatch(number);
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

    @Then("they become eligible again and are returned to the pool as the highest ranked substitute")
    @Pending
    public void thenTheyBecomeEligibleAgainAndAreReturnedToThePoolAsTheHighestRankedSubstitute() {
        // nothing to assert here, need to assert that they can be picked
    }

    @Then("they are notified that they are eligible again")
    @Pending
    public void thenTheyAreNotifiedThatTheyAreEligibleAgain() {
        genericFixture.checkAcceptingPlayerWhoHasAlreadyDeclinedIsNotifiedOfTheirEligibility();
    }

    @When("the next appropriate player declines")
    @Pending
    public void whenTheNextAppropriatePlayerDeclines() {
        genericFixture.aSecondPickPlayerDeclines();
    }

    @When("the next appropriate player accepts")
    @Pending
    public void whenTheNextAppropriatePlayerAccepts() {
        genericFixture.aSecondPickPlayerAccepts();
    }

    @When("the outstanding player declines")
    @Pending
    public void whenTheOutstandingPlayerDeclines() {
        genericFixture.aFirstPickPlayerDeclines();
    }

    @Then("the standby player is selected")
    @Pending
    public void thenTheStandbyPlayerIsSelected() {
        thenAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool();
    }

    @Given("the next appropriate player accepts the standby request")
    @Pending
    public void givenTheNextAppropriatePlayerAcceptsTheStandbyRequest() {
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

    @Then("a standby notification goes out to the 2nd next appropriate player in the pool")
    @Pending
    public void thenAStandbyNotificationGoesOutToThe2ndNextAppropriatePlayerInThePool() {
        // PENDING
    }

    @Given("the 2nd standby player the standby request")
    @Pending
    public void givenThe2ndStandbyPlayerTheStandbyRequest() {
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

    @Given("the 1st standby player accepts")
    @Pending
    public void givenThe1stStandbyPlayerAccepts() {
        // PENDING
    }

    @Given("the 2nd standby player accepts")
    @Pending
    public void givenThe2ndStandbyPlayerAccepts() {
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

    @When("the player subsequently declines")
    @Pending
    public void whenThePlayerSubsequentlyDeclines() {
        // PENDING
    }

    @Given("the standby player accepts")
    @Pending
    public void givenTheStandbyPlayerAccepts() {
        // PENDING
    }

    @Given("the original player accepts")
    @Pending
    public void givenTheOriginalPlayerAccepts() {
        // PENDING
    }

    @Given("the original player declines")
    @Pending
    public void givenTheOriginalPlayerDeclines() {
        // PENDING
    }

    @Given("a standby notification goes out to the standby player")
    @Pending
    public void givenAStandbyNotificationGoesOutToTheStandbyPlayer() {
        // PENDING
    }

    @Given("the standby player declines")
    @Pending
    public void givenTheStandbyPlayerDeclines() {
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

    @Then("a standby reminder goes out to the next appropriate player in the pool")
    @Pending
    public void thenAStandbyReminderGoesOutToTheNextAppropriatePlayerInThePool() {
        // PENDING
    }

    @Given("all but 2 first pick players responds")
    @Pending
    public void givenAllButTwoFirstPickPlayersResponds() {
        // PENDING
    }

    @Given("all standby players accept")
    @Pending
    public void givenAllStandbyPlayersAccept() {
        // PENDING
    }

    @Then("the match is confirmed")
    @Pending
    public void thenTheMatchIsConfirmed() {
        // PENDING
    }

    @Then("all confirmed standby players are stood down and notified")
    @Pending
    public void thenAllConfirmedStandbyPlayersAreStoodDownAndNotified() {
        // PENDING
    }

    @Then("all unconfirmed standby players are stood down and notified")
    @Pending
    public void thenAllUnconfirmedStandbyPlayersAreStoodDownAndNotified() {
        // PENDING
    }

    @Then("all notified players who have not declined are sent a detailed match status")
    @Pending
    public void thenAllNotifiedPlayersWhoHaveNotDeclinedAreSentADetailedMatchStatus() {
        // PENDING
    }

    @Then("all notified players who have not declined are sent a detailed match status with completed status.")
    @Pending
    public void thenAllNotifiedPlayersWhoHaveNotDeclinedAreSentADetailedMatchStatusWithCompletedStatus() {
        // PENDING
    }

    @Then("the outstanding player is notified that the match has passed")
    @Pending
    public void thenTheOutstandingPlayerIsNotifiedThatTheMatchHasPassed() {
        // PENDING
    }

    @When("time elapses till after the match")
    @Pending
    public void whenTimeElapsesTillAfterTheMatch() {
        // PENDING
    }

    @Then("the standby player is notified that the match has passed")
    @Pending
    public void thenTheStandbyPlayerIsNotifiedThatTheMatchHasPassed() {
        // PENDING
    }

    @Given("all but 2 equivalent first pick players responds")
    @Pending
    public void givenAllBut2EquivalentFirstPickPlayersResponds() {
        // PENDING
    }

    @When("all outstanding selected players accept")
    @Pending
    public void whenAllOutstandingSelectedPlayersAccept() {
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
