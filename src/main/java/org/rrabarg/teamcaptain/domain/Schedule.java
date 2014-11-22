package org.rrabarg.teamcaptain.domain;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Schedule {

    @Id
    private final String id;

    private final List<Match> matches;

    public Schedule(String scheduleId, Match... match) {
        this.id = scheduleId;
        this.matches = Arrays.asList(match);
    }

    public List<Match> getMatches() {
        return matches;
    }

}
