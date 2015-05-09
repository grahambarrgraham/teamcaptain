package org.rrabarg.teamcaptain;

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
