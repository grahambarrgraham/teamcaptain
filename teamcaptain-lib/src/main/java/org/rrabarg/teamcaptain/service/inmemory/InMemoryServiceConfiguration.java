package org.rrabarg.teamcaptain.service.inmemory;

import org.rrabarg.teamcaptain.service.CompetitionService;
import org.rrabarg.teamcaptain.service.ScheduleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("inmemory")
public class InMemoryServiceConfiguration {
	
	@Bean
	CompetitionService competitionService() {
		return new CompetitionInMemoryService();
	}

	@Bean
	ScheduleService scheduleService() {
		return new ScheduleInMemoryService();
	}
}
