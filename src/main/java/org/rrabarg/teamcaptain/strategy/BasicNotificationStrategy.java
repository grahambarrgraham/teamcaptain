package org.rrabarg.teamcaptain.strategy;

import org.codehaus.jackson.annotate.JsonProperty;
import org.rrabarg.teamcaptain.NotificationStrategy;

public class BasicNotificationStrategy implements NotificationStrategy {
    private final int daysTillMatchForReminders;
    private final int daysTillMatchForNotifications;
    private final int daysTillMatchForStandbys;
    private final ContactPreference contactPreference;

    public BasicNotificationStrategy(
            @JsonProperty("daysTillMatchForReminders") int daysTillMatchForReminders,
            @JsonProperty("daysTillMatchForNotifications") int daysTillMatchForNotifications,
            @JsonProperty("daysTillMatchForStandbys") int daysTillMatchForStandbys,
            @JsonProperty("contactPreference") ContactPreference contactPreference) {
        this.daysTillMatchForReminders = daysTillMatchForReminders;
        this.daysTillMatchForNotifications = daysTillMatchForNotifications;
        this.daysTillMatchForStandbys = daysTillMatchForStandbys;
        this.contactPreference = contactPreference;
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

}
