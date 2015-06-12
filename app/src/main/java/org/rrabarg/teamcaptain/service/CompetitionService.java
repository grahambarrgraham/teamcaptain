package org.rrabarg.teamcaptain.service;

import java.util.Collection;

import org.rrabarg.teamcaptain.domain.Competition;

public interface CompetitionService {

    public abstract String saveCompetition(Competition competition);

    public abstract Competition findCompetitionByName(String competitionName);

    public abstract void clearCompetition(Competition competition);

    public abstract Collection<String> getCompetitionIds();

}