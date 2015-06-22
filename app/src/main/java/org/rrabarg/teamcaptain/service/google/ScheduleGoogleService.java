package org.rrabarg.teamcaptain.service.google;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("google")
public class ScheduleGoogleService {

    private static final String SCHEDULE_PREFIX = "TeamCaptainSchedule - ";

    Logger log = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    GoogleCalendarRepository scheduleRepository;

    public Schedule getScheduleById(String scheduleId) {
        try {
            return scheduleRepository.getScheduleById(scheduleId);
        } catch (final IOException e) {
            throw new RuntimeException("Failed whilst trying to find schedule with id " + scheduleId);
        }
    }

    public String saveSchedule(String scheduleName, Schedule schedule)
            throws IOException, InterruptedException {

        log.debug("Saving schedule " + scheduleName);

        String scheduleId = schedule.getId();

        if (scheduleId == null) {
            scheduleId = getOrCreateScheduleId(scheduleName);
            schedule.setId(scheduleId);
        }

        final List<Match> matches = schedule.getMatches();
        log.debug("Saving " + matches.size() + " matches to " + scheduleId);

        for (final Match match : matches) {
            final String matchId = scheduleRepository.addMatchToSchedule(scheduleId, match);
            log.debug("Saved " + match.getTitle() + " with id " + matchId + " to schedule " + scheduleId);
        }

        return scheduleId;
    }

    private String getOrCreateScheduleId(String scheduleName)
            throws IOException, InterruptedException {

        String scheduleId = getScheduleIdForName(scheduleName);

        if (scheduleId == null) {
            scheduleId = scheduleRepository.addSchedule(getNameWithPrefix(scheduleName));
        }

        return scheduleId;
    }

    public String getScheduleIdForName(String scheduleName) throws IOException {
        return scheduleRepository.getScheduleIdForName(getNameWithPrefix(scheduleName));
    }

    public void updateMatch(Match match) {
        log.debug("Updating match " + match.getTitle() + " with id " + match.getId() + " for schedule "
                + match.getScheduleId());
        try {
            scheduleRepository.updateEvent(match);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update the match " + match);
        }
    }

    public Collection<String> getAllScheduleIds() throws IOException {
        return scheduleRepository.getAllScheduleIds(SCHEDULE_PREFIX);
    }

    private String getNameWithPrefix(String scheduleName) {
        return SCHEDULE_PREFIX + scheduleName;
    }

}
