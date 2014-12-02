package org.rrabarg.teamcaptain.service;

import java.io.IOException;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.MatchWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkflowService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CompetitionService competitionService;

    @Autowired
    Provider<MatchWorkflow> provider;

    public void checkForUpcomingMatches(String competitionName) throws IOException {
        final Competition competition = competitionService.findCompetitionByName(competitionName);

        if (competition != null) {
            competition.getSchedule().getUpcomingMatches().stream()
                    .parallel()
                    .peek(match -> log.info("Notify workflow for match \"" + match + "\""))
                    .map(match -> getWorkflow(competition, match))
                    .forEach(workflow -> workflow.canYouPlay());
        } else {
            log.info("No upcoming matches for competition " + competitionName);
        }
    }

    private MatchWorkflow getWorkflow(Competition competition, Match match) {
        final MatchWorkflow matchWorkflow = provider.get();
        matchWorkflow.setup(competition, match);
        return matchWorkflow;
    }
}
