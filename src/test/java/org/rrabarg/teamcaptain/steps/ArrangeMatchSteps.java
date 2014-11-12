package org.rrabarg.teamcaptain.steps;

import java.io.IOException;

import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Pending;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;
import org.rrabarg.teamcaptain.fixture.ScheduleFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArrangeMatchSteps extends Steps {

    @Autowired
    ScheduleFixture scheduleFixture;

    @BeforeScenario
    public void reset() throws IOException {
        scheduleFixture.reset();
    }

    @Given("a match is scheduled")
    public void givenAMatchIsScheduled() {
        scheduleFixture.scheduleMatch();
    }

    @When("it is 10 days before the match")
    @Pending
    public void whenItIs10DaysBeforeTheMatch() {
        // PENDING
    }

    @Then("an availability notification is sent to the first pick members")
    @Pending
    public void thenAnAvailabilityNotificationIsSentToTheFirstPickMembers() {
        // PENDING
    }

    @Given("notifications have been sent out to the proposed team members")
    @Pending
    public void givenNotificationsHaveBeenSentOutToTheProposedTeamMembers() {
        // PENDING
    }

    @When("a team member acknowledges their availability")
    @Pending
    public void whenATeamMemberAcknowledgesTheirAvailability() {
        // PENDING
    }

    @Then("they are assigned to the match")
    @Pending
    public void thenTheyAreAssignedToTheMatch() {
        // PENDING
    }

    @Then("an acknowledgement notification goes to the player")
    @Pending
    public void thenAnAcknowledgementNotificationGoesToThePlayer() {
        // PENDING
    }

    @Given("notifications have been sent out to the first pick players")
    @Pending
    public void givenNotificationsHaveBeenSentOutToTheFirstPickPlayers() {
        // PENDING
    }

    @When("a player responds that they are not available")
    @Pending
    public void whenAPlayerRespondsThatTheyAreNotAvailable() {
        // PENDING
    }

    @Then("a notification goes out to the next appropriate player in the pool")
    @Pending
    public void thenANotificationGoesOutToTheNextAppropriatePlayerInThePool() {
        // PENDING
    }

    @Given("it is at least 4 days before the match")
    @Pending
    public void givenItIsAtLeast4DaysBeforeTheMatch() {
        // PENDING
    }

    @Given("more than 1 day has elapsed since a notification was sent to the player")
    @Pending
    public void givenMoreThan1DayHasElapsedSinceANotificationWasSentToThePlayer() {
        // PENDING
    }

    @When("a team member fails to acknowledges their availability")
    @Pending
    public void whenATeamMemberFailsToAcknowledgesTheirAvailability() {
        // PENDING
    }

    @Then("a reminder notification is sent to the player")
    @Pending
    public void thenAReminderNotificationIsSentToThePlayer() {
        // PENDING
    }

    @Given("it is less than 4 days before the match")
    @Pending
    public void givenItIsLessThan4DaysBeforeTheMatch() {
        // PENDING
    }

    @Then("a reserve notification goes out to the next appropriate player in the pool")
    @Pending
    public void thenAReserveNotificationGoesOutToTheNextAppropriatePlayerInThePool() {
        // PENDING
    }

    @Then("an administrator alert is raised")
    @Pending
    public void thenAnAdministratorAlertIsRaised() {
        // PENDING
    }

    @When("sufficient players are assigned to the match")
    @Pending
    public void whenSufficientPlayersAreAssignedToTheMatch() {
        // PENDING
    }

    @Then("a match confirmation notification is sent out to all notified players")
    @Pending
    public void thenAMatchConfirmationNotificationIsSentOutToAllNotifiedPlayers() {
        // PENDING
    }

    @Then("the confirmation contains the list of players assigned to the match")
    @Pending
    public void thenTheConfirmationContainsTheListOfPlayersAssignedToTheMatch() {
        // PENDING
    }

    @Then("the confirmation contains the match details")
    @Pending
    public void thenTheConfirmationContainsTheMatchDetails() {
        // PENDING
    }

    @Then("an administration notification is raised")
    @Pending
    public void thenAnAdministrationNotificationIsRaised() {
        // PENDING
    }

    @Given("a member of the pool is on holiday on the date of the match")
    @Pending
    public void givenAMemberOfThePoolIsOnHolidayOnTheDateOfTheMatch() {
        // PENDING
    }

    @When("players are notified")
    @Pending
    public void whenPlayersAreNotified() {
        // PENDING
    }

    @Then("the player on holiday is not notified")
    @Pending
    public void thenThePlayerOnHolidayIsNotNotified() {
        // PENDING
    }

    @Given("a member of the pool is injured")
    @Pending
    public void givenAMemberOfThePoolIsInjured() {
        // PENDING
    }

    @Then("the injured player is not notified")
    @Pending
    public void thenTheInjuredPlayerIsNotNotified() {
        // PENDING
    }

    @Given("an there are insufficient eligible players to fulfill the match")
    @Pending
    public void givenAnThereAreInsufficientEligiblePlayersToFulfillTheMatch() {
        // PENDING
    }

    @Given("a mixed doubles match is scheduled")
    @Pending
    public void givenAMixedDoublesMatchIsScheduled() {
        // PENDING
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
