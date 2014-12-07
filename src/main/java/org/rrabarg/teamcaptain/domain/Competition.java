package org.rrabarg.teamcaptain.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Competition {

    private final String name;
    private final Schedule schedule;
    private final PoolOfPlayers playerPool;

    public Competition(String name, Schedule schedule, PoolOfPlayers playerPool) {
        this.schedule = schedule;
        this.playerPool = playerPool;
        this.name = name;
        this.schedule.setCompetition(this);
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
