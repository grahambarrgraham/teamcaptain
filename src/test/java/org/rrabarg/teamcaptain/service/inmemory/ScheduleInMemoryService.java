package org.rrabarg.teamcaptain.service.inmemory;

import java.io.IOException;

import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.rrabarg.teamcaptain.service.ScheduleService;
import org.springframework.stereotype.Component;

@Component
public class ScheduleInMemoryService implements ScheduleService {

    @Override
    public Schedule findByName(String scheduleName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String saveSchedule(String name, Schedule schedule) throws IOException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearMatches(Schedule schedule) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateMatch(Match match) throws IOException {
        // no-op
    }
}
