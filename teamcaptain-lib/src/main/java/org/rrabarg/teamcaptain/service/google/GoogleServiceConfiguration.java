package org.rrabarg.teamcaptain.service.google;

import org.rrabarg.teamcaptain.service.CompetitionService;
import org.rrabarg.teamcaptain.service.ScheduleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("google")
public class GoogleServiceConfiguration {
	
	@Bean
	CompetitionService competitionService() {
		return new CompetitionGoogleService();
	}

	@Bean
	ScheduleService scheduleService() {
		return new ScheduleGoogleService();
	}
}
