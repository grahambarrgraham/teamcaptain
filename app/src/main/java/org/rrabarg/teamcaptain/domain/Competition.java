package org.rrabarg.teamcaptain.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Competition {

    private String id;

    private final String name;
    private final Schedule schedule;
    private final PlayerPool playerPool;
    private final SelectionStrategy selectionStrategy;
    private final NotificationStrategy notificationStrategy;

    public Competition(String name, Schedule schedule,
            PlayerPool playerPool,
            SelectionStrategy selectStrategy,
            NotificationStrategy notificationStrategy) {
        this.schedule = schedule;
        this.playerPool = playerPool;
        this.name = name;
        this.selectionStrategy = selectStrategy;
        this.notificationStrategy = notificationStrategy;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public PlayerPool getPlayerPool() {
        return playerPool;
    }

    public String getName() {
        return name;
    }

    public TeamCaptain getTeamCaptain() {
        return getPlayerPool().getTeamCaptain();
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    public NotificationStrategy getNotificationStrategy() {
        return notificationStrategy;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
