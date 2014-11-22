package org.rrabarg.teamcaptain.domain;

import java.time.ZonedDateTime;

import javax.persistence.Entity;

@Entity
public class Match {

    private final String title;
    private final ZonedDateTime startDateTime;
    private final ZonedDateTime endDateTime;
    private final Location location;

    public Match(String title, ZonedDateTime startDateTime, ZonedDateTime endDateTime, Location location) {
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
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

}
