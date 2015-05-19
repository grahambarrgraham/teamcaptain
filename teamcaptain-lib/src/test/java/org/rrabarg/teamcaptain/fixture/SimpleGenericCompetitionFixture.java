package org.rrabarg.teamcaptain.fixture;

import static java.util.Arrays.asList;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.joe;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.peter;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.stacy;

import java.io.IOException;

import org.rrabarg.teamcaptain.NotificationStrategy;
import org.rrabarg.teamcaptain.SelectionStrategy;
import org.rrabarg.teamcaptain.demo.CompetitionBuilder;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerState;
import org.rrabarg.teamcaptain.strategy.BasicNotificationStrategy;
import org.rrabarg.teamcaptain.strategy.ContactPreference;
import org.rrabarg.teamcaptain.strategy.SimpleGenderedSelectionStrategy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
@Profile("test")
public class SimpleGenericCompetitionFixture extends BaseFixture {

    private final Player[] playerPool = new Player[] { joe, stacy, peter };
    private final SelectionStrategy testSelectionStrategy = new SimpleGenderedSelectionStrategy(1, 1);
    private final NotificationStrategy testNotificationStrategy = new BasicNotificationStrategy(7, 10, 4,
            ContactPreference.emailOnly());

    Player substitute = peter;
    Player noEligibleSub = stacy;
    Player hasEligibleSub = joe;
    Player selectedPlayerWhoHasDeclinePriorToMatchWindow = joe;

    @Override
    public Competition createCompetitionImpl() {
        return standardCompetition();
    }

    @Override
    protected void setupScenarioImpl() {
        selectedPlayers.addAll(asList(joe, stacy));
    }

    private Competition standardCompetition() {
        return new CompetitionBuilder()
                .withNotificationStrategy(testNotificationStrategy)
                .withPlayerPool(playerPool)
                .withSelectStrategy(testSelectionStrategy)
                .build();
    }

    public Player getASelectedPlayerWithAnEligibleSubstitute() {
        return hasEligibleSub;
    }

    public Player getASelectedPlayerWithNoEligibleSubstitute() {
        return noEligibleSub;
    }

    public Player getEligibleSubstituteFor(Player player) {
        if (hasEligibleSub.equals(player)) {
            return substitute;
        } else {
            return null;
        }
    }

    public Player aSelectedPlayerWithNoEligibleSubstitute() {
        return noEligibleSub;
    }

    public void aFirstPickPoolMemberHasDeclinedPriorToTheMatchWindow(Match match) throws IOException {
        selectedPlayersThatDeclined.add(selectedPlayerWhoHasDeclinePriorToMatchWindow);
        match.setPlayerState(selectedPlayerWhoHasDeclinePriorToMatchWindow, PlayerState.Declined);
        scheduleService.updateMatch(match);
    }

}
