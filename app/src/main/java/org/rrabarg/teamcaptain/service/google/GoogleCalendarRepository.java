package org.rrabarg.teamcaptain.service.google;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.inject.Provider;

import org.rrabarg.teamcaptain.domain.CompetitionState;
import org.rrabarg.teamcaptain.domain.Location;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.rrabarg.teamcaptain.service.CompetitionStateSerialisationHelper;
import org.rrabarg.teamcaptain.service.WorkflowStateSerialisationHelper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar.Calendars;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

@Repository
@Profile("google")
public class GoogleCalendarRepository {

    Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Provider<Clock> clock;

    @Autowired
    private com.google.api.services.calendar.Calendar googleCalendarClient;

    @Autowired
    private WorkflowStateSerialisationHelper workflowStateSerialisationHelper;

    @Autowired
    private CompetitionStateSerialisationHelper competitionStateSerialisationHelper;

    public Schedule getScheduleByName(String competitionName) throws IOException {
        return getScheduleById(getScheduleIdForName(competitionName));
    }

    public Schedule getScheduleById(String scheduleId) throws IOException {
        if (scheduleId == null) {
            return null;
        }
        return new Schedule(scheduleId, null, getMatchesForScheduleId(scheduleId));
    }

    public CompetitionState getCompetitionState(final String competitionId) throws IOException {
        final CalendarListEntry calendar = getCalendarById(competitionId);

        log.debug("Loading calendar description " + calendar.getDescription() + " for schedule id " + competitionId);

        final CompetitionState competitionState =
                competitionStateSerialisationHelper.fromString(calendar.getDescription());
        return competitionState;
    }

    public void deleteSchedule(String scheduleId) throws IOException {
        getCalendars().delete(scheduleId).execute();
    }

    public String addMatchToSchedule(String scheduleId, Match match)
            throws IOException {
        final String id = getEvents().insert(scheduleId, asEvent(match)).execute().getId();
        match.init(id, scheduleId);
        return id;
    }

    public void clearSchedule(String scheduleId) throws IOException {
        for (final Event event : getEventsForSchedule(scheduleId).getItems()) {
            getEvents().delete(scheduleId, event.getId()).execute();
        }
    }

    public String getScheduleIdForName(String scheduleName) throws IOException {
        final List<CalendarListEntry> items = getCalendarList().getItems();
        for (final CalendarListEntry calendarListEntry : items) {
            if (scheduleName.equals(calendarListEntry.getSummary()) && (!calendarListEntry.isDeleted())) {
                return calendarListEntry.getId();
            }
        }
        return null;
    }

    public List<Match> getMatchesForScheduleId(String scheduleId) throws IOException {
        return getEventsForSchedule(scheduleId).getItems().stream()
                .map(event -> asMatch(scheduleId, event))
                .collect(Collectors.toList());
    }

    public Calendar setCompetitionState(String competitionId, CompetitionState state) throws IOException {
        final Calendar entry = getCalendars().get(competitionId).execute();
        entry.setDescription(competitionStateSerialisationHelper.toString(state));
        return getCalendars().update(competitionId, entry).execute();
    }

    public String addSchedule(String scheduleName) throws IOException {
        final Calendar entry = new Calendar();
        entry.setSummary(scheduleName);
        return getCalendars().insert(entry).execute().getId();
    }

    public void updateEvent(Match match) throws IOException {
        getEvents().update(match.getScheduleId(), match.getId(), asEvent(match)).execute();
    }

    public Set<String> getAllScheduleIds(String schedulePrefix) throws IOException {
        return getCalendarList().getItems().stream()
                .filter(e -> e.getSummary().startsWith(schedulePrefix))
                .filter(e -> !e.isDeleted())
                .map(e -> e.getId())
                .collect(Collectors.toSet());
    }

    private Event asEvent(Match match) {
        final Event event = new Event();

        event.setSummary(match.getTitle());

        final DateTime start = asGoogleDateTime(match.getStartDateTime());
        final DateTime end = asGoogleDateTime(match.getEndDateTime());

        event.setStart(new EventDateTime().setDateTime(start));
        event.setEnd(new EventDateTime().setDateTime(end));
        event.setLocation(match.getLocation().toString());
        event.setDescription(workflowStateSerialisationHelper.toString(match.getWorkflowState()));

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

    private Events getEventsForSchedule(String calendarId) throws IOException {
        return getEvents().list(calendarId).execute();
    }

    private com.google.api.services.calendar.Calendar.Events getEvents() {
        return googleCalendarClient.events();
    }


    private Match asMatch(String scheduleId, Event event) {
        return new Match(
                event.getId(),
                scheduleId,
                event.getSummary(),
                asJavaDateTime(event.getStart().getDateTime(), getTimezone(event)),
                asJavaDateTime(event.getEnd().getDateTime(), getTimezone(event)), Location.fromString(event
                .getLocation()), workflowStateSerialisationHelper.fromString(event.getDescription()));
    }

    private TimeZone getTimezone(Event event) {
        final String timeZone = event.getStart().getTimeZone();
        return timeZone == null ? TimeZone.getDefault() : TimeZone.getTimeZone(timeZone);
    }

    private CalendarListEntry getCalendarById(final String calendarId) throws IOException {
        return googleCalendarClient.calendarList().get(calendarId).execute();
    }


}
