package org.rrabarg.teamcaptain;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.rrabarg.teamcaptain.domain.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleService {

    private static final int NUMBER_OF_DAYS_TILL_UPCOMING_MATCH = 10;

    @Autowired
    ScheduleRepository repository;

    @Autowired
    WorkflowService workflowService;

    public String createMatch(String scheduleId, Match match) {
        try {
            return repository.scheduleMatch(scheduleId, match);
        } catch (final IOException ioe) {
            throw new ScheduleException("Failed to create match " + match
                    + " on schedule " + scheduleId, ioe);
        }
    }

    public void checkScheduleForUpcomingMatches(String scheduleId) throws IOException {
        workflowService.getWorkflows(getUpcomingMatches(scheduleId)).stream()
                .parallel().forEach(workflow -> workflow.matchUpcoming());
    }

    private Collection<Match> getUpcomingMatches(String scheduleId) throws IOException {
        return repository.getUpcomingMatches(scheduleId,
                NUMBER_OF_DAYS_TILL_UPCOMING_MATCH, ChronoUnit.DAYS);
    }
}
