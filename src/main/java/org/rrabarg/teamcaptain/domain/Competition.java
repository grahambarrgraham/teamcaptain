package org.rrabarg.teamcaptain.domain;

import javax.persistence.Id;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.rrabarg.teamcaptain.SelectionStrategy;

public class Competition {

    @Id
    private String id;

    private final String name;
    private final Schedule schedule;
    private final PlayerPool playerPool;
    private final SelectionStrategy selectionStrategy;
    private final TeamCaptain teamCaptain;

    public Competition(String name, Schedule schedule, PlayerPool playerPool, SelectionStrategy strategy,
            TeamCaptain teamCaptain) {
        this.schedule = schedule;
        this.playerPool = playerPool;
        this.name = name;
        selectionStrategy = strategy;
        this.teamCaptain = teamCaptain;
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
        return teamCaptain;
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
