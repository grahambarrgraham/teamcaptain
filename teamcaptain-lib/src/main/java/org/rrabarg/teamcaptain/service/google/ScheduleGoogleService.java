package org.rrabarg.teamcaptain.service.google;

import java.io.IOException;
import java.util.List;

import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.rrabarg.teamcaptain.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleGoogleService implements ScheduleService {

    Logger log = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    ScheduleGoogleRepository scheduleRepository;

    @Override
    public Schedule findByName(String scheduleName) {
        try {
            final Schedule scheduleByName = scheduleRepository.getScheduleByName(scheduleName);
            log.debug("Loaded schedule " + scheduleName + " with schedule id " + scheduleByName.getId() + ". It has "
                    + scheduleByName.getMatches().size() + " matches.");
            return scheduleByName;
        } catch (final IOException e) {
            throw new RuntimeException("Failed whilst trying to find schedule with name " + scheduleName);
        }
    }

    @Override
    public String saveSchedule(String name, Schedule schedule)
            throws IOException, InterruptedException {

        log.debug("Saving schedule " + name);

        String scheduleId = schedule.getId();

        if (scheduleId == null) {
            scheduleId = getOrCreateScheduleId(name);
            schedule.setId(scheduleId);
        }

        final List<Match> matches = schedule.getMatches();
        log.debug("Saving " + matches.size() + " matches to " + scheduleId);

        for (final Match match : matches) {
            final String matchId = scheduleRepository.scheduleMatch(scheduleId, match);
            log.debug("Saved " + match.getTitle() + " with id " + matchId + " to schedule " + scheduleId);
        }

        return scheduleId;
    }

    private String getOrCreateScheduleId(String scheduleName)
            throws IOException, InterruptedException {

        String scheduleId = scheduleRepository.getScheduleId(scheduleName);

        if (scheduleId == null) {
            scheduleId = scheduleRepository.addSchedule(scheduleName);
        }

        return scheduleId;
    }

    @Override
    public void clearMatches(Schedule schedule) throws IOException {
        log.debug("Clearing all matches for schedule " + schedule.getId());
        scheduleRepository.clearSchedule(schedule.getId());
    }

    @Override
    public void updateMatch(Match match) throws IOException {
        log.debug("Updating match " + match.getTitle() + " with id " + match.getId() + " for schedule "
                + match.getScheduleId());
        scheduleRepository.updateMatch(match);
    }

}
