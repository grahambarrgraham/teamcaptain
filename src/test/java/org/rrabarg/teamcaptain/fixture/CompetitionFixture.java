package org.rrabarg.teamcaptain.fixture;

import static java.time.temporal.ChronoUnit.HOURS;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.rrabarg.teamcaptain.CompetitionService;
import org.rrabarg.teamcaptain.MatchBuilder;
import org.rrabarg.teamcaptain.TestClockFactory;
import org.rrabarg.teamcaptain.WorkflowService;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.Gender;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PoolOfPlayers;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompetitionFixture {

    Logger log = LoggerFactory.getLogger(this.getClass());

    String aTitle = "A test match";
    LocalDate aDate = LocalDate.of(2014, 3, 20);
    LocalTime aTime = LocalTime.of(20, 00);
    LocalTime aEndTime = aTime.plus(3, HOURS);
    String aLocationFirstLine = "1 some street";
    String aLocationPostcode = "EH1 1YA";

    @Autowired
    CompetitionService competitionService;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    TestClockFactory clockFactory;

    private final Player joe = new Player("Joe", "Ninety", Gender.Male, null, null);

    private final Player stacy = new Player("Stacy", "Fignorks", Gender.Female, null, null);

    private final Player peter = new Player("Peter", "Pan", Gender.Male, null, null);

    private Competition competition;

    public void clearCompetition() {

        if (competition == null) {
            return;
        }

        competitionService.clearCompetition(competition);
    }

    private Match standardMatch() {
        return new MatchBuilder().withTitle(aTitle)
                .withStart(aDate, aTime)
                .withEnd(aDate, aEndTime)
                .withLocation(aLocationFirstLine, aLocationPostcode).build();
    }

    public void setup() {
        clearCompetition();
    }

    public void teardown() {
        clearCompetition();
    }

    public void fixDateTimeBeforeMatch(int amount, ChronoUnit unit) {
        clockFactory.fixInstant(aDate.atTime(aTime).minus(amount, unit).atZone(ZoneId.systemDefault()).toInstant());
    }

    public void nudgeScheduler() throws IOException {
        workflowService.checkForUpcomingMatches(competition.getName());
    }

    public void createCompetition() {
        competition = standardCompetition();
        competitionService.saveCompetition(competition);
    }

    public void checkAllNotificationsWereSent() {
        // TODO Auto-generated method stub
    }

    private Competition standardCompetition() {
        return new Competition(getTestCompetitionName(),
                standardSchedule(getTestCompetitionName()),
                standardPoolOfPlayers(getTestCompetitionName()));
    }

    private PoolOfPlayers standardPoolOfPlayers(String competitionName) {
        return new PoolOfPlayers(joe, stacy, peter);
    }

    private Schedule standardSchedule(String scheduleName) {
        return new Schedule(standardMatch());
    }

    private String getTestCompetitionName() {
        return "Test competition-" + getUniqueNameForHost();
    }

    private String getUniqueNameForHost() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (final UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}