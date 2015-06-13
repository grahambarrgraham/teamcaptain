package org.rrabarg.teamcaptain.domain;

import org.rrabarg.teamcaptain.strategy.ContactPreference;

public interface NotificationStrategy {

    int getDaysTillMatchForReminders();

    int getDaysTillMatchForNotifications();

    long getDaysTillMatchForStandbys();

    ContactPreference getContactPreference();

    int getDaysTillMatchForStatusUpdate();

    int getDaysTillMatchForAutoStandbySelection();
}