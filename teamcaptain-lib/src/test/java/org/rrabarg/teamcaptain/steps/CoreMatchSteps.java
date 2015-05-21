package org.rrabarg.teamcaptain.steps;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.Alias;
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
import org.rrabarg.teamcaptain.fixture.SimpleGenericCompetitionFixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gdata.util.ServiceException;

@Component
public class CoreMatchSteps extends Steps {

    @Autowired
    SimpleGenericCompetitionFixture genericFixture;

    @Autowired
    CambridgeLeagueCompetitionFixture cambsLeagueFixture;

    Logger log = LoggerFactory.getLogger(getClass().getName());

    Match match;

    private Player theLastPlayerWhoReplied;
    private Player theFirstPickPlayerWhoReplied;
    private Player thePlayerStillToRespond;

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

    @When("a selected player with an eligible substitute %response")
    @Alias("a selected player %response the match")
    public void whenAPlayerWithAnEligibleSubResponds(String response) {
        theLastPlayerWhoReplied = genericFixture.getASelectedPlayerWithAnEligibleSubstitute();
        theFirstPickPlayerWhoReplied = theLastPlayerWhoReplied;
        genericFixture.aSelectedPlayerResponds(theLastPlayerWhoReplied, getResponse(response));
    }

    @When("a selected player with no eligible substitute %response")
    public void whenAPlayerWithNoEligibleSubResponds(String response) {
        theLastPlayerWhoReplied = genericFixture.getASelectedPlayerWithNoEligibleSubstitute();
        theFirstPickPlayerWhoReplied = theLastPlayerWhoReplied;
        genericFixture.aSelectedPlayerResponds(theLastPlayerWhoReplied, getResponse(response));
    }

    @When("that selected player then %response")
    public void whenThatSelectedPlayerResponds(String response) {
        genericFixture.aSelectedPlayerResponds(theLastPlayerWhoReplied, getResponse(response));
    }

