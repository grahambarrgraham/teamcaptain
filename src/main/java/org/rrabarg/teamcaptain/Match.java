package org.rrabarg.teamcaptain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Match {

    private final String aTitle;
    private final LocalDate aDate;
    private final LocalTime aTime;
    private final LocalTime aEndTime;
    private final String aLocationFirstLine;
    private final String aLocationPostcode;

    public Match(String aTitle, LocalDate aDate, LocalTime aTime,
            LocalTime aEndTime, String aLocationFirstLine,
            String aLocationPostcode) {
        this.aTitle = aTitle;
        this.aDate = aDate;
        this.aTime = aTime;
        this.aEndTime = aEndTime;
        this.aLocationFirstLine = aLocationFirstLine;
        this.aLocationPostcode = aLocationPostcode;
    }

    public String getTitle() {
        return aTitle;
    }

    public LocalDate getDate() {
        return aDate;
    }

    public LocalTime getTime() {
        return aTime;
    }

    public LocalTime getaEndTime() {
        return aEndTime;
    }

    public String getLocationFirstLine() {
        return aLocationFirstLine;
    }

    public String getLocationPostcode() {
        return aLocationPostcode;
    }

    public Date getStartZonedDateTimeInstant() {
        return Date.from(LocalDateTime.of(getDate(), getTime())
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getEndZonedDateTimeInstant() {
        return Date.from(LocalDateTime.of(getDate(), getaEndTime())
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    public String getLocationString() {
        return getLocationFirstLine() + ", " + getLocationPostcode();
    }

}
