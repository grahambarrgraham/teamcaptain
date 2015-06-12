package org.rrabarg.teamcaptain.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Entity
public class Schedule {

    //@Id
    private String id;

    private final List<Match> matches;

    private Competition competition;

    /**
     * Used for entity creation
     */
    public Schedule(Match... match) {
        this.matches = Arrays.asList(match);
    }

    /**
     * Used for load from persistent store
     */
    public Schedule(String scheduleId, Competition competition, List<Match> matches) {
        this.id = scheduleId;
        this.matches = matches;
        this.competition = competition;
    }

    public synchronized List<Match> getMatches() {
        return new ArrayList<>(matches);
    }

    public String getPlayerPoolId() {
        return competition.getPlayerPool().getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String scheduleId) {
        this.id = scheduleId;
    }

    @Override
    public String toString() {
        return "Schedule " + matches;
    }

    public synchronized void updateMatch(Match match) {
        matches.removeIf(a -> a.equals(match));
        matches.add(match);
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
