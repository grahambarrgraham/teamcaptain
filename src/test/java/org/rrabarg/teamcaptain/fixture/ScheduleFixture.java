package org.rrabarg.teamcaptain.fixture;

import static java.time.temporal.ChronoUnit.HOURS;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import org.rrabarg.teamcaptain.Match;
import org.rrabarg.teamcaptain.MatchBuilder;
import org.rrabarg.teamcaptain.ScheduleRepository;
import org.rrabarg.teamcaptain.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleFixture {

    String aTitle = "A test match";
    LocalDate aDate = LocalDate.of(2011, 3, 20);
    LocalTime aTime = LocalTime.of(20, 00);
    LocalTime aEndTime = aTime.plus(3, HOURS);
    String aLocationFirstLine = "1 some street";
    String aLocationPostcode = "EH1 1YA";

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    ScheduleRepository scheduleRepository;
    private String scheduleId;

    public void createSchedule() throws IOException {
        scheduleId = scheduleRepository.addSchedule("Test schedule "
                + System.currentTimeMillis());
    }

    public void deleteSchedule() throws IOException {
        scheduleRepository.deleteSchedule(scheduleId);
    }

    public void scheduleMatch() {
        scheduleService.createMatch(scheduleId, standardMatch());
    }

    private Match standardMatch() {
        return new MatchBuilder().withTitle(aTitle).withDate(aDate)
                .withStartTime(aTime).withEndTime(aEndTime)
                .withLocation(aLocationFirstLine, aLocationPostcode).build();
    }

    public void reset() throws IOException {
        if (scheduleId != null) {
            deleteSchedule();
        }
        createSchedule();
    }

}
