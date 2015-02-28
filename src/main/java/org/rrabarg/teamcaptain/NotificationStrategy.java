package org.rrabarg.teamcaptain;

import org.rrabarg.teamcaptain.strategy.ContactPreference;

public interface NotificationStrategy {

    int getDaysTillMatchForReminders();

    int getDaysTillMatchForNotifications();

    long getDaysTillMatchForStandbys();

    ContactPreference getContactPreference();
}