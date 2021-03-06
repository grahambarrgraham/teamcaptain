package org.rrabarg.teamcaptain.domain;

import java.util.Collection;

public class Team extends PlayerPool {

    public Team(String poolId, Collection<Player> players) {
        super(poolId, players);
    }

    @Override
    public String toString() {
        return "Team " + players;
    }

}
