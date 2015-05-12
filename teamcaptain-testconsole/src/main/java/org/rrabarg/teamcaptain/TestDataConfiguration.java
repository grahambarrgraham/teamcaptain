package org.rrabarg.teamcaptain;

import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.jed;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.jimmy;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.joe;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.peter;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.safron;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.sally;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.sharon;
import static org.rrabarg.teamcaptain.demo.CompetitionBuilder.stacy;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.rrabarg.teamcaptain.demo.CompetitionBuilder;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.service.CompetitionService;
import org.rrabarg.teamcaptain.service.TeamCaptainManager;
import org.rrabarg.teamcaptain.strategy.ContactPreference;
import org.springframework.stereotype.Component;

@Component
public class TestDataConfiguration {

    @Inject
    CompetitionService competitionService;

    @Inject
    TeamCaptainManager teamCaptainManager;

    private Competition competition;

    @PostConstruct
    void setTestData() throws IOException {
        competition = new CompetitionBuilder()
                .withPlayerPool(stacy, sharon, safron, sally, joe, jimmy, peter, jed)
                .withContactPreference(ContactPreference.smsQuestionsWithEmailBroadcast())
                .build();
        competitionService.saveCompetition(competition);
        teamCaptainManager.refreshCompetitions();
    }

    public void reset() throws IOException {
        competitionService.clearCompetition(competition);
        setTestData();
    }

}
