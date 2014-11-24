package org.rrabarg.teamcaptain.domain;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_FIELD_NAMES_STYLE);
    }
}
