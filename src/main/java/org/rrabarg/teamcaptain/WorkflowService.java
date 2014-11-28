package org.rrabarg.teamcaptain;

import java.io.IOException;

import org.rrabarg.teamcaptain.domain.Competition;
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

    public void checkForUpcomingMatches(String competitionName) throws IOException {
        final Competition competition = competitionService.findCompetitionByName(competitionName);

        if (competition != null) {
            competition.getSchedule().getUpcomingMatches().stream()
                    .parallel()
                    .peek(match -> log.info("Notify workflow for match \"" + match + "\""))
                    .map(match -> new MatchWorkflow(competition, match))
                    .forEach(workflow -> workflow.event());
        } else {
            log.info("No upcoming matches for competition " + competitionName);
        }
    }
}
