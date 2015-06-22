package org.rrabarg.teamcaptain.service.google;

import java.io.IOException;

import org.rrabarg.teamcaptain.domain.CompetitionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("google")
public class CompetitionStateGoogleService {

    @Autowired
    GoogleCalendarRepository repository;

    public CompetitionState getCompetitionState(String competitionId) throws IOException {
        return repository.getCompetitionState(competitionId);
    }

    public void save(String competitionId, CompetitionState competitionState) throws IOException {
        repository.setCompetitionState(competitionId, competitionState);
    }

}
