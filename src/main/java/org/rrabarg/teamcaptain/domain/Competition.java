package org.rrabarg.teamcaptain.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.rrabarg.teamcaptain.SelectionStrategy;

public class Competition {

    private final String name;
    private final Schedule schedule;
    private final PoolOfPlayers playerPool;
    private final SelectionStrategy selectionStrategy;

    public Competition(String name, Schedule schedule, PoolOfPlayers playerPool, SelectionStrategy strategy) {
        this.schedule = schedule;
        this.playerPool = playerPool;
        this.name = name;
        selectionStrategy = strategy;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public PoolOfPlayers getPlayerPool() {
        return playerPool;
    }

    public String getName() {
        return name;
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
