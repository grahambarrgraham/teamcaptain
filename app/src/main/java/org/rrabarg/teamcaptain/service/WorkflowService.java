package org.rrabarg.teamcaptain.service;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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

    private static final int NUMBER_OF_DAYS_TILL_MATCH = 10;

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CompetitionService competitionService;

    @Autowired
    Provider<MatchWorkflow> provider;

    @Autowired
    Provider<Clock> clock;

    Map<Match, MatchWorkflow> workflowMap = new HashMap<>();

    public void refresh(Competition competition) throws IOException {

        competition
                .getSchedule()
                .getMatches()
                .stream()
                .filter(a -> isUpcoming(a))
                .parallel()
                .peek(match -> log.info("Loaded \"" + match + "\""))
                .map(match ->
                        createOrUpdateWorkflow(competition, match))
                .peek(workflow -> workflowMap.put(workflow.getMatch(), workflow))
                .forEach(workflow -> workflow.pump());
    }

    private MatchWorkflow createOrUpdateWorkflow(Competition competition, Match match) {

        MatchWorkflow flow = workflowMap.get(match);

        if (flow == null) {
            flow = provider.get();
        }

        flow.setup(competition, match);
        return flow;
    }

    public MatchWorkflow getWorkflow(Match match) {
        return workflowMap.get(match);
    }

    public void recordWorkflow(MatchWorkflow matchWorkflow) throws IOException {
        competitionService.updateMatch(matchWorkflow.getMatch());
    }

    public void pump() {
        workflowMap.values().forEach(a -> a.pump());
    }

    private boolean isUpcoming(Match match) {
        final Instant startOfWindow = match.getStartDateTime().minusDays(NUMBER_OF_DAYS_TILL_MATCH).toInstant();
        final Instant now = clock.get().instant();

        return (now.equals(startOfWindow) || (now.isAfter(startOfWindow) && now.isBefore(match.getStartDateTime()
                .toInstant())));

    }
}
