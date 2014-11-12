package org.rrabarg.teamcaptain;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleService {

    @Autowired
    ScheduleRepository repository;

    public String createMatch(String scheduleId, Match match) {
        try {
            return repository.scheduleMatch(scheduleId, match);
        } catch (final IOException ioe) {
            throw new ScheduleException("Failed to create match " + match
                    + " on schedule " + scheduleId, ioe);
        }
    }

}
