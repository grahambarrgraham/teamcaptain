package org.rrabarg.teamcaptain.repository.google;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.domain.Location;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Schedule;
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

    public Schedule getScheduleByName(String competitionName) throws IOException {
        final String scheduleId = getScheduleId(competitionName);

        if (scheduleId == null) {
            return null;
        }

        final CalendarListEntry calendar = getCalendarById(scheduleId);

        return new Schedule(scheduleId, calendar.getDescription(),
                getUpcomingMatches(scheduleId, 10, ChronoUnit.DAYS));
    }

    private CalendarListEntry getCalendarById(final String calendarId) throws IOException {
        return googleCalendarClient.calendarList().get(calendarId).execute();
    }

    public String addSchedule(String scheduleTitle, String playerPoolId) throws IOException {
        return addCalendar(scheduleTitle, playerPoolId).getId();
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

    public String getScheduleId(String scheduleName) throws IOException {
        final List<CalendarListEntry> items = getCalendarList().getItems();
        for (final CalendarListEntry calendarListEntry : items) {
            if (scheduleName.equals(calendarListEntry.getSummary())) {
                return calendarListEntry.getId();
            }
        }
        return null;
    }

    public List<Match> getUpcomingMatches(String scheduleId,
            int numberOfDaysTillMatch, ChronoUnit days) throws IOException {
        return getEventsForSchedule(scheduleId).getItems().stream().map(event -> asMatch(event))
                .filter(match -> isUpcoming(match, numberOfDaysTillMatch, days)).collect(Collectors.toList());
    }

    public Calendar setPlayerPoolId(String scheduleId, String playerPoolId) throws IOException {
        final Calendar entry = getCalendars().get(scheduleId).execute();
        setPlayerPoolData(playerPoolId, entry);
        return getCalendars().update(scheduleId, entry).execute();
    }

    private void setPlayerPoolData(String playerPoolId, final Calendar entry) {
        entry.setDescription(playerPoolId);
    }

    private boolean isUpcoming(Match match, int numberOfDaysTillMatch, ChronoUnit days) {
        return Instant.now(clock.get()).plus(numberOfDaysTillMatch, days).isAfter(match.getStartDateTime().toInstant());
    }

    private Match asMatch(Event event) {

        return new Match(
                event.getId(),
                event.getSummary(),
                asJavaDateTime(event.getStart().getDateTime(), getTimezone(event)),
                asJavaDateTime(event.getEnd().getDateTime(), getTimezone(event)),
                Location.fromString(event.getLocation()));
    }

    private TimeZone getTimezone(Event event) {
        final String timeZone = event.getStart().getTimeZone();
        return timeZone == null ? TimeZone.getDefault() : TimeZone.getTimeZone(timeZone);
    }

    private Calendar addCalendar(String summary, String playerPoolId) throws IOException {
        final Calendar entry = new Calendar();
        entry.setSummary(summary);
        entry.setDescription("Players : " + playerPoolId);
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
