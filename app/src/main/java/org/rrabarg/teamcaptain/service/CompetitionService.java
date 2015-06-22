package org.rrabarg.teamcaptain.service;

import java.io.IOException;
import java.util.Collection;

import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;

public interface CompetitionService {

    String createCompetition(Competition competition);

    Collection<String> getCompetitionIds();

    Competition findCompetitionById(String id);

    Competition getCompetitionByName(String competitionName) throws IOException;

    void updateMatch(Match match) throws IOException;

    void clearCompetition(Competition comp);
}