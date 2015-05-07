package org.rrabarg.teamcaptain.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.rrabarg.teamcaptain.config.JavaUtilLoggingBridgeConfiguration;
import org.rrabarg.teamcaptain.domain.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TeamCaptainManager {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    JavaUtilLoggingBridgeConfiguration julBridge; // ensure configured
    
    @Inject
    CompetitionService competitionService;

    @Inject
    WorkflowService workflowService;

    Collection<Competition> competitions = new ArrayList<>();

    @PostConstruct
    public void refreshCompetitions() throws IOException {
        competitions = competitionService.getCompetitionIds().stream()
                .map(a -> competitionService.findCompetitionByName(a))
                .filter(a -> a != null)
                .collect(Collectors.toList());
    }

	public synchronized void refreshWorkflows() {
		competitions.stream().forEach(competition -> refreshWorkflow(competition));		
	}
    
    private void refreshWorkflow(Competition competition) {
        try {
            workflowService.refresh(competition);
        } catch (final IOException e) {
            log.error("Failed to refresh workflow for competitition %s", competition, e);
        }
    }

    final String s = "Test competition-127.0.1.1";

}
