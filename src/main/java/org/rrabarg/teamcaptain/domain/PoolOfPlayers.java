package org.rrabarg.teamcaptain.domain;

import java.util.Arrays;
import java.util.Collection;

public class PoolOfPlayers {

    Collection<Player> players;

    public PoolOfPlayers(Collection<Player> players) {
        this.players = players;
    }

    public PoolOfPlayers(Player... players) {
        this.players = Arrays.asList(players);
    }

    public Collection<Player> getPlayers() {
        return players;
    }

}
