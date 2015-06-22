package org.rrabarg.teamcaptain.service.inmemory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.service.CompetitionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("inmemory")
public class CompetitionInMemoryService implements CompetitionService {

    Map<String, Competition> map = new HashMap<String, Competition>();

    @Override
    public String createCompetition(Competition competition) {
        final String id = competition.getName();
        map.put(id, competition);
        competition.setId(id);
        return id;
    }

    @Override
    public Competition findCompetitionById(String id) {
        return map.get(id);
    }

    @Override
    public Competition getCompetitionByName(String competitionName) {
        return findCompetitionById(competitionName);
    }

    @Override
    public void updateMatch(Match match) {
        match.getScheduleId();
    }

    @Override
    public void clearCompetition(Competition comp) {
        map.clear();
    }

    @Override
    public Collection<String> getCompetitionIds() {
        return map.keySet();
    }

}
