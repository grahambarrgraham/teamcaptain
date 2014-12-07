package org.rrabarg.teamcaptain.domain;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PoolOfPlayers {

    @Id
    private String id;

    Collection<Player> players;

    /**
     * Used for entity creation
     */
    public PoolOfPlayers(Player... players) {
        this.players = Arrays.asList(players);
    }

    /**
     * Used for load from persistent store
     */
    public PoolOfPlayers(String poolId, Collection<Player> players) {
        this.players = players;
        this.id = poolId;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pool " + players;
    }
}
