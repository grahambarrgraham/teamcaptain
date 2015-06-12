package org.rrabarg.teamcaptain.service;

import java.io.IOException;

import org.rrabarg.teamcaptain.domain.CompetitionState;

public interface CompetitionStateService {

    CompetitionState getCompetitionState(String competitionName) throws IOException;

    void save(String competitionName, CompetitionState competitionState) throws IOException;

}
