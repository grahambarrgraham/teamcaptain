package org.rrabarg.teamcaptain.fixture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.jimmy;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.joe;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.peter;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.safron;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.sharon;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.stacy;

import java.io.IOException;
import java.util.Arrays;

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
public class CambridgeLeagueCompetitionFixture extends BaseFixture {

    private static Logger log = LoggerFactory.getLogger(CambridgeLeagueCompetitionFixture.class);

    private final Player[] firstPick = new Player[] { stacy, sharon, safron, joe, jimmy, peter };
    private final SelectionStrategy testSelectionStrategy = new SimpleGenderedSelectionStrategy(3, 3);
    private final NotificationStrategy testNotificationStrategy = new BasicNotificationStrategy(7, 15, 4,
            ContactPreference.emailOnly());

    @Override
    public Competition createCompetitionImpl() {
        return new CompetitionBuilder()
                .withSelectStrategy(testSelectionStrategy)
                .withNotificationStrategy(testNotificationStrategy)
                .withPlayerPool(firstPick)
                .build();
    }

    @Override
    protected void setupScenarioImpl() {
        firstPickPlayers.addAll(Arrays.asList(firstPick));
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
