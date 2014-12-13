package org.rrabarg.teamcaptain.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PoolOfPlayers {

    @Id
    private String id;

    Map<String, Player> players;

    /**
     * Used for entity creation
     */
    public PoolOfPlayers(Player... players) {
        this.players = createMap(Arrays.asList(players));
    }

    /**
     * Used for load from persistent store
     */
    public PoolOfPlayers(String poolId, Collection<Player> players) {
        this.players = createMap(players);
        this.id = poolId;
    }

    public Collection<Player> getPlayers() {
        return players.values();
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

    public Player getPlayerForKey(String k) {
        return players.get(k);
    }

    private Map<String, Player> createMap(Collection<Player> players) {
        final Map<String, Player> result =
                players.stream().collect(Collectors.toMap(Player::getKey,
                        Function.<Player> identity()));
        return result;
    }
}
