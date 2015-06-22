package org.rrabarg.teamcaptain.strategy;

import org.codehaus.jackson.annotate.JsonProperty;
import org.rrabarg.teamcaptain.domain.NotificationStrategy;

public class BasicNotificationStrategy implements NotificationStrategy {
    private final int daysTillMatchForReminders;
    private final int daysTillMatchForNotifications;
    private final int daysTillMatchForStandbys;
    private final int daysTillMatchForStatusUpdate;
    private final ContactPreference contactPreference;
    private final int daysTillMatchForAuthStandbySelection;

    public BasicNotificationStrategy(
            @JsonProperty("daysTillMatchForNotifications") int daysTillMatchTillWindowOpen,
            @JsonProperty("daysTillMatchForReminders") int daysTillMatchForReminders,
            @JsonProperty("daysTillMatchForStandbys") int daysTillMatchForStandbys,
            @JsonProperty("daysTillMatchForStatusUpdate") int daysTillMatchTillStatusUpdate,
            @JsonProperty("daysTillMatchForAutoStandbySelection") int daysTillMatchForAutoStandbySelection,
            @JsonProperty("contactPreference") ContactPreference contactPreference) {
        this.daysTillMatchForReminders = daysTillMatchForReminders;
        this.daysTillMatchForNotifications = daysTillMatchTillWindowOpen;
        this.daysTillMatchForStandbys = daysTillMatchForStandbys;
        this.daysTillMatchForStatusUpdate = daysTillMatchTillStatusUpdate;
        this.daysTillMatchForAuthStandbySelection = daysTillMatchForAutoStandbySelection;
        this.contactPreference = contactPreference;
    }

    @Override
    public int getDaysTillMatchForStatusUpdate() {
        return daysTillMatchForStatusUpdate;
    }

    @Override
    public int getDaysTillMatchForReminders() {
        return daysTillMatchForReminders;
    }

    @Override
    public int getDaysTillMatchForNotifications() {
        return daysTillMatchForNotifications;
    }

    @Override
    public long getDaysTillMatchForStandbys() {
        return daysTillMatchForStandbys;
    }

    @Override
    public ContactPreference getContactPreference() {
        return contactPreference;
    }

    @Override
    public int getDaysTillMatchForAutoStandbySelection() {
        return daysTillMatchForAuthStandbySelection;
    }

}
