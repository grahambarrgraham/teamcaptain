package org.rrabarg.teamcaptain;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.domain.Location;
import org.rrabarg.teamcaptain.domain.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar.Calendars;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

@Component
public class ScheduleRepository {

    @Autowired
    private Provider<Clock> clock;

    @Autowired
    private com.google.api.services.calendar.Calendar googleCalendarClient;

    public String addSchedule(String summary) throws IOException {
        return addCalendar(summary).getId();
    }

    public void deleteSchedule(String sheduleId) throws IOException {
        getCalendars().delete(sheduleId).execute();
    }

    public String scheduleMatch(String scheduleId, Match match)
            throws IOException {
        return getEvents().insert(scheduleId, asEvent(match)).execute().getId();
    }

    public void clearSchedule(String scheduleId) throws IOException {
        for (final Event event : getEventsForSchedule(scheduleId).getItems()) {
            getEvents().delete(scheduleId, event.getId()).execute();
        }
    }

    public void clearSchedulesWithSummaryStartingWith(String string) throws IOException {
        for (final CalendarListEntry calendarListEntry : getCalendarList().getItems()) {

            if (calendarListEntry.getSummary().startsWith(string)) {
                getCalendars().delete(calendarListEntry.getId()).execute();
            }

        }
    }

    public String findSchedule(String scheduleName) throws IOException {
        final List<CalendarListEntry> items = getCalendarList().getItems();
        for (final CalendarListEntry calendarListEntry : items) {
            if (scheduleName.equals(calendarListEntry.getSummary())) {
                return calendarListEntry.getId();
            }
        }
        return null;
    }

    public Collection<Match> getUpcomingMatches(String scheduleId,
            int numberOfDaysTillMatch, ChronoUnit days) throws IOException {
        return getEventsForSchedule(scheduleId).getItems().stream().map(event -> asMatch(event))
                .filter(match -> isUpcoming(match, numberOfDaysTillMatch, days)).collect(Collectors.toList());
    }

    private boolean isUpcoming(Match match, int numberOfDaysTillMatch, ChronoUnit days) {
        return Instant.now(clock.get()).plus(numberOfDaysTillMatch, days).isAfter(match.getStartDateTime().toInstant());
    }

    private Match asMatch(Event event) {

        return new Match(
                event.getSummary(),
                asJavaDateTime(event.getStart().getDateTime(), getTimezone(event)),
                asJavaDateTime(event.getEnd().getDateTime(), getTimezone(event)),
                Location.fromString(event.getLocation()));
    }

    private TimeZone getTimezone(Event event) {
        final String timeZone = event.getStart().getTimeZone();
        return timeZone == null ? TimeZone.getDefault() : TimeZone.getTimeZone(timeZone);
    }

    private Calendar addCalendar(String summary) throws IOException {
        final Calendar entry = new Calendar();
        entry.setSummary(summary);
        return getCalendars().insert(entry).execute();
    }

    private Event asEvent(Match match) {
        final Event event = new Event();

        event.setSummary(match.getTitle());

        final DateTime start = asGoogleDateTime(match.getStartDateTime());
        final DateTime end = asGoogleDateTime(match.getEndDateTime());

        event.setStart(new EventDateTime().setDateTime(start));
        event.setEnd(new EventDateTime().setDateTime(end));
        event.setLocation(match.getLocation().toString());

        return event;
    }

    private DateTime asGoogleDateTime(ZonedDateTime dateTime) {
        return new DateTime(Date.from(dateTime.toInstant()), TimeZone.getTimeZone(dateTime.getZone()));
    }

    private ZonedDateTime asJavaDateTime(DateTime dateTime, TimeZone timezone) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime.getValue()), timezone.toZoneId());
    }

    private Calendars getCalendars() {
        return googleCalendarClient.calendars();
    }

    private CalendarList getCalendarList() throws IOException {
        return googleCalendarClient.calendarList().list().execute();
    }

    private Events getEventsForSchedule(String scheduleId) throws IOException {
        return getEvents().list(scheduleId).execute();
    }

    private com.google.api.services.calendar.Calendar.Events getEvents() {
        return googleCalendarClient.events();
    }

}
