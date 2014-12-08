package org.rrabarg.teamcaptain.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.SelectionStrategy;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.MatchWorkflow;
import org.rrabarg.teamcaptain.domain.PoolOfPlayers;
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

    @Autowired
    ScheduleService scheduleService;

    Map<Match, MatchWorkflow> workflowMap = new HashMap<>();

    public void refresh(String competitionName) throws IOException {
        final Competition competition = competitionService.findCompetitionByName(competitionName);

        if (competition != null) {
            competition
                    .getSchedule()
                    .getUpcomingMatches()
                    .stream()
                    .parallel()
                    .peek(match -> log.info("Loaded \"" + match + "\""))
                    .map(match ->
                            createOrUpdateWorkflow(competition.getPlayerPool(), match,
                                    competition.getSelectionStrategy()))
                    .peek(workflow -> workflowMap.put(workflow.getMatch(), workflow))
                    .forEach(workflow -> workflow.pump());
        } else {
            log.info("No upcoming matches for competition " + competitionName);
        }
    }

    private MatchWorkflow createOrUpdateWorkflow(PoolOfPlayers pool, Match match, SelectionStrategy strategy) {

        MatchWorkflow flow = workflowMap.get(match);

        if (flow == null) {
            flow = provider.get();
        }

        flow.setup(pool, match, strategy);
        return flow;
    }

    public MatchWorkflow getWorkflow(Match match) {
        return workflowMap.get(match);
    }

    public void recordWorkflow(MatchWorkflow matchWorkflow) throws IOException {
        scheduleService.updateMatch(matchWorkflow.getMatch());
    }
}
