package org.rrabarg.teamcaptain.domain;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.rrabarg.teamcaptain.strategy.ContactPreference;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public interface NotificationStrategy {

    int getDaysTillMatchForReminders();

    int getDaysTillMatchForNotifications();

    long getDaysTillMatchForStandbys();

    ContactPreference getContactPreference();

    int getDaysTillMatchForStatusUpdate();

    int getDaysTillMatchForAutoStandbySelection();
}