package org.rrabarg.teamcaptain.service.google;

import java.io.IOException;

import org.rrabarg.teamcaptain.domain.CompetitionState;
import org.rrabarg.teamcaptain.service.CompetitionStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("google")
public class CompetitionStateGoogleService implements CompetitionStateService {

    @Autowired
    ScheduleGoogleRepository repository;

    @Override
    public CompetitionState getCompetitionState(String competitionName) throws IOException {
        final String scheduleId = repository.getScheduleId(competitionName);
        return repository.getCompetitionState(scheduleId);
    }

    @Override
    public void save(String competitionName, CompetitionState competitionState) throws IOException {
        final String scheduleId = repository.getScheduleId(competitionName);
        repository.setCompetitionState(scheduleId, competitionState);
    }

}
