package org.rrabarg.teamcaptain.config;

import java.io.IOException;

import javax.inject.Inject;

import org.rrabarg.teamcaptain.service.TeamCaptainManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    private static final int WORKFLOW_REFRESH_DELAY = 1000 * 60;
	
	@Inject
	TeamCaptainManager teamCaptainManager;
	
    @Scheduled(initialDelay = 0, fixedDelay = WORKFLOW_REFRESH_DELAY)
    public synchronized void applyWorkflows() throws IOException {
        teamCaptainManager.refreshWorkflows();
    }
    
    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 60)
    public synchronized void refreshCompetitions() throws IOException {
    	teamCaptainManager.refreshCompetitions();
    }
	
}
