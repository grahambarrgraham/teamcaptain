package org.rrabarg.teamcaptain;

import java.io.IOException;
import java.util.List;

import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    public Schedule findByName(String scheduleName) {
        try {
            return scheduleRepository.getScheduleByName(scheduleName);
        } catch (final IOException e) {
            throw new RuntimeException("Failed whilst trying to find schedule with name " + scheduleName);
        }
    }

    public String createMatch(String scheduleId, Match match) {
        try {
            return scheduleRepository.scheduleMatch(scheduleId, match);
        } catch (final IOException ioe) {
            throw new ScheduleException("Failed to create match " + match
                    + " on schedule " + scheduleId, ioe);
        }
    }

    public String saveSchedule(String name, String poolId, Schedule schedule) throws IOException, InterruptedException {
        String scheduleId = schedule.getId();

        if (scheduleId == null) {
            scheduleId = getOrCreateScheduleId(name, poolId);
            schedule.setId(scheduleId);
        }

        final List<Match> matches = schedule.getMatches();
        for (final Match match : matches) {
            scheduleRepository.scheduleMatch(scheduleId, match);
        }

        schedule.setPlayerPoolId(poolId);

        return scheduleId;
    }

    private String getOrCreateScheduleId(String scheduleName, String poolId) throws IOException, InterruptedException {

        String scheduleId = scheduleRepository.getScheduleId(scheduleName);

        if (scheduleId == null) {
            scheduleId = scheduleRepository.addSchedule(scheduleName, poolId);
        } else {
            scheduleRepository.setPlayerPoolId(scheduleId, poolId);
        }

        return scheduleId;
    }

    public void clearMatches(Schedule schedule) throws IOException {
        scheduleRepository.clearSchedule(schedule.getId());
    }

}
