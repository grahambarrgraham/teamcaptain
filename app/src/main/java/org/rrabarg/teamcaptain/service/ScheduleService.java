package org.rrabarg.teamcaptain.service;

import java.io.IOException;

import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Schedule;

public interface ScheduleService {

    Schedule findByName(String scheduleName);

    String saveSchedule(String name, Schedule schedule)
            throws IOException, InterruptedException;

    void clearMatches(Schedule schedule) throws IOException;

    void updateMatch(Match match) throws IOException;

}