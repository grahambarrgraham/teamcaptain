package org.rrabarg.teamcaptain.strategy;

import org.codehaus.jackson.annotate.JsonProperty;
import org.rrabarg.teamcaptain.NotificationStrategy;

public class BasicNotificationStrategy implements NotificationStrategy {
    private final int daysTillMatchForReminders;
    private final int daysTillMatchForNotifications;
    private final int daysTillMatchForStandbys;
    private final int daysTillMatchForStatus;
    private final ContactPreference contactPreference;

    public BasicNotificationStrategy(
            @JsonProperty("daysTillMatchForReminders") int daysTillMatchForReminders,
            @JsonProperty("daysTillMatchForNotifications") int daysTillMatchForNotifications,
            @JsonProperty("daysTillMatchForStandbys") int daysTillMatchForStandbys,
            @JsonProperty("daysTillMatchTillStatusUpdate") int daysTillMatchTillStatusUpdate,
            @JsonProperty("contactPreference") ContactPreference contactPreference) {
        this.daysTillMatchForReminders = daysTillMatchForReminders;
        this.daysTillMatchForNotifications = daysTillMatchForNotifications;
        this.daysTillMatchForStandbys = daysTillMatchForStandbys;
        this.daysTillMatchForStatus = daysTillMatchTillStatusUpdate;
        this.contactPreference = contactPreference;
    }

    @Override
    public int getDaysTillMatchForStatusUpdate() {
        return daysTillMatchForStatus;
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
