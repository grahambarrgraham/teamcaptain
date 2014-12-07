package org.rrabarg.teamcaptain.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.rrabarg.teamcaptain.domain.Location;
import org.rrabarg.teamcaptain.domain.Match;

public class MatchBuilder {

    private String aTitle;
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    private String aLocationFirstLine;
    private String aLocationPostcode;

    public Match build() {
        return new Match(aTitle, startDateTime, endDateTime, new Location(aLocationFirstLine, aLocationPostcode));
    }

    public MatchBuilder withTitle(String aTitle) {
        this.aTitle = aTitle;
        return this;
    }

    public MatchBuilder withLocation(String aLocationFirstLine,
            String aLocationPostcode) {
        this.aLocationFirstLine = aLocationFirstLine;
        this.aLocationPostcode = aLocationPostcode;
        return this;
    }

    public MatchBuilder withStart(LocalDate startDate, LocalTime startTime) {
        this.startDateTime = asZonedDateTime(startDate, startTime);
        return this;
    }

    public MatchBuilder withEnd(LocalDate endDate, LocalTime endTime) {
        this.endDateTime = asZonedDateTime(endDate, endTime);
        return this;
    }

    private ZonedDateTime asZonedDateTime(LocalDate date, LocalTime time) {
        return ZonedDateTime.of(date, time, ZoneId.systemDefault());
    }

}
