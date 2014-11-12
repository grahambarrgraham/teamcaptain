package org.rrabarg.teamcaptain;

import java.time.LocalDate;
import java.time.LocalTime;

public class MatchBuilder {

    private String aTitle;
    private LocalDate aDate;
    private LocalTime aTime;
    private LocalTime aEndTime;
    private String aLocationFirstLine;
    private String aLocationPostcode;

    public Match build() {
        return new Match(aTitle, aDate, aTime, aEndTime, aLocationFirstLine,
                aLocationPostcode);
    }

    public MatchBuilder withTitle(String aTitle) {
        this.aTitle = aTitle;
        return this;
    }

    public MatchBuilder withDate(LocalDate aDate) {
        this.aDate = aDate;
        return this;
    }

    public MatchBuilder withStartTime(LocalTime aTime) {
        this.aTime = aTime;
        return this;
    }

    public MatchBuilder withEndTime(LocalTime aEndTime) {
        this.aEndTime = aEndTime;
        return this;
    }

    public MatchBuilder withLocation(String aLocationFirstLine,
            String aLocationPostcode) {
        this.aLocationFirstLine = aLocationFirstLine;
        this.aLocationPostcode = aLocationPostcode;
        return this;
    }

}
