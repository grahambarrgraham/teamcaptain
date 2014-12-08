package org.rrabarg.teamcaptain.service;

import java.io.IOException;
import java.util.List;

import org.rrabarg.teamcaptain.SelectionStrategy;
import org.rrabarg.teamcaptain.domain.CompetitionState;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.rrabarg.teamcaptain.repository.google.ScheduleRepository;
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

    public String saveSchedule(String name, Schedule schedule, String playerPoolId, SelectionStrategy selectionStrategy)
            throws IOException, InterruptedException {

        final CompetitionState competitionState = new CompetitionState(playerPoolId, selectionStrategy);
        String scheduleId = schedule.getId();

        if (scheduleId == null) {
            scheduleId = getOrCreateScheduleId(name, competitionState);
            schedule.setId(scheduleId);
        }

        final List<Match> matches = schedule.getMatches();
        for (final Match match : matches) {
            scheduleRepository.scheduleMatch(scheduleId, match);
        }

        schedule.setState(competitionState);

        return scheduleId;
    }

    private String getOrCreateScheduleId(String scheduleName, CompetitionState state)
            throws IOException, InterruptedException {

        String scheduleId = scheduleRepository.getScheduleId(scheduleName);

        if (scheduleId == null) {
            scheduleId = scheduleRepository.addSchedule(scheduleName, state);
        } else {
            scheduleRepository.setCompetitionState(scheduleId, state);
        }

        return scheduleId;
    }

    public void clearMatches(Schedule schedule) throws IOException {
        scheduleRepository.clearSchedule(schedule.getId());
    }

    public void updateMatch(Match match) throws IOException {
        scheduleRepository.updateMatch(match);
    }

}
