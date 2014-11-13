package org.rrabarg.teamcaptain.fixture;

import static java.time.temporal.ChronoUnit.HOURS;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalTime;

import org.rrabarg.teamcaptain.Match;
import org.rrabarg.teamcaptain.MatchBuilder;
import org.rrabarg.teamcaptain.ScheduleRepository;
import org.rrabarg.teamcaptain.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleFixture {

    Logger log = LoggerFactory.getLogger(this.getClass());

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

    public void deleteSchedule() throws IOException {
        if (scheduleId != null) {
            scheduleRepository.deleteSchedule(scheduleId);
        }
    }

    public void clearSchedule() throws IOException {
        scheduleRepository.clearSchedule(scheduleId);
    }

    public void clearTestSchedules() throws IOException {
        scheduleRepository
                .clearSchedulesWithSummaryStartingWith("Test schedule ");
    }

    public void scheduleMatch() {
        scheduleService.createMatch(scheduleId, standardMatch());
    }

    private Match standardMatch() {
        return new MatchBuilder().withTitle(aTitle).withDate(aDate)
                .withStartTime(aTime).withEndTime(aEndTime)
                .withLocation(aLocationFirstLine, aLocationPostcode).build();
    }

    public void reset() throws IOException, InterruptedException {
        teardown();
        setup();
    }

    public void setup() throws IOException, InterruptedException {

        if (scheduleId != null) {
            return;
        }

        scheduleId = scheduleRepository.findSchedule(getTestScheduleName());
        if (scheduleId == null) {
            scheduleId = scheduleRepository.addSchedule(getTestScheduleName());
            log.info("Created test schedule " + scheduleId);
        } else {
            log.info("Found test schedule " + scheduleId);
        }
    }

    public void teardown() throws IOException, InterruptedException {
        if (scheduleId != null) {
            clearSchedule();
        }
    }

    private String getTestScheduleName() throws UnknownHostException {
        return "Test schedule-" + Inet4Address.getLocalHost().getHostAddress();
    }

}
