package org.rrabarg.teamcaptain.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.rrabarg.teamcaptain.domain.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

@Singleton
public class TeamCaptainManager {

    private static final int WORKFLOW_REFRESH_DELAY = 1000 * 60;

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    CompetitionService competitionService;

    @Inject
    WorkflowService workflowService;

    Collection<Competition> competitions = new ArrayList<>();

    @PostConstruct
    public void loadCompetitions() throws IOException {
        competitions = competitionService.getCompetitionIds().stream()
                .map(a -> competitionService.findCompetitionByName(a))
                .filter(a -> a != null)
                .collect(Collectors.toList());
    }

    @Scheduled(initialDelay = 0, fixedDelay = WORKFLOW_REFRESH_DELAY)
    public synchronized void applyWorkflows() throws IOException {
        competitions.stream().forEach(competition -> refreshWorkflow(competition));
    }

    private void refreshWorkflow(Competition competition) {
        try {
            workflowService.refresh(competition);
        } catch (final IOException e) {
            log.error("Failed to refresh workflow for competitition %s", competition, e);
        }
    }

    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 60)
    public synchronized void refreshCompetitions() throws IOException {
        loadCompetitions();
    }

    final String s = "Test competition-127.0.1.1";
}
