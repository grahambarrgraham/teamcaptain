package org.rrabarg.teamcaptain.service.inmemory;

import java.util.HashMap;
import java.util.Map;

import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.service.CompetitionService;
import org.springframework.stereotype.Component;

@Component
public class CompetitionInMemoryService implements CompetitionService {

    Map<String, Competition> map = new HashMap<String, Competition>();

    @Override
    public String saveCompetition(Competition competition) {
        final String id = competition.getName();
        map.put(id, competition);
        competition.setId(id);
        return id;
    }

    @Override
    public Competition findCompetitionByName(String competitionName) {
        return map.get(competitionName);
    }

    @Override
    public void clearCompetition(Competition competition) {
        map.remove(competition.getName());
    }

}