    @When("the first picked selected player then %response")
    public void whenTheFirstPickedSelectedPlayerResponds(String response) {
        genericFixture.aSelectedPlayerResponds(theFirstPickPlayerWhoReplied, getResponse(response));
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

    @Given("a selected player with an eligible substitute %response")
    @Alias("a selected player %response the match")
    public void givenAPlayerWithAnEligibleSubResponds(String response) {
        whenAPlayerWithAnEligibleSubResponds(response);
    }

    @Given("a selected player with no eligible substitute %response")
    public void giveAPlayerWithNoEligibleSubstituteDeclines(String response) {
        whenAPlayerWithNoEligibleSubResponds(response);
    }

    @When("the original player %response")
    public void whenTheOriginalPlayerResponds(String response) {
        genericFixture.aSelectedPlayerResponds(theFirstPickPlayerWhoReplied, getResponse(response));
    }

    @Then("a notification goes out to the next appropriate player in the pool")
    public void thenANotificationGoesOutToTheNextAppropriatePlayerInThePool() {
        genericFixture.checkOutboundMessageIsCorrect(genericFixture.getEligibleSubstituteFor(theLastPlayerWhoReplied),
                NotificationKind.CanYouPlay, match);
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

    @Given("all selected players %response except one, and that player has %hasSub eligible substitute in the pool")
    public void givenAllButOneFirstPickPlayersResponds(String response, String hasSub) throws IOException {

        if ("no".equals(hasSub)) {
            thePlayerStillToRespond = genericFixture.getASelectedPlayerWithNoEligibleSubstitute();
        } else {
            thePlayerStillToRespond = genericFixture.getASelectedPlayerWithAnEligibleSubstitute();
        }

        genericFixture.allSelectedPlayersRespondExcept(match, getResponse(response),
                thePlayerStillToRespond);
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
        genericFixture.checkOutboundMessageIsCorrect(genericFixture.getEligibleSubstituteFor(theLastPlayerWhoReplied),
                NotificationKind.StandBy, match);
    }

    @Then("the team captain is notified of the %type message from %player")
    public void thenTheTeamCaptainIsNotified(String type, String player) {
        genericFixture.checkOutboundTeamCaptainMessageIsCorrect(getTeamCaptainNotificationKind(type), match,
                theLastPlayerWhoReplied);
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

    @Given("a member of the pool is on holiday on the date of the match")
    public void givenAMemberOfThePoolIsOnHolidayOnTheDateOfTheMatch() throws IOException {
        genericFixture.aFirstPickPoolMemberHasDeclinedPriorToTheMatchWindow(match);
    }

    @Then("the player on holiday is not notified")
    public void thenThePlayerOnHolidayIsNotNotified() {
        genericFixture.checkNotificationDoesNotGoToPlayerWhoDeclined();
        genericFixture.checkNotificationGoesToEligibleFirstPickPlayers(match);
    }

    @When("there are insufficient eligible players to fulfill the match")
    public void whenThereAreInsufficientEligiblePlayersToFulfillTheMatch() throws IOException {
        whenAPlayerWithNoEligibleSubResponds("decline");
    }

    @Then("they become eligible again and are returned to the pool as the highest ranked substitute")
    @Pending
    public void thenTheyBecomeEligibleAgainAndAreReturnedToThePoolAsTheHighestRankedSubstitute() {
        // nothing to assert here, need to assert that they can be picked
    }

    @When("the selected substitute player %response")
    public void whenTheSelectedSubstituteResponds(String response) {
        theLastPlayerWhoReplied = genericFixture.getEligibleSubstituteFor(theLastPlayerWhoReplied);
        genericFixture.aStandbyPlayerResponds(theLastPlayerWhoReplied, getResponse(response));
    }

    @When("an unselected substitute player %response")
    public void whenAnUnselectedSelectedSubstituteResponds(String response) {
        theLastPlayerWhoReplied = getViableSubstitute();
        genericFixture.aStandbyPlayerResponds(theLastPlayerWhoReplied, getResponse(response));
    }

    @Given("the selected substitute player %response")
    public void givenTheSelectedSubstituteResponds(String response) {
        whenTheSelectedSubstituteResponds(response);
    }

    @When("the outstanding player %response the match")
    public void whenTheOutstandingPlayerResponds(String response) {
        theLastPlayerWhoReplied = genericFixture
                .aSelectedPlayerResponds(thePlayerStillToRespond, getResponse(response));
    }

    @Then("the standby player is selected")
    public void thenTheStandbyPlayerIsSelected() {
        genericFixture.checkPlayerIsAssignedToTheMatch(getViableSubstitute());
    }

    @Given("the next appropriate player %response the standby request")
    public void givenTheNextAppropriatePlayerAcceptsTheStandbyRequest(String response) {
        theLastPlayerWhoReplied = genericFixture.aStandbyPlayerResponds(getViableSubstitute(), getResponse(response));
    }

    @Then("the next appropriate player is selected")
    public void thenTheNextAppropriatePlayerIsSelected() {
        thenAStandbyNotificationGoesOutToTheNextAppropriatePlayerInThePool();
    }

    @Then("they are notified that they are now a confirmed standby player")
    public void thenTheyAreNotifiedThatTheyAreNowAConfirmedSubstitute() {
        genericFixture.checkOutboundMessageIsCorrect(theLastPlayerWhoReplied, NotificationKind.ConfirmationOfStandby,
                match);
    }

    @Then("all notified players who have not declined are sent a detailed match status with completed status.")
    public void thenAllNotifiedPlayersWhoHaveNotDeclinedAreSentADetailedMatchStatusWithCompletedStatus() {
        genericFixture.getAllNotifiedPlayers().forEach(
                player -> genericFixture.checkHasReceivedDetailedMatchStatus(player, match));
    }

    @Then("the outstanding player is automatically declined")
    @Pending
    public void thenTheOutstandingPlayerIsAutomaticallyDeclined() {
        // genericFixture.checkThatOutStandingPlayerIsAutomaticallyDeclined();
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

    @Then("the outstanding player is notified that the match has passed")
    @Pending
    public void thenTheOutstandingPlayerIsNotifiedThatTheMatchHasPassed() {
        // PENDING
    }

    @When("time elapses till after the match")
    public void whenTimeElapsesTillAfterTheMatch() {
        genericFixture.pumpWorkflowsTillXDaysBeforeMatch(-1, match);
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

    private Player getViableSubstitute() {
        return genericFixture.getEligibleSubstituteFor(genericFixture.getASelectedPlayerWithAnEligibleSubstitute());
    }

}
