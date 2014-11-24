package org.rrabarg.teamcaptain.domain;

import java.time.ZonedDateTime;

import javax.persistence.Entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
public class Match {

    private final String title;
    private final ZonedDateTime startDateTime;
    private final ZonedDateTime endDateTime;
    private final Location location;
    private final MatchWorkflow matchWorkflow;

    public Match(String title, ZonedDateTime startDateTime, ZonedDateTime endDateTime, Location location) {
        this(null, title, startDateTime, endDateTime, location, new MatchWorkflow());
    }

    public Match(String id, String title, ZonedDateTime startDateTime, ZonedDateTime endDateTime, Location location,
            MatchWorkflow matchWorkflow) {
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.matchWorkflow = matchWorkflow;
        this.matchWorkflow.setMatch(this);
    }

    public String getTitle() {
        return title;
    }

    public Location getLocation() {
        return location;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public MatchWorkflow getWorkflow() {
        return matchWorkflow;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
