package org.rrabarg.teamcaptain.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Schedule {

    @Id
    private String id;

    private final List<Match> matches;

    private String poolId;

    /**
     * Used for entity creation
     */
    public Schedule(Match... match) {
        this.matches = Arrays.asList(match);
    }

    /**
     * Used for load from persistent store
     */
    public Schedule(String scheduleId, String playerPoolId, List<Match> matches) {
        this.id = scheduleId;
        this.matches = matches;
        this.poolId = playerPoolId;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public String getPlayerPoolId() {
        return poolId;
    }

    public String getId() {
        return id;
    }

    public void setId(String scheduleId) {
        this.id = scheduleId;
    }

    public void setPlayerPoolId(String poolId) {
        this.poolId = poolId;
    }

    public Collection<Match> getUpcomingMatches() {
        return getMatches();
    }

    @Override
    public String toString() {
        return "Schedule " + matches;
    }
}
