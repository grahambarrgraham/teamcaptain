package org.rrabarg.teamcaptain.fixture;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.joe;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.peter;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.stacy;

import java.io.IOException;

import org.rrabarg.teamcaptain.NotificationStrategy;
import org.rrabarg.teamcaptain.SelectionStrategy;
import org.rrabarg.teamcaptain.demo.CompetitionBuilder;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.NotificationKind;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerState;
import org.rrabarg.teamcaptain.strategy.BasicNotificationStrategy;
import org.rrabarg.teamcaptain.strategy.ContactPreference;
import org.rrabarg.teamcaptain.strategy.SimpleGenderedSelectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
@Profile("test")
public class SimpleGenericCompetitionFixture extends BaseFixture {

    private static Logger log = LoggerFactory.getLogger(SimpleGenericCompetitionFixture.class);

    private final Player[] playerPool = new Player[] { joe, stacy, peter };
    private final SelectionStrategy testSelectionStrategy = new SimpleGenderedSelectionStrategy(1, 1);
    private final NotificationStrategy testNotificationStrategy = new BasicNotificationStrategy(7, 10, 4,
            ContactPreference.emailOnly());

    Player substitute = peter;
    Player aFirstPickPlayerWithNoEligibleSubstitute = stacy;
    Player aFirstPickPlayerWithAnEligibleSubstitute = joe;
    Player selectedPlayerWhoHasDeclinePriorToMatchWindow = joe;

    @Override
    public Competition createCompetitionImpl() {
        return standardCompetition();
    }

    @Override
    protected void setupScenarioImpl() {
        selectedPlayers.addAll(asList(joe, stacy));
    }

    enum Action {
        Accepts, Declines
    }

    private Competition standardCompetition() {
        return new CompetitionBuilder()
                .withNotificationStrategy(testNotificationStrategy)
                .withPlayerPool(playerPool)
                .withSelectStrategy(testSelectionStrategy)
                .build();
    }

    public void allSelectedPlayersAcceptExceptOneWhoHasEligibleSubstitute(Match match) {
        log.debug("All players accept except " + aFirstPickPlayerWithAnEligibleSubstitute);
        aPlayerDoesNotRespond(aFirstPickPlayerWithAnEligibleSubstitute, match);
        aSelectedPlayerDeclines(aFirstPickPlayerWithNoEligibleSubstitute);
    }

    public void allSelectedPlayersAcceptExceptOneWhoHasNoEligibleSubstitute(Match match) {
        log.debug("All players accept except " + aFirstPickPlayerWithAnEligibleSubstitute);
        aPlayerDoesNotRespond(aFirstPickPlayerWithAnEligibleSubstitute, match);
        aSelectedPlayerDeclines(aFirstPickPlayerWithNoEligibleSubstitute);
    }

    public Player aSelectedPlayerWithAnEligibleSubstituteDeclines() {
        aSelectedPlayerDeclines(aFirstPickPlayerWithAnEligibleSubstitute);
        return aFirstPickPlayerWithAnEligibleSubstitute;
    }

    public Player aSelectedPlayerWithNoEligibleSubstituteDeclines() {
        aSelectedPlayerDeclines(aFirstPickPlayerWithNoEligibleSubstitute);
        return aFirstPickPlayerWithNoEligibleSubstitute;
    }

    public Player aSelectedSubstituteDeclines() {
        aSelectedPlayerDeclines(substitute);
        return substitute;
    }

    public Player aSelectedSubstituteAccepts() {
        aSelectedPlayerAccepts(substitute);
        return substitute;
    }

    public Player aPlayerWhoDoesntHaveAnEligibleSubstituteDeclines() {
        aSelectedPlayerDeclines(aFirstPickPlayerWithNoEligibleSubstitute);
        return aFirstPickPlayerWithNoEligibleSubstitute;
    }

    public Player aSelectedPlayerWithAnEligibleSubstituteAccepts() {
        aSelectedPlayerAccepts(aFirstPickPlayerWithAnEligibleSubstitute);
        return aFirstPickPlayerWithAnEligibleSubstitute;
    }

    public void checkNotificationGoesToNextAppropriatePlayerInThePool(Match match) {
        checkOutboundEmailIsCorrect(substitute, NotificationKind.CanYouPlay, match);
    }

    public void checkNextAppropriatePlayerInThePoolIsNotifiedOfStandby(Match match) {
        checkOutboundEmailIsCorrect(substitute, NotificationKind.StandBy, match);
    }

    public void aFirstPickPoolMemberHasDeclinedPriorToTheMatchWindow(Match match) throws IOException {
        selectedPlayersThatDeclined.add(selectedPlayerWhoHasDeclinePriorToMatchWindow);
        match.setPlayerState(selectedPlayerWhoHasDeclinePriorToMatchWindow, PlayerState.Declined);
        scheduleService.updateMatch(match);
    }

    public void checkNotificationDoesNotGoToPlayerWhoDeclined() {
        for (final Player player : selectedPlayersThatDeclined) {
            assertThat("Mailbox of player who decline prior to match is empty",
                    mailbox.peek(player.getEmailAddress()), nullValue());
        }
    }

    public void checkThatThoseWhoSaidTheyCouldPlayAreAssignedToTheMatch() {
        for (final Player player : selectedPlayersThatAccepted) {
            checkThatThoseWhoSaidTheyCouldPlayAreAssignedToTheMatch(player);
        }
    }

    public void checkThatOutStandingPlayersAreAutomaticallyDeclined() {
        throw new UnsupportedOperationException("not implemented");
    }

    public void theSelectedPlayerAccepts() {
        throw new UnsupportedOperationException("not implemented");
    }

    private void aPlayerDoesNotRespond(Player player, Match match) {
        checkOutboundEmailIsCorrect(player, NotificationKind.CanYouPlay, match);
        playersThatDidntRespond.add(player);
    }

    public void aSelectedPlayerDeclines(Player player) {
        selectedPlayersThatDeclined.add(player);
        aPlayerRespondsWith(player, "No");
    }

    public void aSelectedPlayerAccepts(Player player) {
        selectedPlayersThatAccepted.add(player);
        aPlayerRespondsWith(player, "Yes");
    }

    public void aSelectedPlayerAcceptsStandby(Player player) {
        standbyPlayersThatAccepted.add(player);
        aPlayerRespondsWith(player, "Yes");
    }

}
