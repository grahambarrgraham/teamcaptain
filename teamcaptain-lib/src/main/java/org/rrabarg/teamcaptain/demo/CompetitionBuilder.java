package org.rrabarg.teamcaptain.demo;

import static java.time.temporal.ChronoUnit.HOURS;

import java.time.LocalDate;
import java.time.LocalTime;

import org.rrabarg.teamcaptain.NotificationStrategy;
import org.rrabarg.teamcaptain.SelectionStrategy;
import org.rrabarg.teamcaptain.domain.Competition;
import org.rrabarg.teamcaptain.domain.ContactDetail;
import org.rrabarg.teamcaptain.domain.Gender;
import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerPool;
import org.rrabarg.teamcaptain.domain.Schedule;
import org.rrabarg.teamcaptain.domain.TeamCaptain;
import org.rrabarg.teamcaptain.strategy.BasicNotificationStrategy;
import org.rrabarg.teamcaptain.strategy.ContactPreference;
import org.rrabarg.teamcaptain.strategy.SimpleGenderedSelectionStrategy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompetitionBuilder {

    public static final String DEFAULT_COMPETITION_NAME = "All England Championship";

    public static final TeamCaptain teamCaptain = new TeamCaptain(new ContactDetail("nick", "peters",
            "nick@nomail.com",
            "3434"), ContactPreference.emailOnly(), Gender.Female);

    public static final Player stacy = new Player("Stacy", "Fignorks", Gender.Female, "stacy@nomail.com", "1111");
    public static final Player sally = new Player("Sally", "Figpigs", Gender.Female, "sally@nomail.com", "2222");
    public static final Player sara = new Player("Sara", "Figcows", Gender.Female, "sara@nomail.com", "3333");
    public static final Player sharon = new Player("Sharon", "Fighens", Gender.Female, "sharon@nomail.com", "4444");
    public static final Player sonia = new Player("Sonia", "Fighorse", Gender.Female, "sonia@nomail.com", "5555");
    public static final Player safron = new Player("Safron", "Fignigs", Gender.Female, "safron@nomail.com", "6666");

    public static final Player joe = new Player("Joe", "Ninety", Gender.Male, "joe@nomail.com", "7777");
    public static final Player john = new Player("John", "Ten", Gender.Male, "john@nomail.com", "8888");
    public static final Player josh = new Player("Josh", "Twenty", Gender.Male, "josh@nomail.com", "9999");
    public static final Player jimmy = new Player("Jimmy", "Thirty", Gender.Male, "jimmy@nomail.com", "1119");
    public static final Player jeff = new Player("Jeff", "Fourty", Gender.Male, "jeff@nomail.com", "2229");
    public static final Player jed = new Player("Jed", "Fifty", Gender.Male, "jed@nomail.com", "3339");
    public static final Player peter = new Player("Peter", "Pan", Gender.Male, "peterpan@nomail.com", "4449");

    private Player[] players = new Player[] { stacy, sharon, safron, joe, jimmy, peter };

    private SelectionStrategy selectionStrategy = new SimpleGenderedSelectionStrategy(3, 3);

    private NotificationStrategy notificationStrategy = new BasicNotificationStrategy(7, 15, 4,
            ContactPreference.emailOnly());

    private final LocalDate aDate = LocalDate.of(2014, 3, 20);
    private final LocalTime aTime = LocalTime.of(20, 00);
    private final LocalTime aEndTime = aTime.plus(3, HOURS);
    private final String aLocationFirstLine = "1 some street";
    private final String aLocationPostcode = "EH1 1YA";
    private final String aTitle = "A test match";

    private Schedule standardSchedule(String scheduleName) {
        return new Schedule(standardMatch());
    }

    private Match standardMatch() {
        return new MatchBuilder().withTitle(aTitle)
                .withStart(aDate, aTime)
                .withEnd(aDate, aEndTime)
                .withLocation(aLocationFirstLine, aLocationPostcode).build();
    }

    private PlayerPool standardPlayerPool(String competitionName) {
        return new PlayerPool(players);
    }

    public CompetitionBuilder withSelectStrategy(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
        return this;
    }

    public CompetitionBuilder withNotificationStrategy(NotificationStrategy notificationStrategy) {
        this.notificationStrategy = notificationStrategy;
        return this;
    }

    public CompetitionBuilder withPlayerPool(Player[] pool) {
        players = pool;
        return this;
    }

    public CompetitionBuilder withContactPreference(ContactPreference preference) {
        notificationStrategy = new BasicNotificationStrategy(7, 15, 4, preference);
        return this;
    }

    public Competition build() {
        return new Competition(DEFAULT_COMPETITION_NAME,
                standardSchedule(DEFAULT_COMPETITION_NAME),
                standardPlayerPool(DEFAULT_COMPETITION_NAME),
                selectionStrategy, notificationStrategy, teamCaptain);
    }

}
