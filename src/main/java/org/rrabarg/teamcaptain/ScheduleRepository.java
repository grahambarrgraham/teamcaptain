package org.rrabarg.teamcaptain;

import java.io.IOException;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

@Component
public class ScheduleRepository {

    @Autowired
    private com.google.api.services.calendar.Calendar googleCalendarClient;

    public String addSchedule(String summary) throws IOException {
        return addCalendar(summary).getId();
    }

    public void deleteSchedule(String sheduleId) throws IOException {
        googleCalendarClient.calendars().delete(sheduleId);
    }

    public String scheduleMatch(String scheduleId, Match match)
            throws IOException {
        return googleCalendarClient.events()
                .insert(scheduleId, newEvent(match)).execute().getId();
    }

    private Calendar addCalendar(String summary) throws IOException {
        final Calendar entry = new Calendar();
        entry.setSummary(summary);
        return googleCalendarClient.calendars().insert(entry).execute();
    }

    private Event newEvent(Match match) {
        final Event event = new Event();

        event.setSummary(match.getTitle());

        final DateTime start = new DateTime(
                match.getStartZonedDateTimeInstant(), TimeZone.getDefault());

        final DateTime end = new DateTime(match.getEndZonedDateTimeInstant(),
                TimeZone.getDefault());

        event.setStart(new EventDateTime().setDateTime(start));
        event.setEnd(new EventDateTime().setDateTime(end));

        event.setLocation(match.getLocationString());

        return event;
    }

}
