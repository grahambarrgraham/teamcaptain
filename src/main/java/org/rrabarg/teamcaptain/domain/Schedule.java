package org.rrabarg.teamcaptain.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.rrabarg.teamcaptain.SelectionStrategy;

@Entity
public class Schedule {

    @Id
    private String id;

    private final List<Match> matches;

    private CompetitionState state;

    /**
     * Used for entity creation
     */
    public Schedule(Match... match) {
        this.matches = Arrays.asList(match);
    }

    /**
     * Used for load from persistent store
     */
    public Schedule(String scheduleId, CompetitionState state, List<Match> matches) {
        this.id = scheduleId;
        this.matches = matches;
        this.state = state;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public String getPlayerPoolId() {
        return state.getPlayerPoolId();
    }

    public String getId() {
        return id;
    }

    public void setId(String scheduleId) {
        this.id = scheduleId;
    }

    public Collection<Match> getUpcomingMatches() {
        return getMatches();
    }

    @Override
    public String toString() {
        return "Schedule " + matches;
    }

    public void setState(CompetitionState state) {
        this.state = state;
    }

    public SelectionStrategy getSelectionStrategy() {
        return state.getSelectionStrategy();
    }

    public CompetitionState getCompetitionState() {
        return state;
    }
}
