package org.rrabarg.teamcaptain.domain;


public class Competition {

    private final Schedule schedule;
    private final PoolOfPlayers playerPool;

    public Competition(Schedule schedule, PoolOfPlayers playerPool) {
        super();
        this.schedule = schedule;
        this.playerPool = playerPool;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public PoolOfPlayers getPlayerPool() {
        return playerPool;
    }

}
