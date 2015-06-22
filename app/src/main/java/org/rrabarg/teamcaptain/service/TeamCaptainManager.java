package org.rrabarg.teamcaptain.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.rrabarg.teamcaptain.domain.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TeamCaptainManager {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    CompetitionService competitionService;

    @Inject
    WorkflowService workflowService;

    Collection<Competition> competitions = new ArrayList<>();

    @PostConstruct
    public void refreshCompetitions() throws IOException {

        log.info("Refreshing all competitions");

        competitions = competitionService.getCompetitionIds().stream()
                .map(a -> competitionService.findCompetitionById(a))
                .filter(a -> a != null)
                .peek(e -> log.debug("Refreshing competition {}", e.getName()))
                .collect(Collectors.toList());
    }

    public synchronized void applyWorkflowForAllCompetitions() {
        log.info("Applying workflow for all competitions");
        competitions.stream().forEach(competition -> applyWorkflow(competition));
    }

    private void applyWorkflow(Competition competition) {

        log.info("Applying workflow for {}", competition.getName());

        try {
            workflowService.refresh(competition);
        } catch (final IOException e) {
            log.error("Failed to refresh workflow for competition {}", competition, e);
        }
    }

}
